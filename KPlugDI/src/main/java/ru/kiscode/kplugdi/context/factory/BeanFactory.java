package ru.kiscode.kplugdi.context.factory;

import lombok.NonNull;
import lombok.Setter;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;

import java.util.*;

@Setter
public abstract class BeanFactory {
    protected BeanProcessRegistry beanProcessRegistry;
    protected BeanRegistry beanRegistry;

    public abstract void createBeans(@NonNull Set<BeanDefinition> beanDefinitions);
    public abstract Object createBean(@NonNull BeanDefinition beanDefinition);

    public <T> T getBean(String name) {
        return null;

    }
    public <T> T getBean(Class<T> clazz){
        return null;
    }
}
