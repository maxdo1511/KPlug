package ru.kiscode.kplugdi.context.reader.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Qualifier;
import ru.kiscode.kplugdi.context.model.impl.ApplicationContextBeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
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
    public Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull BeanRegistry beanRegistry, @NonNull JavaPlugin plugin) {
        if(beanDefinition instanceof PluginBeanDefinition){
            return ((PluginBeanDefinition) beanDefinition).getPluginInstance();
        }
        if (beanDefinition instanceof ApplicationContextBeanDefinition){
            return ((ApplicationContextBeanDefinition) beanDefinition).getApplicationContext();
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
                Object parameterBean = ReflectionUtil.hasAnnotation(parameter, Qualifier.class) &&
                        !parameter.getAnnotation(Qualifier.class).name().isEmpty() ?
                        beanRegistry.getBean(parameter.getAnnotation(Qualifier.class).name(),plugin) : beanRegistry.getBean(parameter.getType(),plugin);
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
