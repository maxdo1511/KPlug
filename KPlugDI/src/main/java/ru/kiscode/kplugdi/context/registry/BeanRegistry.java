package ru.kiscode.kplugdi.context.registry;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.scope.BeanScope;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.*;

public class BeanRegistry {

    @Getter
    private final Map<String, BeanScope> beanScopes = new HashMap<>();
    private final Map<String,BeanDefinition> beanDefinitionByName = new HashMap<>();
    private final Map<String,Object> singletonBeanByName = new HashMap<>();


    public void createAndRegistryBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull JavaPlugin plugin){
        for(BeanDefinition beanDefinition: beanDefinitions){
            Object bean = createBean(beanDefinition,plugin);
            singletonBeanByName.put(beanDefinition.getName(),bean);
            beanDefinitionByName.put(beanDefinition.getName(),beanDefinition);
        }
    }

    public Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin) {
        BeanFactory beanFactory = beanDefinition.getBeanFactory();
        if(beanFactory == null) throw new BeanCreatingException("Bean " + beanDefinition.getName() + " has no beanFactory");
        Object bean = beanFactory.createBean(beanDefinition,plugin);
        if (bean == null) throw new BeanCreatingException("BeanFactory return null: " + beanFactory.getClass().getName() + " " + beanDefinition.getName());

        for(BeanPostProcessor processor: beanFactory.getBeanProcessRegistry().getBeanPostProcessors()){
            bean =  processor.postProcessBeforeInitialization(bean,beanDefinition.getName(),plugin);
            if (bean == null) throw new BeanCreatingException("BeanPostProcessor return null: " + processor.getClass().getName());
        }

        for(BeanPostProcessor processor: beanFactory.getBeanProcessRegistry().getBeanPostProcessors()){
            bean =  processor.postProcessAfterInitialization(bean,beanDefinition.getName(),plugin);
            if (bean == null) throw new BeanCreatingException("BeanPostProcessor return null: " + processor.getClass().getName());
        }

        // Какая-то поебень
        BeanScope beanScope = beanScopes.get(beanDefinition.getScope().toLowerCase(Locale.ENGLISH));
        if(beanScope == null){
            throw new BeanCreatingException("Not found scope class for bean " + beanDefinition.getScope());
        }
        bean = beanScope.postProcessAfterInitialization(bean, beanDefinition.getName(), plugin);
        bean = beanScope.postProcessBeforeInitialization(bean, beanDefinition.getName(), plugin);
        // Бессмысленная хуйня

        if(bean == null){
            throw new BeanCreatingException("Filed to create bean " + beanDefinition.getName());
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<?> type, @NonNull JavaPlugin plugin) {
        Object bean = singletonBeanByName.get(type.getName());
        if(bean != null) return (T) bean;
        Set<BeanDefinition> implBeanDefinitions = new HashSet<>();
        for(BeanDefinition beanDefinition: beanDefinitionByName.values()){
            if(beanDefinition.getImplementInterfaces().contains(type)){
                implBeanDefinitions.add(beanDefinition);
            }
        }
        if(implBeanDefinitions.isEmpty()){
            throw new BeanCreatingException("Not found bean for type " + type.getName());
        }
        if(implBeanDefinitions.size() > 1){
            throw new BeanCreatingException("found multiple beans for type " + type.getName()+ "use @CustomBeanName annotation");
        }
        return (T) createBean(implBeanDefinitions.iterator().next(),plugin);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, @NonNull JavaPlugin plugin) {
        Object bean = singletonBeanByName.get(beanName);
        if(bean != null) return (T) bean;
        BeanDefinition beanDefinition = beanDefinitionByName.get(beanName);
        if(beanDefinition == null){
            throw new BeanCreatingException("Bean " + beanName + " not found");
        }
        return (T) createBean(beanDefinition,plugin);
    }

}