package ru.kiscode.kplugdi.test.bean;

import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;

import java.util.List;
import java.util.Map;

public class DefaultBeanFactory extends BeanFactory {

    public DefaultBeanFactory(Map<String, BeanDefinition> beanDefinitions, List<BeanPostProcessor> beanPostProcessors) {
        super(beanDefinitions, beanPostProcessors);
    }

    @Override
    public void createBeans() {

    }

    @Override
    protected Object createBean(BeanDefinition beanDefinition) {
        return null;
    }
}
