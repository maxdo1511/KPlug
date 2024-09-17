package ru.kiscode.kplugdi.context.initializer;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.BeanFactory;

public abstract class ApplicationContextInitializer {
    protected final ApplicationContext applicationContext;
    protected final BeanFactory beanFactory;

    public ApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.beanFactory = applicationContext.getBeanFactory();
    }

    public abstract void initialize(@NonNull JavaPlugin plugin);


}
