package ru.kiscode.kplugdi.context.processor.beanpostprocessors;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Autowired;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private final BeanFactory beanFactory;

    public AutowiredBeanPostProcessor(@NonNull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        for(Field field: ReflectionUtil.getAllFields(bean.getClass(), true)){
            if(!field.isAnnotationPresent(Autowired.class)) continue;
            ReflectionUtil.isStatic(field);
            Autowired autowired = field.getAnnotation(Autowired.class);
            Object autowiredBean = autowired.name().isEmpty() ? beanFactory.getBean(field.getType()) : beanFactory.getBean(autowired.name());
            field.setAccessible(true);
            try {
                field.set(bean, autowiredBean);
            } catch (IllegalAccessException e) {
                throw new BeanCreatingException("Can't autowire field << %s >> in class << %s >>", e, field.getName(), bean.getClass().getName());
            }
        }
        for(Method method: ReflectionUtil.getAllMethods(bean.getClass(), true)){
            if(!method.isAnnotationPresent(Autowired.class)) continue;
            ReflectionUtil.isStatic(method);
            ReflectionUtil.checkReturnType(method,true);
            ReflectionUtil.multiplyParameters(method);
            Autowired autowired = method.getAnnotation(Autowired.class);
            Class<?> parameterClass = method.getParameterTypes()[0];
            Object autowiredBean = autowired.name().isEmpty() ? beanFactory.getBean(parameterClass) : beanFactory.getBean(autowired.name());
            method.setAccessible(true);
            try {
                method.invoke(bean, autowiredBean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanCreatingException("Can't autowire method << %s >> in class << %s >>", e, method.getName(), bean.getClass().getName());
            }
        }
        return bean;
    }
}
