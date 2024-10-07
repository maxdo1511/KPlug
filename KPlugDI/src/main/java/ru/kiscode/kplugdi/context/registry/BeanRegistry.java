package ru.kiscode.kplugdi.context.registry;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.PostConstruct;
import ru.kiscode.kplugdi.annotations.PreConstruct;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.scope.BeanScope;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class BeanRegistry {

    @Getter
    private final Map<String, BeanScope> beanScopes = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionByName = new HashMap<>();
    @Getter
    private final Map<String, Object> singletonBeanByName = new HashMap<>();
    private final BeanProcessRegistry beanProcessRegistry;

    public BeanRegistry(@NonNull BeanProcessRegistry beanProcessRegistry){
        this.beanProcessRegistry = beanProcessRegistry;
    }

    public void createBeans(@NonNull List<BeanDefinition> beanDefinitions, @NonNull JavaPlugin plugin) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            registerBean(beanDefinition, plugin);
        }
        for(BeanDefinition beanDefinition : beanDefinitions){
            beanScopes.get(beanDefinition.getScope()).getBean(beanDefinition, plugin, this);
        }
    }

    public Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin) {
        // получаем фабрику
        BeanFactory beanFactory = beanDefinition.getBeanFactory();
        if(beanFactory == null) throw new BeanCreatingException("Bean " + beanDefinition.getName() + " has no beanFactory");
        // создаем бин
        Object bean = beanFactory.createBean(beanDefinition, plugin);
        if (bean == null) throw new BeanCreatingException("BeanFactory return null: " + beanFactory.getClass().getName() + " " + beanDefinition.getName());

        // post process
        for (BeanPostProcessor beanPostProcessor : beanProcessRegistry.getBeanPostProcessors()) {
            beanPostProcessor.postProcessBeforeInitialization(bean, beanDefinition.getName(), plugin);
        }

        // pre construct
        List<Method> pre = ReflectionUtil.getAllMethodsAnnotatedWith(bean.getClass(), PreConstruct.class, false);
        if (!pre.isEmpty()) {
            try {
                pre.get(0).invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        //TODO init

        for (BeanPostProcessor beanPostProcessor : beanProcessRegistry.getBeanPostProcessors()) {
            beanPostProcessor.postProcessAfterInitialization(bean, beanDefinition.getName(), plugin);
        }

        // post construct
        List<Method> post = ReflectionUtil.getAllMethodsAnnotatedWith(bean.getClass(), PostConstruct.class, false);
        if (!post.isEmpty()) {
            try {
                post.get(0).invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

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

        // Что за бред?? а если бину дали свое название?
        BeanDefinition beanDefinition = beanDefinitionByName.get(type.getName());
        if(beanDefinition != null) return (T) beanScopes.get(beanDefinition.getScope()).getBean(beanDefinition, plugin, this);

        Set<BeanDefinition> implBeanDefinitions = new HashSet<>();
        for(BeanDefinition bd : beanDefinitionByName.values()){
            if (ReflectionUtil.hasInterfaceOrSuperClass(bd.getBeanClass(), type)) {
                implBeanDefinitions.add(bd);
            }
        }
        if(implBeanDefinitions.isEmpty()) {
            throw new BeanCreatingException("Not found bean for type " + type.getName());
        }
        if(implBeanDefinitions.size() > 1) {
            throw new BeanCreatingException("found multiple beans for type " + type.getName()+ "use @CustomBeanName annotation");
        }
        beanDefinition = implBeanDefinitions.iterator().next();
        return (T) beanScopes.get(beanDefinition.getScope()).getBean(beanDefinition, plugin, this);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, @NonNull JavaPlugin plugin) {
        Object bean = singletonBeanByName.get(beanName);
        if(bean != null) return (T) bean;
        BeanDefinition beanDefinition = beanDefinitionByName.get(beanName);
        if(beanDefinition == null){
            throw new BeanCreatingException("Bean " + beanName + " not found");
        }
        return (T) beanScopes.get(beanDefinition.getScope()).getBean(beanDefinition, plugin, this);
    }

    public void addSingletonBean(@NonNull Object bean, @NonNull String name){
        singletonBeanByName.put(name, bean);
    }

    public void registerBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin){
        beanDefinitionByName.put(beanDefinition.getName(), beanDefinition);
    }

}