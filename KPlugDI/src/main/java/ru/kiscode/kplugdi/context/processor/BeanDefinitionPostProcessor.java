package ru.kiscode.kplugdi.context.processor;

import ru.kiscode.kplugdi.context.model.BeanDefinition;

public interface BeanDefinitionPostProcessor {

    void postProcess(BeanDefinition beanDefinition);

}
