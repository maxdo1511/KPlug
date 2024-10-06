package ru.kiscode.kplugdi.context.reader.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.*;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ComponentBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ConfigurationBeanDefinition;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;
import ru.kiscode.kplugdi.utils.ValidationUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultBeanDefinitionReader implements BeanDefinitionReader {

    @Override
    public Set<BeanDefinition> createBeanDefinition(@NonNull Class<?> clazz, @NonNull JavaPlugin plugin) {
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        if (clazz.isAnnotation()) return beanDefinitions;
        if(ReflectionUtil.hasAnnotation(clazz, Component.class)){
            ValidationUtil.validateClassIfAbstractOrInterface(clazz);
            BeanDefinition beanDefinition = getComponentBeanDefinition(clazz, plugin);
            beanDefinitions.add(beanDefinition);
        }
        if(ReflectionUtil.hasAnnotation(clazz, BeanConfiguration.class)){
            ValidationUtil.validateClassIfAbstractOrInterface(clazz);
            for(BeanDefinition beanDefinition: getConfigurationBeanDefinition(clazz, plugin)){
                if(!beanDefinitions.add(beanDefinition)){
                    throw new BeanCreatingException("bean with name << %s >> already exists in context. Please change bean name", beanDefinition.getName());
                }
            }
        }
        return beanDefinitions;
    }

    private BeanDefinition getComponentBeanDefinition(@NonNull Class<?> clazz, @NonNull JavaPlugin plugin){
        String componentName = ValidationUtil.validateQualifier(clazz,clazz.getName());
        Set<Class<?>> implementInterfaces = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) implementInterfaces.add(superClass);
        return ComponentBeanDefinition.builder()
                .name(componentName)
                .plugin(plugin)
                .beanClass(clazz)
                .implementInterfaces(implementInterfaces)
                .scope(getScopeType(clazz))
                .build();
    }

    private Set<BeanDefinition> getConfigurationBeanDefinition(@NonNull Class<?> clazz, @NonNull JavaPlugin plugin){
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for(Method method: ReflectionUtil.getAllMethodsAnnotatedWith(clazz,Bean.class,true)){
            ValidationUtil.validateStaticMethod(method,Bean.class);
            ValidationUtil.validateReturnType(method,false,Bean.class);
            String beanName = ValidationUtil.validateQualifier(method,plugin.getName() + "." + method.getName());
            beanDefinitions.add(ConfigurationBeanDefinition.builder()
                    .name(beanName)
                    .plugin(plugin)
                    .beanClass(method.getReturnType())
                    .implementInterfaces(new HashSet<>(Arrays.asList(method.getReturnType().getInterfaces())))
                    .scope(getScopeType(method.getReturnType()))
                    .configurationMethod(method)
                    .build());
        }
        return beanDefinitions;
    }


    private String getScopeType(@NonNull Class<?> clazz){
        if(ReflectionUtil.hasAnnotation(clazz,Scope.class)){
            return clazz.getAnnotation(Scope.class).value();
        }
        return "singleton";
    }
}
