package ru.kiscode.kplugdi.context.scope.impl;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.context.scope.BeanScope;

public class SingletonBeanScope implements BeanScope {
    @Override
    public Object getBean(Object bean, String beanName, JavaPlugin plugin, BeanRegistry beanRegistry) {
        return bean;
    }

    @Override
    public String getScopeName() {
        return "singleton";
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return BeanScope.super.postProcessBeforeInitialization(bean, beanName, plugin);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return BeanScope.super.postProcessAfterInitialization(bean, beanName, plugin);
    }
}
