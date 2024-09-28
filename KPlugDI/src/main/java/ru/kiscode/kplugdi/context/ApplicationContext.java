package ru.kiscode.kplugdi.context;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.bean.BeanFactory;
import ru.kiscode.kplugdi.context.factory.bean.DefaultBeanFactory;
import ru.kiscode.kplugdi.context.factory.definition.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.factory.definition.DefaultBeanDefinitionFactory;
import ru.kiscode.kplugdi.context.initializer.impl.DefaultApplicationContextInitializer;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Setter
@Getter
public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private static final boolean shouldLog = false;
    private static ApplicationContext applicationContext;
    private BeanFactory beanFactory;
    private BeanDefinitionFactory beanDefinitionFactory;
    private Set<ApplicationContextInitializer> initializers;

    public ApplicationContext() {
        applicationContext = this;
        beanFactory = new DefaultBeanFactory();
        beanDefinitionFactory = new DefaultBeanDefinitionFactory();
        initializers = new HashSet<>();
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
}
