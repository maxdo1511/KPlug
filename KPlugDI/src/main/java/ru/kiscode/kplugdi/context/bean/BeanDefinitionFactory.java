package ru.kiscode.kplugdi.context.bean;

import lombok.SneakyThrows;
import org.bukkit.event.Listener;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.minectaftutil.AbstractCommand;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanDefinitionFactory {

    private Map<Class<?>, Method> beanConfigs;
    private List<BeanDefinition> beanDefinitions;
    private List<BeanDefinitionPostProcessor> beanDefinitionPostProcessors;
    private List<BeanPostProcessor> beanPostProcessors;

    public BeanDefinitionFactory(Map<Class<?>, Method> beanConfigs) {
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
        beanDefinitionBuilder.setBeanClass(clazz);
        beanDefinitionBuilder.setBeanConfigMethod(method);
        beanDefinitionBuilder.setBeanConstructors();
        beanDefinitionBuilder.setImplementedInterfaces();
        beanDefinitionBuilder.setScope();
        beanDefinitionBuilder.setShouldInstantiate(true);
        return beanDefinitionBuilder.build();
    }

}
