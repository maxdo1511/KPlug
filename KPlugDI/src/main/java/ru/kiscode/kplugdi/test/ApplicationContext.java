package ru.kiscode.kplugdi.test;

import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.test.bean.BeanFactory;
import ru.kiscode.kplugdi.test.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.test.initializer.DefaultApplicationContextInitializer;
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
    private BeanFactory beanFactory;

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
        applicationContext.init(plugin);
    }




    private void init(JavaPlugin plugin) {
        if (pluginsClasses.containsKey(plugin.getClass())) {
            throw new BeanCreatingException("Plugin already initialized");
        }
        pluginsClasses.put(plugin.getClass(), plugin);
        initializer.initialize(plugin);
    }


    public <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    public <T> T getBean(String name) {
        return beanFactory.getBean(name);
    }

}
