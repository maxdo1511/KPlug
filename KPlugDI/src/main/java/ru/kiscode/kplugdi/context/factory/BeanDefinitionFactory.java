package ru.kiscode.kplugdi.context.factory;

import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;

import java.util.Set;

@Setter
public abstract class BeanDefinitionFactory {
    protected BeanProcessRegistry beanProcessRegistry;

    public abstract Set<BeanDefinition> createBeanDefinitions(@NonNull JavaPlugin plugin);
}

