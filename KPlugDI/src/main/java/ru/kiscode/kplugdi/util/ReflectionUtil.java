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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ReflectionUtil {
    private final Reflections reflections;


    public ReflectionUtil(@NonNull JavaPlugin plugin) {
        reflections = new Reflections(new ConfigurationBuilder().forPackage(plugin.getClass().getPackage().getName(),
                plugin.getClass().getClassLoader()).setScanners(Scanners.values()));
    }


    public ReflectionUtil(@NonNull String packageName, @NonNull ClassLoader classLoader) {
        reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName, classLoader).setScanners(Scanners.values()));
    }

    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreatingException("error creating class <%s>. The class must have a public empty constructor, not be an interface or abstract class", clazz.getName());
        }
    }

    public Set<Class<?>> getAllClasses() {
        return reflections.getSubTypesOf(Object.class);
    }


    public static List<Field> getFieldsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    public Set<Class<?>> getClassesAnnotatedWith(@NonNull Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }

    public <I> Set<Class<? extends I>> getImplementingClassesThroughSubclasses(@NonNull Class<I> interfaceClass) {
        return reflections.getSubTypesOf(interfaceClass);
    }

    public static List<Method> getMethodsAnnotatedWith(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }
}
