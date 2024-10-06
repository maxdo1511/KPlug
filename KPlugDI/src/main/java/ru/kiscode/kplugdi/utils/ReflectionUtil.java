package ru.kiscode.kplugdi.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtil {
    private final ScanResult classGraph;

    /**
     * Constructs a {@link ReflectionUtil} instance that initializes a {@link ClassGraph}
     * object to scan the plugin package.
     *
     * @param plugin The plugin instance used to define the package to scan.
     */
    public ReflectionUtil(@NonNull JavaPlugin plugin) {
        classGraph = new ClassGraph()
                .addClassLoader(plugin.getClass().getClassLoader())
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(plugin.getClass().getPackage().getName())
                .scan();
    }

    /**
     * Constructs a {@link ReflectionUtil} instance that initializes a {@link ClassGraph}
     * object to scan the specified path with the provided class loader.
     *
     * @param path       The path to scan for classes.
     * @param classLoader The class loader to be used for loading classes.
     */
    public ReflectionUtil(@NonNull String path, @NonNull ClassLoader classLoader) {
        classGraph = new ClassGraph()
                .addClassLoader(classLoader)
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPaths(path)
                .scan();
    }

    /**
     * Returns a list of all classes scanned by the class graph.
     *
     * @return A list of all classes.
     */
    public List<Class<?>> getAllClasses() {
        return classGraph.getAllClasses().loadClasses();
    }

    /**
     * Returns a list of fields in the specified class that are annotated with the given annotation.
     *
     * @param clazz          The class whose fields are to be checked.
     * @param annotation     The annotation that fields are checked against.
     * @param checkSuperClass Whether to check the superclass of the class.
     * @return A list of fields annotated with the specified annotation.
     */
    public static List<Field> getAllFieldsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation, boolean checkSuperClass) {
        return getAllFields(clazz, checkSuperClass).stream()
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of methods in the specified class that are annotated with the given annotation.
     *
     * @param clazz          The class whose methods are to be checked.
     * @param annotation     The annotation that methods are checked against.
     * @param checkSuperClass Whether to check the superclass of the class.
     * @return A list of methods annotated with the specified annotation.
     */
    public static List<Method> getAllMethodsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation, boolean checkSuperClass) {
        return getAllMethods(clazz, checkSuperClass).stream()
                .filter(m -> m.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all plugin classes annotated with the specified annotation.
     *
     * @param annotation The annotation that classes are checked against.
     * @return A list of classes annotated with the specified annotation.
     */
    public List<Class<?>> getClassesAnnotatedWith(@NonNull Class<? extends Annotation> annotation) {
        return classGraph.getClassesWithAnnotation(annotation.getName()).loadClasses();
    }

    /**
     * Creates a new instance of the specified class, attempting to match constructor parameters.
     *
     * @param clazz The class to instantiate.
     * @param args  The arguments to pass to the constructor.
     * @return A new instance of the specified class.
     */
    public static Object newInstance(@NonNull Class<?> clazz, Object... args) {
        try {
            Constructor<?> constructor = findConstructor(clazz, args);
            System.out.println(constructor.getParameterCount());
            if (constructor.getParameterCount() == 0) {
                return constructor.newInstance();
            }
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreatingException("Error creating class << %s >>. Must have a public empty constructor.", e, clazz.getName());
        }
    }

    /**
     * Finds a suitable constructor in the specified class that matches the given arguments.
     *
     * @param clazz The class to search for a constructor.
     * @param args  The arguments to match against the constructor parameters.
     * @return A constructor that matches the given arguments.
     */
    private static Constructor<?> findConstructor(@NonNull Class<?> clazz, Object... args) {
        int len = args == null ? 0 : args.length;
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0 || constructor.getParameterCount() == len) {
                return constructor;
            }
        }
        throw new BeanCreatingException("Error creating class << %s >>. Must have an empty constructor or a constructor with %s arguments.", clazz.getName(), len);
    }

    /**
     * Returns all fields declared in the specified class, optionally including those from superclasses.
     *
     * @param clazz         The class to retrieve fields from.
     * @param checkSuperClass Whether to include fields from the superclass.
     * @return A list of fields in the specified class.
     */
    public static List<Field> getAllFields(@NonNull Class<?> clazz, boolean checkSuperClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (checkSuperClass) {
            Class<?> superClass = clazz.getSuperclass();
            while (superClass != null) {
                fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
                superClass = superClass.getSuperclass();
            }
        }
        return fields;
    }

    /**
     * Returns all methods declared in the specified class, optionally including those from superclasses.
     *
     * @param clazz          The class to retrieve methods from.
     * @param checkSuperClass Whether to include methods from the superclass.
     * @return A list of methods in the specified class.
     */
    public static List<Method> getAllMethods(@NonNull Class<?> clazz, boolean checkSuperClass) {
        List<Method> methods = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
        if (checkSuperClass) {
            Class<?> superClass = clazz.getSuperclass();
            while (superClass != null) {
                methods.addAll(Arrays.asList(superClass.getDeclaredMethods()));
                superClass = superClass.getSuperclass();
            }
        }
        return methods;
    }

    /**
     * Checks if the specified element has the specified annotation.
     *
     * @param element The element to check.
     * @param targetAnnotation The annotation to check for.
     * @return {@code true} if the element has the specified annotation; {@code false} otherwise.
     */
    public static boolean hasAnnotation(@NonNull AnnotatedElement element, @NonNull Class<? extends Annotation> targetAnnotation) {
        return hasComponentAnnotation(element, targetAnnotation, new HashSet<>());
    }

    private static boolean hasComponentAnnotation(AnnotatedElement element, @NonNull Class<? extends Annotation> targetAnnotation, Set<Class<?>> visited) {
        // Проверяем, есть ли аннотация targetAnnotation на текущем элементе
        if (element.isAnnotationPresent(targetAnnotation)) {
            return true;
        }

        // Получаем все аннотации текущего элемента
        Annotation[] annotations = element.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<?> annotationType = annotation.annotationType();
            // Проверяем, не проверяли ли мы эту аннотацию ранее
            if (!visited.contains(annotationType)) {
                visited.add(annotationType); // Добавляем аннотацию в множество проверенных
                // Рекурсивно проверяем аннотацию
                if (hasComponentAnnotation(annotationType, targetAnnotation, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the specified class has the specified interface or superclass.
     *
     * @param clazz  The class to check.
     * @param aClass The interface or superclass to check for.
     * @return {@code true} if the class has the specified interface or superclass; {@code false} otherwise.
     */
    public static boolean hasInterfaceOrSuperClass(Class<?> clazz, Class<?> aClass) {
        if (clazz == null || aClass == null) return false;

        if (hasInterface(clazz, aClass)) return true;

        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null) {
            if (superClass.equals(aClass) || hasInterface(superClass, aClass)) return true;
            superClass = superClass.getSuperclass();
        }

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

    /**
     * Checks if the specified class implements the given interface.
     *
     * @param clazz  The class to check.
     * @param aClass The interface to check for.
     * @return {@code true} if the class implements the specified interface; {@code false} otherwise.
     */
    public static boolean hasInterface(@NonNull Class<?> clazz, @NonNull Class<?> aClass) {
        return Arrays.asList(clazz.getInterfaces()).contains(aClass);
    }

    /**
     * Checks if the specified class is an abstract or an interface.
     * @param clazz The class to check.
     * @return {@code true} if the class is an abstract or an interface; {@code false} otherwise.
     */
    public static boolean isAbstractOrInterface(@NonNull Class<?> clazz) {
        return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
    }
}
