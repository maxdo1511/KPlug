package ru.kiscode.kplugdi.context.initializer.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.BeanDefinitionFactory;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.resource.impl.PluginDirectoryResourceLoader;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
public class DefaultApplicationContextInitializer extends ApplicationContextInitializer {

    public DefaultApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void initialize(@NonNull JavaPlugin plugin) {
        Set<Class<?>> classes = new HashSet<>();
        loadAllResources(new PluginDirectoryResourceLoader(plugin), classes);

        BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory(plugin);
        beanDefinitionFactory.createBeanDefinitions(classes);

        for (BeanDefinitionPostProcessor beanDefinitionPostProcessor : beanDefinitionFactory.getBeanDefinitionPostProcessors()) {
            beanDefinitionFactory.getBeanDefinitions().forEach(beanDefinitionPostProcessor::postProcess);
        }

        beanFactory.createBeans(beanDefinitionFactory.getBeanDefinitions(), beanDefinitionFactory.getBeanPostProcessors());
    }
}
