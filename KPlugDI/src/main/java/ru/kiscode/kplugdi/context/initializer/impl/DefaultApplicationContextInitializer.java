package ru.kiscode.kplugdi.context.initializer.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ComponentScan;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.DefaultResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.PluginDirectoryResourceLoader;

import java.util.HashSet;
import java.util.Set;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;

@Getter
@Setter
public class DefaultApplicationContextInitializer extends ApplicationContextInitializer {

    public DefaultApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        super(applicationContext);
    }


    @Override
    public void initialize(@NonNull JavaPlugin plugin) {
        //загружаем нужные классы
        Set<Class<?>> classes = new HashSet<>();
        loadAllResources(new PluginDirectoryResourceLoader(plugin), classes);

        //регистрируем необходимые процессы, ивенты и конверт классы и выгружаем из контекста наружу
        beanProcessRegistry.findAndRegisterProcessors(classes, plugin);
    }

    @Override
    public void run(@NonNull JavaPlugin plugin) {
        //создаем beanDefinitions
        Set<BeanDefinition> beanDefinitions = new HashSet<>(applicationContext.getBeanDefinitionFactory().createBeanDefinitions(plugin));

        //ивент BeanDefinitionPostProcess
        beanDefinitions
                .forEach(beanDefinition -> beanProcessRegistry.getBeanDefinitionPostProcessors()
                        .forEach(beanDefinitionPostProcessor -> beanDefinitionPostProcessor.postProcess(beanDefinition)));

        //создаем бины
        applicationContext.getBeanRegistry().createAndRegistryBeans(beanDefinitions,plugin);
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
