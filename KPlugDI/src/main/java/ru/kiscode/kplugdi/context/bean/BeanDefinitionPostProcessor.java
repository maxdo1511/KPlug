package ru.kiscode.kplugdi.context.bean;

public interface BeanDefinitionPostProcessor {

    BeanDefinition postProcess(BeanDefinition beanDefinition);

}
