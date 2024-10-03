package ru.kiscode.kplugdi.context.processor.definition;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;

public class BeanFactoryInjectDefinitionProcess implements BeanDefinitionPostProcessor {

    private final BeanFactory beanFactory = ApplicationContext.getApplicationContext().getBeanFactory();

    @Override
    public void postProcess(BeanDefinition beanDefinition) {
        beanDefinition.setBeanFactory(beanFactory);
    }
}
