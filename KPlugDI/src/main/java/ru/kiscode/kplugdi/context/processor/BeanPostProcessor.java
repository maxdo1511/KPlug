package ru.kiscode.kplugdi.context.processor;

import org.bukkit.plugin.java.JavaPlugin;

public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin);
    Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin);

}
