package ru.kiscode.kplugdi.context.initializer;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;

public abstract class ApplicationContextInitializer {
    protected final ApplicationContext applicationContext;
    protected final BeanProcessRegistry beanProcessRegistry;

    public ApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanProcessRegistry = applicationContext.getBeanProcessRegistry();
        applicationContext.getBeanFactory().setBeanProcessRegistry(applicationContext.getBeanProcessRegistry());
        applicationContext.getBeanFactory().setBeanRegistry(applicationContext.getBeanRegistry());
        applicationContext.getBeanDefinitionFactory().setBeanProcessRegistry(applicationContext.getBeanProcessRegistry());
    }

    public abstract void initialize(@NonNull JavaPlugin plugin);
    public abstract void run(@NonNull JavaPlugin plugin);


}
