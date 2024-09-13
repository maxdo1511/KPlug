package ru.kiscode.kplugdi.context.processor;

import ru.kiscode.kplugdi.test.bean.BeanDefinition;

public interface BeanDefinitionPostProcessor {

    void postProcess(BeanDefinition beanDefinition);

}
