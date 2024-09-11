package ru.kiscode.kplugdi.context;

import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.bean.BeanDefinition;
import ru.kiscode.kplugdi.context.bean.BeanFactory;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.initializer.DefaultApplicationContextInitializer;

import java.util.Map;
import java.util.logging.Logger;

public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private static boolean shouldLog = false;
    private static ApplicationContext applicationContext;
    @Setter
    private ApplicationContextInitializer initializer;
    private Map<Class<?>, JavaPlugin> pluginsClasses;
    private BeanFactory beanFactory;

    public ApplicationContext() {
        applicationContext = this;
        initializer = new DefaultApplicationContextInitializer(this);
        pluginsClasses = new java.util.HashMap<>();

        if (shouldLog) {
            logger.info("ApplicationContext initialized");
        }
    }

    public static void run(JavaPlugin plugin) {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        if (plugin == null) {
            throw new RuntimeException("Plugin is Null");
        }
        applicationContext.init(plugin);
    }

    public void init(JavaPlugin plugin) {
        if (pluginsClasses.containsKey(plugin.getClass())) {
            throw new RuntimeException("Plugin already initialized");
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
