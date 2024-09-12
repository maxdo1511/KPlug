package ru.kiscode.kplugdi.context.bean;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public abstract class BeanFactory {

    private Map<String, BeanDefinition> beanDefinitions;
    private List<BeanPostProcessor> beanPostProcessors;
    private Map<String, Object> singletonObjectsByName;
    private Map<Class<?>, Object> singletonObjectsByClass;
    private Map<String, Map<Class<?>, Object>> pluginObjectsByClass;

    public BeanFactory(Map<String, BeanDefinition> beanDefinitions, List<BeanPostProcessor> beanPostProcessors) {
        this.beanDefinitions = beanDefinitions;
        this.beanPostProcessors = beanPostProcessors;
    }

    public <T> T getBean(Class<T> clazz, JavaPlugin plugin) {
        Object bean = singletonObjectsByClass.get(clazz);
        if (bean != null) {
            return (T) bean;
        }
        BeanDefinition beanDefinition = beanDefinitions.get(clazz.getName());
        if (beanDefinition == null) {
            throw new RuntimeException("Bean not found. Class: " + clazz.getName());
        }
        if (beanDefinition.getScope() == ScopeType.PROTOTYPE) {
            return (T) createBean(beanDefinition);
        }
        if (beanDefinition.getScope() == ScopeType.PLUGIN) {
            Object pluginBean = pluginObjectsByClass.get(plugin.getName()).get(clazz);
            if (pluginBean != null) {
                return (T) pluginBean;
            }
            Object pluginBeanInst = createBean(beanDefinition);
            return (T) pluginBeanInst;
        }
        for (Class<?> implementedInterface : beanDefinition.getImplementedInterfaces()) {
            if (clazz.equals(implementedInterface)) {
                return (T) getBean(beanDefinition.getBeanClass(), plugin);
            }
        }
        if (beanDefinition.getScope() == ScopeType.SINGLETON) {
            Object createdBean = createBean(beanDefinition);
            singletonObjectsByClass.put(clazz, createdBean);
            return (T) createdBean;
        }
        throw new RuntimeException("Bean not found. Class: " + clazz.getName());
    }

    public <T> T getBean(String name) {
        if (singletonObjectsByName.containsKey(name)) {
            return (T) singletonObjectsByName.get(name);
        }
        BeanDefinition beanDefinition = beanDefinitions.get(name);
        if (beanDefinition == null) {
            throw new RuntimeException("Bean not found. Name: " + name);
        }
        if (beanDefinition.getScope() == ScopeType.PROTOTYPE) {
            return (T) createBean(beanDefinition);
        }
        if (beanDefinition.getScope() == ScopeType.PLUGIN) {
            Object pluginBean = pluginObjectsByClass.get(clazz);
            if (pluginBean != null) {
                return (T) pluginBean;
            }
            Object pluginBeanInst = createBean(beanDefinition);
            pluginObjectsByClass.put(clazz, pluginBeanInst);
            return (T) pluginBeanInst;
        }
        throw new RuntimeException("Bean not found. Name: " + name);
    }

    private <T> T getBeanImplementedInterface(Class<T> clazz, BeanDefinition beanDefinition, JavaPlugin plugin) {
        for (Class<?> implementedInterface : beanDefinition.getImplementedInterfaces()) {
            if (clazz.equals(implementedInterface)) {
                return (T) getBean(beanDefinition.getBeanClass(), plugin);
            }
        }
        return null;
    }

    public abstract void createBeans();

    protected abstract Object createBean(BeanDefinition beanDefinition);
}
