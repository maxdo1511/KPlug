package ru.kiscode.kplugdi.context.factory.definition;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanProcessorRegistry;

import java.util.Set;

public interface BeanDefinitionFactory {

    Set<BeanDefinition> createBeanDefinitions(@NonNull BeanProcessorRegistry eventRegistry, @NonNull JavaPlugin plugin);
}
