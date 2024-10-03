package ru.kiscode.kplugdi.context.factory.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;

import java.util.HashSet;
import java.util.Set;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;

public class DefaultBeanDefinitionFactory extends BeanDefinitionFactory {

    @Override
    public Set<BeanDefinition> createBeanDefinitions(@NonNull JavaPlugin plugin) {
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        for(Class<?> clazz : beanProcessRegistry.getClasses()){
            logger.warning("DefaultBeanDefinitionFactory: " + clazz.getName());
            for(BeanDefinitionReader reader : beanProcessRegistry.getBeanDefinitionReaders()){
                logger.warning("BeanDefinitionReader: " + reader.getClass().getName());
                beanDefinitions.addAll(reader.createBeanDefinition(clazz));
            }
        }
        return beanDefinitions;
    }
}
