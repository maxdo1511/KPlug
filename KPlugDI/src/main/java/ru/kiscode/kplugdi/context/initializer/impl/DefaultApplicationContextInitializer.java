package ru.kiscode.kplugdi.context.initializer.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ComponentScan;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.definition.DefaultBeanDefinitionFactory;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.registry.BeanProcessorRegistry;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.DefaultResourceLoader;
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

        BeanProcessorRegistry beanProcessorRegistry = new BeanProcessorRegistry();
        beanProcessorRegistry.register(classes);

        Set<BeanDefinition> beanDefinitions = new HashSet<>(applicationContext.getBeanDefinitionFactory().createBeanDefinitions(beanProcessorRegistry,plugin));

        for (BeanDefinitionPostProcessor beanDefinitionPostProcessor : beanProcessorRegistry.getBeanDefinitionPostProcessors()) {
            beanDefinitions.forEach(beanDefinitionPostProcessor::postProcess);
        }

        applicationContext.getBeanFactory().createBeans(beanDefinitions, beanProcessorRegistry);
    }

    private void loadAllResources(@NonNull CollectionResourceLoader<Class<?>> resourceLoader, @NonNull Set<Class<?>> classes) {
        classes.addAll(resourceLoader.loadResource());

        for (Class<?> clazz : classes) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            if(componentScan == null) continue;
            String[] basePackages = componentScan.basePackages();
            if(basePackages == null) continue;
            for (String basePackage : basePackages) {
                loadAllResources(new DefaultResourceLoader(basePackage), classes);
            }
        }
    }
}
