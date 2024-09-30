package ru.kiscode.kplugdi.context.processor.beanpostprocessors;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;

public class AutowiredPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return null;
    }
}
