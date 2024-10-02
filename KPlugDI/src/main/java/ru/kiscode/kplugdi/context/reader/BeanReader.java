package ru.kiscode.kplugdi.context.reader;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

public interface BeanReader {

    Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull BeanRegistry beanRegistry, @NonNull JavaPlugin plugin);
}
