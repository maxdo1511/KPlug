package ru.kiscode.kplugdi.context.initializer.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ComponentScan;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
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

        BeanProcessRegistry beanProcessRegistry = new BeanProcessRegistry();
        beanProcessRegistry.register(classes);
        Set<BeanDefinition> beanDefinitions = new HashSet<>(applicationContext.getBeanDefinitionFactory().createBeanDefinitions(plugin));

        for (BeanDefinitionPostProcessor beanDefinitionPostProcessor : beanProcessRegistry.getBeanDefinitionPostProcessors()) {
            beanDefinitions.forEach(beanDefinitionPostProcessor::postProcess);
        }

        applicationContext.getBeanFactory().createBeans(beanDefinitions);
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
