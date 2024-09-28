package ru.kiscode.kplugdi.context.factory.bean;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

import ru.kiscode.kplugdi.context.registry.BeanProcessorRegistry;

import java.util.Set;

public class DefaultBeanFactory extends BeanFactory {
    @Override
    public void createBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull BeanProcessorRegistry eventRegistry) {
        //TODO создание
        //TODO preConstract
        //TODO postProcess
        //TODO postConstract


        
    }

    @Override
    public Object createBean(@NonNull BeanDefinition beanDefinition) {
        return null;
    }
}
