package ru.kiscode.kplugdi.context.bean;

import java.util.List;
import java.util.Map;

public abstract class BeanFactory {

    private Map<String, BeanDefinition> beanDefinitions;
    private List<BeanPostProcessor> beanPostProcessors;
    private Map<String, Object> singletonObjectsByName;
    private Map<Class<?>, Object> singletonObjectsByClass;

    public BeanFactory(Map<String, BeanDefinition> beanDefinitions, List<BeanPostProcessor> beanPostProcessors) {
        this.beanDefinitions = beanDefinitions;
        this.beanPostProcessors = beanPostProcessors;
    }

    public <T> T getBean(Class<T> clazz) {
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
        for (Class<?> implementedInterface : beanDefinition.getImplementedInterfaces()) {
            if (clazz.equals(implementedInterface)) {
                return (T) getBean(beanDefinition.getBeanClass());
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
        Object bean = createBean(beanDefinition);
        return (T) bean;
    }

    public abstract void createBeans();

    protected abstract Object createBean(BeanDefinition beanDefinition);
}
