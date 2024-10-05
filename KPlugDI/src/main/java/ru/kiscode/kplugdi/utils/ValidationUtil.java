package ru.kiscode.kplugdi.utils;

import lombok.NonNull;
import ru.kiscode.kplugdi.annotations.Qualifier;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ValidationUtil {

    private ValidationUtil(){
    }

    public static void validateReturnType(@NonNull Method method, boolean shouldBeVoid, @NonNull Class<? extends Annotation> annotation) {
        if ((shouldBeVoid && method.getReturnType() != void.class) || (!shouldBeVoid && method.getReturnType() == void.class)) {
            throw new BeanCreatingException("%s method << %s >> in class << %s >> should %s be void.",
                    annotation.getSimpleName(), method.getName(), method.getDeclaringClass().getName(), shouldBeVoid ? "" : "not");
        }
    }

    public static void validateStaticMethod(@NonNull Method method, @NonNull Class<? extends Annotation> annotation) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new BeanCreatingException("%s method << %s >> in class << %s >> cannot be static.", annotation.getSimpleName(), method.getName(), method.getDeclaringClass().getName());
        }
    }

    public static void validateStaticField(@NonNull Field field, @NonNull Class<? extends Annotation> annotation) {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new BeanCreatingException("%s field << %s >> cannot be static.", annotation.getSimpleName(), field.getName());
        }
    }

    public static void validateClassIfAbstractOrInterface(@NonNull Class<?> clazz){
        if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())){
            throw new BeanCreatingException("Class << %s >> cannot be interface or abstract", clazz.getName());
        }
    }

    public static String validateQualifier(@NonNull AnnotatedElement annotatedElement, @NonNull String defaultName) {
        if(annotatedElement.isAnnotationPresent(Qualifier.class)){
            String beanName = annotatedElement.getAnnotation(Qualifier.class).name();
            if(beanName.isEmpty()){
                return defaultName;
            }
            return beanName;
        }
        return defaultName;
    }
}
