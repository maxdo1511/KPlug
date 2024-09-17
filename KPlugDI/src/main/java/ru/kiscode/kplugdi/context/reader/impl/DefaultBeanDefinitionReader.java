package ru.kiscode.kplugdi.context.reader.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Bean;
import ru.kiscode.kplugdi.annotations.BeanConfiguration;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.annotations.Scope;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ComponentBeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.ConfigurationBeanDefinition;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class DefaultBeanDefinitionReader implements BeanDefinitionReader {

    private final JavaPlugin plugin;

    public DefaultBeanDefinitionReader(@NonNull JavaPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public Set<BeanDefinition> createBeanDefinition(@NonNull Class<?> clazz){
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        if(clazz.isAnnotationPresent(Component.class)){
            validateClass(clazz);
            BeanDefinition beanDefinition = getComponentBeanDefinition(clazz, clazz.getAnnotation(Component.class).name());
            beanDefinitions.add(beanDefinition);
        }
        if(clazz.isAnnotationPresent(BeanConfiguration.class)){
            for(BeanDefinition beanDefinition: getConfigurationBeanDefinition(clazz)){
                validateClass(clazz);
                if(beanDefinitions.contains(beanDefinition)){
                    throw new BeanCreatingException("bean with name << %s >> already exists", beanDefinition.getName());
                }
                beanDefinitions.add(beanDefinition);
            }
        }
        return beanDefinitions;
    }

    private BeanDefinition getComponentBeanDefinition(@NonNull Class<?> clazz, @NonNull String componentName){
        if(componentName.isEmpty()) componentName = clazz.getName();
        return ComponentBeanDefinition.builder()
                .name(componentName)
                .beanClass(clazz)
                .implementInterfaces(clazz.getInterfaces())
                .scope(getScopeType(clazz))
                .superClass(clazz.getSuperclass())
                .build();
    }

    private Set<BeanDefinition> getConfigurationBeanDefinition(@NonNull Class<?> clazz){
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for(Method method: ReflectionUtil.getAllMethods(clazz,false)){
            if(!method.isAnnotationPresent(Bean.class)){
                throw new BeanCreatingException("method should be annotated with @PostConstruct or @PreConstruct. method: << %s >>, class: << %s >>", method.getName(), clazz.getName());
            }
            if(Modifier.isStatic(method.getModifiers())){
                throw new BeanCreatingException("@Bean method can't be static. method: << %s >>, class: << %s >>", method.getName(), clazz.getName());
            }
            if(method.getReturnType() == void.class){
                throw new BeanCreatingException("@Bean method can't be void. method: << %s >>, class: << %s >>", method.getName(), clazz.getName());
            }
            String beanName = method.getAnnotation(Bean.class).name();
            if(beanName.isEmpty()) beanName = plugin.getName()+"."+method.getName();
            else beanName = plugin.getName()+"."+beanName;
            beanDefinitions.add(ConfigurationBeanDefinition.builder()
                    .name(beanName)
                    .beanClass(method.getReturnType())
                    .implementInterfaces(method.getReturnType().getInterfaces())
                    .scope(getScopeType(method.getReturnType()))
                    .configurationMethod(method)
                    .superClass(method.getReturnType().getSuperclass())
                    .build());
        }
        return beanDefinitions;
    }

    private String getScopeType(@NonNull Class<?> clazz){
        if(clazz.isAnnotationPresent(Scope.class)){
            return clazz.getAnnotation(Scope.class).value();
        }
        return "singleton";
    }

    private void validateClass(@NonNull Class<?> clazz){
        if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())){
            throw new BeanCreatingException("@Component can't be interface or abstract. class: << %s >>", clazz.getName());
        }
    }



}
