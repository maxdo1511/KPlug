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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


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
            throw new BeanCreatingException("Error creating class <%s>." + "The class must have a public empty constructor, " + "not be an interface or abstract class",e, clazz.getName());
        }
    }

    public Set<Class<?>> getAllClasses() {
        return reflections.getSubTypesOf(Object.class);
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

}
