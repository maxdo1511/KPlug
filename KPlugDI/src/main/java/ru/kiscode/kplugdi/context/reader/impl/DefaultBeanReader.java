package ru.kiscode.kplugdi.context.reader.impl;

import lombok.NonNull;
import ru.kiscode.kplugdi.annotations.CustomBeanName;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ComponentBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ConfigurationBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;
import ru.kiscode.kplugdi.context.reader.BeanReader;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class DefaultBeanReader implements BeanReader {
    @Override
    public Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull BeanFactory beanFactory) {
        if(beanDefinition instanceof PluginBeanDefinition){
            return ((PluginBeanDefinition) beanDefinition).getPluginInstance();
        }
        if(beanDefinition instanceof ComponentBeanDefinition){
            ComponentBeanDefinition componentBeanDefinition = (ComponentBeanDefinition) beanDefinition;
            return ReflectionUtil.newInstance(componentBeanDefinition.getBeanClass());
        }
        if(beanDefinition instanceof ConfigurationBeanDefinition){
            ConfigurationBeanDefinition configurationBeanDefinition = (ConfigurationBeanDefinition) beanDefinition;
            Method method = configurationBeanDefinition.getConfigurationMethod();
            Object[] objects = new Object[method.getParameterCount()];
            Object configObject = ReflectionUtil.newInstance(method.getDeclaringClass());
            Parameter[] parameters = method.getParameters();
            for(int i = 0; i < parameters.length; i++){
                Parameter parameter = parameters[i];
                Object parameterBean = parameter.isAnnotationPresent(CustomBeanName.class) &&
                        !parameter.getAnnotation(CustomBeanName.class).name().isEmpty() ?
                        beanFactory.getBean(parameter.getAnnotation(CustomBeanName.class).name()) : beanFactory.getBean(parameter.getType());
                objects[i] = parameterBean;
            }
            method.setAccessible(true);
            try {
                return method.invoke(configObject, objects);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanCreatingException("Can't create @Bean bean << %s >>", e, beanDefinition.getClass().getName());
            }
        }
        return null;
    }
}
