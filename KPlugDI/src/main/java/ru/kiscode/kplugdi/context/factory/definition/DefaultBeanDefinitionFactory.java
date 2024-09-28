package ru.kiscode.kplugdi.context.factory.definition;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;

import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.context.registry.BeanProcessorRegistry;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class DefaultBeanDefinitionFactory implements BeanDefinitionFactory {

    @Override
    public Set<BeanDefinition> createBeanDefinitions(@NonNull BeanProcessorRegistry eventRegistry, @NonNull JavaPlugin plugin) {
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        beanDefinitions.add(getPluginBeanDefinition(plugin));
        for(Class<?> clazz : eventRegistry.getClasses()){
            if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;
            for(BeanDefinitionReader reader : eventRegistry.getBeanDefinitionReaders()){
                beanDefinitions.addAll(reader.createBeanDefinition(clazz));
            }
        }
        return beanDefinitions;
    }

    private BeanDefinition getPluginBeanDefinition(@NonNull JavaPlugin plugin) {
        return PluginBeanDefinition.builder()
                .pluginInstance(plugin)
                .name(plugin.getClass().getName())
                .beanClass(plugin.getClass())
                .implementInterfaces(plugin.getClass().getInterfaces())
                .scope("singleton")
                .build();

    }
}
