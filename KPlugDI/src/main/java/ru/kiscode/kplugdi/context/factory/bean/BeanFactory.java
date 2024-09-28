package ru.kiscode.kplugdi.context.factory.bean;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanProcessorRegistry;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.*;

public abstract class BeanFactory {


    public abstract void createBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull BeanProcessorRegistry eventRegistry);
    public abstract Object createBean(@NonNull BeanDefinition beanDefinition);

    public <T> T getBean(String name) {
        throw new BeanCreatingException("Bean not found. Name: " + name);
    }

    public <T> T getBean(Class<T> clazz){
        return null;

    }


}
