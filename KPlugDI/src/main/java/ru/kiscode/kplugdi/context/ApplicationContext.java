package ru.kiscode.kplugdi.context;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.factory.impl.DefaultBeanFactory;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.factory.impl.DefaultBeanDefinitionFactory;
import ru.kiscode.kplugdi.context.initializer.impl.DefaultApplicationContextInitializer;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.processor.beanpostprocessors.AutowiredBeanPostProcessor;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Getter
public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());
    private static final boolean shouldLog = false;
    private static ApplicationContext applicationContext;
    private final BeanRegistry beanRegistry;
    private final Set<ApplicationContextInitializer> initializers;
    @Setter
    private BeanProcessRegistry beanProcessRegistry;
    @Setter
    private BeanFactory beanFactory;
    @Setter
    private BeanDefinitionFactory beanDefinitionFactory;

    public ApplicationContext() {
        applicationContext = this;

        beanProcessRegistry = new BeanProcessRegistry();
        beanFactory = new DefaultBeanFactory();
        beanDefinitionFactory = new DefaultBeanDefinitionFactory();
        beanRegistry = KPlugDI.getInstance().getBeanRegistry();
        initializers = new HashSet<>();

        initializers.add(new DefaultApplicationContextInitializer(this));
        beanProcessRegistry.registerBeanPostProcessor(new AutowiredBeanPostProcessor(beanFactory));

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
