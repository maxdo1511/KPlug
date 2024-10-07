package ru.kiscode.kplugdi.context;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.factory.impl.DefaultBeanFactory;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.factory.impl.DefaultBeanDefinitionFactory;
import ru.kiscode.kplugdi.context.initializer.impl.DefaultApplicationContextInitializer;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Getter
public class ApplicationContext {

    public static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private static final boolean shouldLog = KPlugDI.getInstance().getConfig().getBoolean("debug", false);
    @Getter
    private static ApplicationContext applicationContext;
    private final Set<ApplicationContextInitializer> initializers;
    @Getter
    private final BeanRegistry beanRegistry;
    private final BeanProcessRegistry beanProcessRegistry;
    private final List<JavaPlugin> plugins;
    @Setter
    private BeanDefinitionFactory beanDefinitionFactory;
    @Setter
    @Getter
    private BeanFactory beanFactory;

    public ApplicationContext() {
        applicationContext = this;

        beanDefinitionFactory = new DefaultBeanDefinitionFactory();
        beanFactory = new DefaultBeanFactory();
        beanRegistry = KPlugDI.getInstance().getBeanRegistry();
        beanProcessRegistry = KPlugDI.getInstance().getBeanProcessRegistry();
        initializers = new HashSet<>();

        plugins = new ArrayList<>();

        addApplicationContextInitializer(new DefaultApplicationContextInitializer(this));

        beanRegistry.addSingletonBean(applicationContext, ApplicationContext.class.getName());

        if (shouldLog) {
            logger.warning("ApplicationContext initialized");
        }
    }

    public static void run(JavaPlugin plugin) {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        if (plugin == null) {
            throw new BeanCreatingException("Plugin is null");
        }

        if (shouldLog) {
            logger.warning("Plugin " + plugin.getName() + " context starting");
        }

        applicationContext.getPlugins().add(plugin);
        for(ApplicationContextInitializer initializer : applicationContext.getInitializers()) {
            initializer.initialize(plugin);
        }
    }

    public static void refresh() {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }

        if (shouldLog) {
            logger.warning("ApplicationContext refreshing");
        }

        for (JavaPlugin plugin : applicationContext.getPlugins()) {
            for (ApplicationContextInitializer initializer : applicationContext.getInitializers()) {
                initializer.run(plugin);
            }
        }
    }

    public Object getBean(@NonNull String name, JavaPlugin plugin) {
        return beanRegistry.getBean(name, plugin);
    }

    public <T> T getBean(@NonNull Class<T> clazz, JavaPlugin plugin) {
        return beanRegistry.getBean(clazz, plugin);
    }

    public void addApplicationContextInitializer(@NonNull ApplicationContextInitializer initializer) {
        initializers.add(initializer);
    }
}
