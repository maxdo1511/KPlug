package ru.kiscode.kplugdi.test.bean;

import lombok.Getter;
import lombok.SneakyThrows;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanDefinitionFactory {

    private Map<Class<?>, Method> beanConfigs;
    @Getter
    private List<BeanDefinition> beanDefinitions;
    @Getter
    private List<BeanDefinitionPostProcessor> beanDefinitionPostProcessors;
    @Getter
    private List<BeanPostProcessor> beanPostProcessors;

    public
    BeanDefinitionFactory(Map<Class<?>, Method> beanConfigs) {
        this.beanConfigs = beanConfigs;
    }

    @SneakyThrows
    public void createBeanDefinitions(Set<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (BeanDefinitionPostProcessor.class.isAssignableFrom(clazz)) {
                beanDefinitionPostProcessors.add((BeanDefinitionPostProcessor) clazz.getConstructors()[0].newInstance());
                continue;
            }
            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                beanPostProcessors.add((BeanPostProcessor) clazz.getConstructors()[0].newInstance());
                continue;
            }
            Component component = clazz.getAnnotation(Component.class);
            if (component != null) {
                BeanDefinition beanDefinition = createComponentBeanDefinition(clazz, beanConfigs.get(clazz));
                if (beanDefinition != null) {
                    beanDefinitions.add(beanDefinition);
                } else {
                    throw new RuntimeException("Bean definition not created. Class: " + clazz.getName());
                }
            }
        }
    }

    private BeanDefinition createComponentBeanDefinition(Class<?> clazz, Method method) {
        BeanDefinitionBuilder beanDefinitionBuilder = new BeanDefinitionBuilder();
        return beanDefinitionBuilder
                .setBeanClass(clazz)
                .setBeanConfigMethod(method)
                .setBeanConstructors()
                .setImplementedInterfaces()
                .setScope()
                .setShouldInstantiate(true)
                .build();
    }

}