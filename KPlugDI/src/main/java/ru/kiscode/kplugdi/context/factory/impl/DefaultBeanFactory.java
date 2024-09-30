package ru.kiscode.kplugdi.context.factory.impl;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.reader.BeanReader;

import java.util.Set;

public class DefaultBeanFactory extends BeanFactory {

    @Override
    public void createBeans(@NonNull Set<BeanDefinition> beanDefinitions) {
        //TODO создание
        //TODO preConstruct
        //TODO postProcess before
        //TODO postProcess after
        //TODO postConstruct

    }

    @Override
    public Object createBean(@NonNull BeanDefinition beanDefinition) {
        Object object = null;
        for(BeanReader beanReader : beanProcessRegistry.getBeanReaders()){
            object = beanReader.createBean(beanDefinition,this);
        }
        return object;
    }
}
