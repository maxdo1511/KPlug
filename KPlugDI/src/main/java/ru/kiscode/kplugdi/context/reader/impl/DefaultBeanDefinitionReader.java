package ru.kiscode.kplugdi.context.reader.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.*;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ApplicationContextBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ComponentBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ConfigurationBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultBeanDefinitionReader implements BeanDefinitionReader {

    private final JavaPlugin plugin;

    public DefaultBeanDefinitionReader(@NonNull JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public Set<BeanDefinition> createBeanDefinition(@NonNull Class<?> clazz){
        ReflectionUtil.isInterfaceOrAbstract(clazz);
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        if(clazz.isAnnotationPresent(Component.class)){
            BeanDefinition beanDefinition = getComponentBeanDefinition(clazz);
            beanDefinitions.add(beanDefinition);
        }
        if(clazz.isAnnotationPresent(BeanConfiguration.class)){
            for(BeanDefinition beanDefinition: getConfigurationBeanDefinition(clazz)){
                if(!beanDefinitions.add(beanDefinition)){
                    throw new BeanCreatingException("bean with name << %s >> already exists in context. Please change bean name", beanDefinition.getName());
                }
            }
        }
        return beanDefinitions;
    }

    private BeanDefinition getComponentBeanDefinition(@NonNull Class<?> clazz){
        String componentName = clazz.isAnnotationPresent(CustomBeanName.class) &&
                !clazz.getAnnotation(CustomBeanName.class).name().isEmpty() ?
                clazz.getAnnotation(CustomBeanName.class).name() : clazz.getName();
        Set<Class<?>> implementInterfaces = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        Class<?> superClass = clazz.getSuperclass();
        if(superClass != null) implementInterfaces.add(superClass);
        return ComponentBeanDefinition.builder()
                .name(componentName)
                .beanClass(clazz)
                .implementInterfaces(implementInterfaces)
                .scope(getScopeType(clazz))
                .build();
    }

    private Set<BeanDefinition> getConfigurationBeanDefinition(@NonNull Class<?> clazz){
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for(Method method: ReflectionUtil.getAllMethods(clazz,false)){
            ReflectionUtil.isStatic(method,Bean.class);
            ReflectionUtil.checkReturnType(method,false,Bean.class);
            String beanName = method.isAnnotationPresent(CustomBeanName.class) &&
                    !method.getAnnotation(CustomBeanName.class).name().isEmpty() ?
                    method.getAnnotation(CustomBeanName.class).name() : plugin.getName() + "." + method.getName();
            beanDefinitions.add(ConfigurationBeanDefinition.builder()
                    .name(beanName)
                    .beanClass(method.getReturnType())
                    .implementInterfaces(new HashSet<>(Arrays.asList(method.getReturnType().getInterfaces())))
                    .scope(getScopeType(method.getReturnType()))
                    .configurationMethod(method)
                    .build());
        }
        return beanDefinitions;
    }

    private BeanDefinition getApplicationContextBeanDefinition() {
        return ApplicationContextBeanDefinition.builder()
                .applicationContext(ApplicationContext.getApplicationContext())
                .name(ApplicationContext.class.getName())
                .beanClass(ApplicationContext.class)
                .implementInterfaces(new HashSet<>(Arrays.asList(ApplicationContext.class.getInterfaces())))
                .scope("singleton")
                .build();
    }

    private String getScopeType(@NonNull Class<?> clazz){
        if(clazz.isAnnotationPresent(Scope.class)){
            return clazz.getAnnotation(Scope.class).value();
        }
        return "singleton";
    }
}
