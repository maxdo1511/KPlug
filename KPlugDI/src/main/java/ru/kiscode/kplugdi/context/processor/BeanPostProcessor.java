package ru.kiscode.kplugdi.context.processor;

import org.bukkit.plugin.java.JavaPlugin;

public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return bean;
    }
    default Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return bean;
    }

}
