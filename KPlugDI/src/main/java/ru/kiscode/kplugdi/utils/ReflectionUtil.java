package ru.kiscode.kplugdi.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;


public class ReflectionUtil {
    private final ScanResult classGraph;

    /**
     * Constructor that initializes a {@link ClassGraph} object to scan the plugin package.
     *
     * @param plugin The plugin instance used to define the package to scan.
     */
    public ReflectionUtil(@NonNull JavaPlugin plugin) {
        classGraph = new ClassGraph()
                .addClassLoader(plugin.getClass().getClassLoader())
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(plugin.getClass().getPackage().getName()).scan();
    }

    public ReflectionUtil(@NonNull String path, @NonNull ClassLoader classLoader) {
        classGraph = new ClassGraph()
                .addClassLoader(classLoader)
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPaths(path).scan();
    }

    public List<Class<?>> getAllClasses() {
        return classGraph.getAllClasses().loadClasses();
    }

    /**
     * Returns a list of fields annotated with the specified annotation.
     *
     * @param clazz      The class whose fields are to be checked.
     * @param annotation The annotation that fields are checked against.
     * @return A list of fields annotated with the specified annotation.
     */
    public List<Field> getFieldsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of methods annotated with the specified annotation.
     *
     * @param clazz      The class whose methods are to be checked.
     * @param annotation The annotation that methods are checked against.
     * @return A list of methods annotated with the specified annotation.
     */
    public List<Method> getMethodsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Returns a set of all plugin classes annotated with the specified annotation.
     *
     * @param annotation The annotation that classes are checked against.
     * @return A set of classes annotated with the specified annotation.
     */
    public List<Class<?>> getClassesAnnotatedWith(@NonNull Class<? extends Annotation> annotation) {
        return classGraph.getClassesWithAnnotation(annotation.getName()).loadClasses();
    }

    /**
     * Checks if the element is static.
     *
     * @param modifiers The element's modifiers.
     * @return {@code true} if the element is static, {@code false} otherwise.
     */
    public boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    /**
     * Checks if the method has exactly one parameter.
     *
     * @param method The method to be checked.
     * @return {@code true} if the method does not have exactly one parameter, {@code false} otherwise.
     */
    public boolean isNotOneParameter(@NonNull Method method) {
        return method.getParameterCount() != 1;
    }

    /**
     * Checks if the method returns {@code void}.
     *
     * @param method The method to be checked.
     * @return {@code true} if the method returns {@code void}, {@code false} otherwise.
     */
    public boolean isVoidMethod(@NonNull Method method) {
        return method.getReturnType() == void.class;
    }

    public static Object newInstance(@NonNull Class<?> clazz, Object... args) {
        try {
            Constructor<?> constructor = null;
            int len = args == null ? 0 : args.length;
            for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
                if (declaredConstructor.getParameterCount() == 0) {
                    constructor = declaredConstructor;
                    break;
                } else if (declaredConstructor.getParameterCount() == len) {
                    constructor = declaredConstructor;
                }
            }
            if (constructor == null) {
                throw new BeanCreatingException("Error creating class << %s >>. Must have a empty constructor or a constructor with %s arguments.", clazz.getName(), len);
            }
            if (constructor.getParameterCount() > 0) {
                return constructor.newInstance(args);
            } else {
                return constructor.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreatingException("Error creating class << %s >>. Must have a public empty constructor.",e, clazz.getName());
        }
    }

    public static void isStatic(@NonNull Field field, @NonNull Class<? extends Annotation> annotation) {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new BeanCreatingException("%s field << %s >> cannot be static.",annotation.getSimpleName(),field.getName());
        }
    }

    public static List<Field> getAllFields(@NonNull Class<?> clazz, boolean checkSuperClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && checkSuperClass) {
            fields.addAll(getAllFields(superClass, true));
        }
        return fields;
    }

    public static List<Method> getAllMethods(@NonNull Class<?> clazz, boolean checkSuperClass) {
        List<Method> methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && checkSuperClass) {
            methods.addAll(getAllMethods(superClass, true));
        }
        return methods;
    }

    public static boolean hasAnnotation(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    public static boolean hasAnnotation(@NonNull Method method, @NonNull Class<? extends Annotation> annotation) {
        return method.isAnnotationPresent(annotation);
    }
    public static void isStatic(@NonNull Method method, @NonNull Class<? extends Annotation> annotation) {
        if(Modifier.isStatic(method.getModifiers())){
            throw new BeanCreatingException("%s method << %s >> in class << %s >> cannot be static.",annotation.getSimpleName(),method.getName(),method.getDeclaringClass().getName());
        }
    }

    public static void checkReturnType(@NonNull Method method, boolean shouldBeVoid, @NonNull Class<? extends Annotation> annotation) {
        if(shouldBeVoid && method.getReturnType() != void.class){
            throw new BeanCreatingException("%s method << %s >> in class << %s >> should be void. method.",annotation.getSimpleName(),method.getName(), method.getDeclaringClass().getName());
        }
        if(!shouldBeVoid && method.getReturnType() == void.class){
            throw new BeanCreatingException("%s method << %s >> in class << %s >> should not be void. method.",annotation.getSimpleName(),method.getName(), method.getDeclaringClass().getName());
        }
    }

    public static void multiplyParameters(@NonNull Method method){
        if(method.getParameterCount() != 1){
            throw new BeanCreatingException("@Autowired method << %s >> in class << %s >> should have one parameter.",method.getName(), method.getDeclaringClass().getName());
        }
    }

    public static void isInterfaceOrAbstract(@NonNull Class<?> clazz){
        if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())){
            throw new BeanCreatingException("class << %s >> can't be interface or abstract", clazz.getName());
        }
    }

    public static boolean hasInterfaceOrSuperClass(@NonNull Class<?> clazz, @NonNull Class<?> aClass) {
        // Проверка на null
        if (clazz == null || aClass == null) {
            return false;
        }

        // Проверка, реализует ли класс интерфейс
        if (hasInterface(clazz, aClass)) {
            return true; // Если нашли нужный интерфейс
        }

        // Проверка на суперклассы
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null) {
            if (superClass.equals(aClass) || hasInterface(superClass, aClass)) {
                return true; // Если нашли нужный класс или интерфейс в иерархии
            }
            superClass = superClass.getSuperclass(); // Переходим к родительскому классу
        }

        // Проверка интерфейсов
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (interfaceClass.equals(aClass) || hasInterfaceOrSuperClass(interfaceClass, aClass)) {
                return true; // Если нашли нужный интерфейс
            }
            // Рекурсивно проверяем интерфейсы, которые наследует текущий интерфейс
            for (Class<?> superInterface : interfaceClass.getInterfaces()) {
                if (hasInterfaceOrSuperClass(superInterface, aClass)) {
                    return true;
                }
            }
        }

        return false; // Если ничего не найдено
    }

    // Метод для проверки, реализует ли класс интерфейс
    public static boolean hasInterface(@NonNull Class<?> clazz, @NonNull Class<?> aClass) {
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (interfaceClass.equals(aClass)) {
                return true;
            }
        }
        return false;
    }
}

