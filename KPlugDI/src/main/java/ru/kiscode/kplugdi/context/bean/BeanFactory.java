package ru.kiscode.kplugdi.context.bean;

import java.util.List;
import java.util.Map;

public abstract class BeanFactory {

    private Map<String, BeanDefinition> beanDefinitions;
    private List<BeanPostProcessor> beanPostProcessors;
    private Map<String, Object> singletonObjectsByName;
    private Map<Class<?>, Object> singletonObjectsByClass;

    public <T> T getBean(Class<T> clazz) {
        return (T) singletonObjectsByClass.get(clazz);
    }

    public <T> T getBean(String name) {
        if (singletonObjectsByName.containsKey(name)) {
            return (T) singletonObjects.get(name);
        }
        BeanDefinition beanDefinition = beanDefinitions.get(name);
        if (beanDefinition == null) {
            throw new RuntimeException("Bean not found. Name: " + name);
        }
        Object bean = createBean(beanDefinition);
        return (T) bean;
    }
}
