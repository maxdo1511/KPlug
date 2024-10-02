package ru.kiscode.kplugdi.context.factory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;

@Getter
@Setter
public abstract class BeanFactory {
    protected BeanProcessRegistry beanProcessRegistry;
    protected BeanRegistry beanRegistry;

    public abstract Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull JavaPlugin plugin);
}
