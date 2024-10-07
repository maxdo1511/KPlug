package ru.kiscode.kplugdi.context.scope;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;

public interface BeanScope {

    Object getBean(Class<?> beanClass, JavaPlugin plugin, BeanRegistry beanRegistry);
    Object getBean(BeanDefinition beanDefinition, JavaPlugin plugin, BeanRegistry beanRegistry);
    Object getBean(String beanName, JavaPlugin plugin, BeanRegistry beanRegistry);

    String getScopeName();

}
