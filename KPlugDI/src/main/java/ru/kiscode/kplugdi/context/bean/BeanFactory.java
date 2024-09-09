package ru.kiscode.kplugdi.context.bean;

import java.util.List;
import java.util.Map;

public abstract class BeanFactory {

    private Map<String, BeanDefinition> beanDefinitions;
    private List<BeanPostProcessor> beanPostProcessors;

}
