package ru.kiscode.kplugdi.context.registry;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.scope.BeanScope;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.util.*;

public class BeanRegistry {

    @Getter
    private final Map<String, BeanScope> beanScopes = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionByName = new HashMap<>();
    private final Map<String, Object> singletonBeanByName = new HashMap<>();
    private final BeanProcessRegistry beanProcessRegistry;

    public BeanRegistry(@NonNull BeanProcessRegistry beanProcessRegistry){
        this.beanProcessRegistry = beanProcessRegistry;
    }

    public void createAndRegistryBeans(@NonNull List<BeanDefinition> beanDefinitions, @NonNull JavaPlugin plugin) {
        beanDefinitions.stream().map(BeanDefinition::getName).forEach(System.out::println);
        for(BeanDefinition beanDefinition : beanDefinitions){
            registerBean(beanDefinition, plugin);
        }
        for (Object bean : singletonBeanByName.values()) {
            for (BeanPostProcessor beanPostProcessor : beanProcessRegistry.getBeanPostProcessors()) {
                beanPostProcessor.postProcessBeforeInitialization(bean, bean.getClass().getName(), plugin);
            }
        }

        // init method

        for (Object bean : singletonBeanByName.values()) {
            for (BeanPostProcessor beanPostProcessor : beanProcessRegistry.getBeanPostProcessors()) {
                beanPostProcessor.postProcessAfterInitialization(bean, bean.getClass().getName(), plugin);
            }
        }
    }

    public Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin) {
        BeanFactory beanFactory = beanDefinition.getBeanFactory();
        if(beanFactory == null) throw new BeanCreatingException("Bean " + beanDefinition.getName() + " has no beanFactory");
        Object bean = beanFactory.createBean(beanDefinition,plugin);
        if (bean == null) throw new BeanCreatingException("BeanFactory return null: " + beanFactory.getClass().getName() + " " + beanDefinition.getName());

        // Какая-то поебень
        /*
        BeanScope beanScope = beanScopes.get(beanDefinition.getScope().toLowerCase(Locale.ENGLISH));
        if(beanScope == null){
            throw new BeanCreatingException("Not found scope class for bean " + beanDefinition.getScope());
        }
        bean = beanScope.postProcessAfterInitialization(bean, beanDefinition.getName(), plugin);
        bean = beanScope.postProcessBeforeInitialization(bean, beanDefinition.getName(), plugin);
         */
        // Бессмысленная хуйня

        return bean;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<?> type, @NonNull JavaPlugin plugin) {
        Object bean = singletonBeanByName.get(type.getName());
        if(bean != null) return (T) bean;

        // Что за бред??
        BeanDefinition beanDefinition = beanDefinitionByName.get(type.getName());
        if(beanDefinition != null) return (T) createBean(beanDefinition, plugin);

        Set<BeanDefinition> implBeanDefinitions = new HashSet<>();
        for(BeanDefinition bd : beanDefinitionByName.values()){
            if (ReflectionUtil.hasInterfaceOrSuperClass(bd.getBeanClass(), type)) {
                implBeanDefinitions.add(bd);
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

    public void addSingletonBean(@NonNull Object bean, @NonNull String name){
        singletonBeanByName.put(name,bean);
    }

    public void registerBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin){
        System.out.println("Register bean: " + beanDefinition.getName());
        beanDefinitionByName.put(beanDefinition.getName(), beanDefinition);
        Object bean = createBean(beanDefinition, plugin);

        // pre init

        //Scope процесс. Возможно где-то тут можно сделать? Хотя хз
        if(beanDefinition.getScope().equalsIgnoreCase("singleton")){
            singletonBeanByName.put(beanDefinition.getName(), bean);
        }
    }

}