package ru.kiscode.kplugdi.context.bean;

import java.util.List;
import java.util.Map;

public class DefaultBeanFactory extends BeanFactory {
    public DefaultBeanFactory(Map<String, BeanDefinition> beanDefinitions, List<BeanPostProcessor> beanPostProcessors) {
        super(beanDefinitions, beanPostProcessors);
    }
}
