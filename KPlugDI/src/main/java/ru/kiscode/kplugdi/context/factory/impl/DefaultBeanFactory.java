package ru.kiscode.kplugdi.context.factory.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

import ru.kiscode.kplugdi.context.reader.BeanReader;

public class DefaultBeanFactory extends BeanFactory {


    @Override
    public Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin) {
        Object object = null;
        for(BeanReader beanReader: beanProcessRegistry.getBeanReaders()){
            object = beanReader.createBean(beanDefinition, beanRegistry,plugin);
        }
        return object;
    }
}
