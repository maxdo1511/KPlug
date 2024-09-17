package ru.kiscode.kplugdi.context;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.factory.impl.DefaultBeanFactory;
import ru.kiscode.kplugdi.context.initializer.impl.DefaultApplicationContextInitializer;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Setter
@Getter
public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private static final boolean shouldLog = false;
    private static ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private List<ApplicationContextInitializer> initializers;

    public ApplicationContext() {
        applicationContext = this;
        beanFactory = new DefaultBeanFactory();
        initializers = new ArrayList<>();
        initializers.add(new DefaultApplicationContextInitializer(this));
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

        for(ApplicationContextInitializer initializer : applicationContext.getInitializers()) {
            initializer.initialize(plugin);
        }
    }

    public void addApplicationContextInitializer(@NonNull ApplicationContextInitializer initializer) {
        initializers.add(initializer);
    }
    public void removeApplicationContextInitializer(@NonNull ApplicationContextInitializer initializer) {
        initializers.remove(initializer);
    }
}
