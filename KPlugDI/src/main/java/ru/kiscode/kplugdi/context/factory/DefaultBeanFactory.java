package ru.kiscode.kplugdi.context.factory;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;

import java.util.List;
import java.util.Set;

public class DefaultBeanFactory extends BeanFactory {
    @Override
    public void createBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull List<BeanPostProcessor> beanPostProcessors) {

    }

    @Override
    public Object createBean(@NonNull BeanDefinition beanDefinition) {
        return null;
    }
}
