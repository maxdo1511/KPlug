package ru.kiscode.kplugdi.context.bean;

import org.bukkit.event.Listener;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.minectaftutil.AbstractCommand;

import java.lang.reflect.Method;
import java.util.Map;

public class BeanDefinitionFactory {

    private Map<Class<?>, Method> beanConfigs;

    public BeanDefinitionFactory(Map<Class<?>, Method> beanConfigs) {
        this.beanConfigs = beanConfigs;
    }

    public BeanDefinition createBeanDefinition(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (component != null) {
            return createComponentBeanDefinition(clazz, beanConfigs.get(clazz));
        }

        if (Listener.class.isAssignableFrom(clazz)) {
            return createListenerBeanDefinition(clazz, beanConfigs.get(clazz));
        }

        if (AbstractCommand.class.isAssignableFrom(clazz)) {
            return createCommandBeanDefinition(clazz, beanConfigs.get(clazz));
        }
        return null;
    }

}
