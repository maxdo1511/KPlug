package ru.kiscode.kplugdi.context.initializer;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.resource.impl.PluginDirectoryResourceLoader;

import java.util.HashSet;
import java.util.Set;

public class DefaultApplicationContextInitializer implements ApplicationContextInitializer {
    private final ApplicationContext applicationContext;
    private JavaPlugin plugin;

    public DefaultApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(@NonNull JavaPlugin plugin) {
        this.plugin = plugin;
        Set<Class<?>> classes = new HashSet<>();
        loadAllResources(new PluginDirectoryResourceLoader(plugin), classes);

        BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory();
        beanDefinitionFactory.createBeanDefinitions(classes);

        for (BeanDefinitionPostProcessor beanDefinitionPostProcessor : beanDefinitionFactory.getBeanDefinitionPostProcessors()) {
            beanDefinitionFactory.getBeanDefinitions().forEach(beanDefinitionPostProcessor::postProcess);
        }

        for(BeanPostProcessor beanPostProcessor : beanDefinitionFactory.getBeanPostProcessors()) {

        }

    }

}
