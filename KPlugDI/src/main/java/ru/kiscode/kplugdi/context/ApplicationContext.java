package ru.kiscode.kplugdi.context;

import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.initializer.DefaultApplicationContextInitializer;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private static final boolean shouldLog = false;
    private static ApplicationContext applicationContext;
    @Setter
    private ApplicationContextInitializer initializer;
    private final Map<Class<?>, JavaPlugin> pluginsClasses;

    public ApplicationContext() {
        applicationContext = this;
        initializer = new DefaultApplicationContextInitializer(this);
        pluginsClasses = new HashMap<>();

        if (shouldLog) {
            logger.info("ApplicationContext initialized");
        }
    }

    public static void run(JavaPlugin plugin) {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        if (plugin == null) {
            throw new BeanCreatingException("Plugin is null");
        }
        applicationContext.refreshContext(plugin);
    }


    public void refreshContext(JavaPlugin plugin) {
        pluginsClasses.put(plugin.getClass(), plugin);
        initializer.initialize(plugin);
    }

    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    public <T> T getBean(String name) {
        return null;
    }
}
