package ru.kiscode.kplugdi.context.processor.definition;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;

public class BeanFactoryInjectDefinitionProcess implements BeanDefinitionPostProcessor {

    private final BeanFactory beanFactory;

    public BeanFactoryInjectDefinitionProcess(@NonNull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcess(BeanDefinition beanDefinition) {
        beanDefinition.setBeanFactory(beanFactory);
    }
}
