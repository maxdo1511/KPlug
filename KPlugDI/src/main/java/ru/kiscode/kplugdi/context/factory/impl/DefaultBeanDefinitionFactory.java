package ru.kiscode.kplugdi.context.factory.impl;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;

import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;

import java.util.HashSet;
import java.util.Set;

public class DefaultBeanDefinitionFactory extends BeanDefinitionFactory {


    private BeanDefinition getPluginBeanDefinition(@NonNull JavaPlugin plugin) {
        return PluginBeanDefinition.builder()
                .pluginInstance(plugin)
                .name(plugin.getClass().getName())
                .beanClass(plugin.getClass())
                .implementInterfaces(plugin.getClass().getInterfaces())
                .scope("singleton")
                .build();

    }

    @Override
    public Set<BeanDefinition> createBeanDefinitions(@NonNull JavaPlugin plugin) {
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        beanDefinitions.add(getPluginBeanDefinition(plugin));
        for(Class<?> clazz : beanProcessRegistry.getClasses()){
            for(BeanDefinitionReader reader : beanProcessRegistry.getBeanDefinitionReaders()){
                beanDefinitions.addAll(reader.createBeanDefinition(clazz));
            }
        }
        return beanDefinitions;
    }
}
