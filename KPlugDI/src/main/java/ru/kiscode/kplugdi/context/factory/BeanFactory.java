package ru.kiscode.kplugdi.context.factory;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.*;

public abstract class BeanFactory {


    public abstract void createBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull List<BeanPostProcessor> beanPostProcessors);
    public abstract Object createBean(@NonNull BeanDefinition beanDefinition);

    public <T> T getBean(String name) {
        throw new BeanCreatingException("Bean not found. Name: " + name);
    }
}
