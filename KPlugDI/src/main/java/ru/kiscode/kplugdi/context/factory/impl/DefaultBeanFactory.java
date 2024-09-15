package ru.kiscode.kplugdi.context.factory.impl;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;

import java.util.List;
import java.util.Set;

public class DefaultBeanFactory extends BeanFactory {
    @Override
    public void createBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull List<BeanPostProcessor> beanPostProcessors) {
        //TODO preConstract
        //TODO создание
        //TODO postProcess
        //TODO preConstract

        
    }

    @Override
    public Object createBean(@NonNull BeanDefinition beanDefinition) {
        return null;
    }
}
