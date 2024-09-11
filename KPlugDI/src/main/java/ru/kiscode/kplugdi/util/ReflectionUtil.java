package ru.kiscode.kplugdi.util;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for handling reflection in the plugin context.
 * Provides methods to find and process annotated elements within a plugin.
 */
public class ReflectionUtil {
    private final Reflections reflections;

    /**
     * Constructor that initializes a {@link Reflections} object to scan the plugin package.
     *
     * @param plugin The plugin instance used to define the package to scan.
     */
    public ReflectionUtil(@NonNull JavaPlugin plugin) {
        reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(plugin.getClass().getPackage().getName(), plugin.getClass().getClassLoader())
                        .setScanners(Scanners.values()));
    }
    
    public ReflectionUtil(@NonNull String packageName, @NonNull ClassLoader classLoader) {
        reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader).setScanners(Scanners.values()));
    }

    public Set<Class<?>> getAllClasses() {
        return reflections.getSubTypesOf(Object.class);
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
     * Returns a set of all plugin classes annotated with the specified annotation.
     *
     * @param annotation The annotation that classes are checked against.
     * @return A set of classes annotated with the specified annotation.
     */
    public Set<Class<?>> getClassesAnnotatedWith(@NonNull Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }

    /**
     * Returns a set of classes that implement the specified interface.
     *
     * @param interfaceClass The interface whose implementations are to be found.
     * @param <I>            The type of the interface.
     * @return A set of classes that implement the specified interface.
     */
    public <I> Set<Class<? extends I>> getImplementingClassesThroughSubclasses(@NonNull Class<I> interfaceClass) {
        return reflections.getSubTypesOf(interfaceClass);
    }

    /**
     * Returns a list of methods annotated with the specified annotation.
     *
     * @param clazz      The class whose methods are to be checked.
     * @param annotation The annotation that methods are checked against.
     * @return A list of methods annotated with the specified annotation.
     */
    public static List<Method> getMethodsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the element is static.
     *
     * @param modifiers The element's modifiers.
     * @return {@code true} if the element is static, {@code false} otherwise.
     */
    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    /**
     * Checks if the method has exactly one parameter.
     *
     * @param method The method to be checked.
     * @return {@code true} if the method does not have exactly one parameter, {@code false} otherwise.
     */
    public static boolean isNotOneParameter(@NonNull Method method) {
        return method.getParameterCount() != 1;
    }

    /**
     * Checks if the method returns {@code void}.
     *
     * @param method The method to be checked.
     * @return {@code true} if the method returns {@code void}, {@code false} otherwise.
     */
    public static boolean isVoidMethod(@NonNull Method method) {
        return method.getReturnType() == void.class;
    }

    /**
     * Validates a method for injection.
     *
     * @param method The method to be validated.
     * @param clazz  The class containing the method.
     * @throws BeanCreatingException if the method is static or has incorrect parameters.
     */
    public static void validateInjectMethod(@NonNull Method method, @NonNull Class<?> clazz) {
        if (isStatic(method.getModifiers())) {
            throw new BeanCreatingException(ErrorMessages.OBJECT_STATIC, "method", method.getName(), clazz.getName());
        }
        if (isNotOneParameter(method)) {
            throw new BeanCreatingException(ErrorMessages.METHOD_PARAMETER_ERROR, method.getName(), clazz.getName());
        }
    }

    /**
     * Generates a unique identifier for a method annotated with {@code @Bean}.
     *
     * @param clazz  The class containing the method.
     * @param method The name of the method.
     * @return A string representation of the method's path.
     */
    public static String generatePath(@NonNull Class<?> clazz, @NonNull String method) {
        return clazz.getSimpleName() + "." + method;
    }

    /**
     * Creates an instance of a class using its default constructor.
     *
     * @param clazz The class to instantiate.
     * @return The created instance of the class.
     * @throws BeanCreatingException if the object creation fails.
     */
    public static Object createClassObject(@NonNull Class<?> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new BeanCreatingException(ErrorMessages.CONSTRUCTOR_CREATING, e, clazz.getName());
        }
    }
}
