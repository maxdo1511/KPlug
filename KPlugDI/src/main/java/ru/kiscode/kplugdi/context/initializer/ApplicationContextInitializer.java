package ru.kiscode.kplugdi.context.initializer;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;

public abstract class ApplicationContextInitializer {
    protected final ApplicationContext applicationContext;
    protected final BeanProcessRegistry beanProcessRegistry;
    protected final BeanRegistry beanRegistry;
    protected final BeanDefinitionFactory beanDefinitionFactory;
    protected final BeanFactory beanFactory;

    public ApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanProcessRegistry = applicationContext.getBeanProcessRegistry();
        this.beanRegistry = applicationContext.getBeanRegistry();
        this.beanDefinitionFactory = applicationContext.getBeanDefinitionFactory();
        this.beanFactory = applicationContext.getBeanFactory();
        beanFactory.setBeanRegistry(beanRegistry);
        beanFactory.setBeanProcessRegistry(beanProcessRegistry);
        beanDefinitionFactory.setBeanProcessRegistry(beanProcessRegistry);
    }

    public abstract void initialize(@NonNull JavaPlugin plugin);
    public abstract void run(@NonNull JavaPlugin plugin);

}
