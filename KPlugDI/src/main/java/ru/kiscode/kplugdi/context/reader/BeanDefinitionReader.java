package ru.kiscode.kplugdi.context.reader;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

import java.util.Set;

public interface BeanDefinitionReader {
    Set<BeanDefinition> createBeanDefinition(@NonNull Class<?> clazz, @NonNull JavaPlugin plugin);
}
