package ru.kiscode.kplugdi.context.scope.impl;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.context.scope.BeanScope;

public class SingletonBeanScope implements BeanScope {

    @Override
    public Object getBean(Class<?> beanClass, JavaPlugin plugin, BeanRegistry beanRegistry) {
        return beanRegistry.getBean(beanClass, plugin);
    }

    @Override
    public Object getBean(BeanDefinition beanDefinition, JavaPlugin plugin, BeanRegistry beanRegistry) {
        if (beanRegistry.getSingletonBeanByName().containsKey(beanDefinition.getName())) {
            return beanRegistry.getBean(beanDefinition.getName(), plugin);
        }
        Object bean = beanRegistry.createBean(beanDefinition, plugin);
        beanRegistry.addSingletonBean(bean, beanDefinition.getName());
        return bean;
    }

    @Override
    public Object getBean(String beanName, JavaPlugin plugin, BeanRegistry beanRegistry) {
        return beanRegistry.getBean(beanName, plugin);
    }

    @Override
    public String getScopeName() {
        return "singleton";
    }
}
