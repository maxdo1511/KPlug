package ru.kiscode.kplugdi.context.initializer.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ComponentScan;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.DefaultResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.PluginDirectoryResourceLoader;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.Arrays;
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

        // плагин как бин
        beanDefinitions.add(getPluginBeanDefinition(plugin));

        //ивент BeanDefinitionPostProcess
        beanDefinitions
                .forEach(beanDefinition -> beanProcessRegistry.getBeanDefinitionPostProcessors()
                        .forEach(beanDefinitionPostProcessor -> beanDefinitionPostProcessor.postProcess(beanDefinition)));

        //создаем бины
        applicationContext.getBeanRegistry().createAndRegistryBeans(beanDefinitions, plugin);

        // проходим по пост процессорам before initialization
        for (BeanDefinition beanDefinition : beanDefinitions) {
            BeanFactory beanFactory = beanDefinition.getBeanFactory();
            Object bean = ApplicationContext.getApplicationContext().getBeanRegistry().getBean(beanDefinition.getName(), plugin);

            for(BeanPostProcessor processor: beanFactory.getBeanProcessRegistry().getBeanPostProcessors()){
                bean =  processor.postProcessBeforeInitialization(bean,beanDefinition.getName(), plugin);
                if (bean == null) throw new BeanCreatingException("BeanPostProcessor return null: " + processor.getClass().getName());
            }
        }

        // проходит по пост процессорам after initialization
        for (BeanDefinition beanDefinition : beanDefinitions) {
            BeanFactory beanFactory = beanDefinition.getBeanFactory();
            Object bean = ApplicationContext.getApplicationContext().getBeanRegistry().getBean(beanDefinition.getName(), plugin);

            for(BeanPostProcessor processor: beanFactory.getBeanProcessRegistry().getBeanPostProcessors()){
                bean =  processor.postProcessAfterInitialization(bean,beanDefinition.getName(), plugin);
                if (bean == null) throw new BeanCreatingException("BeanPostProcessor return null: " + processor.getClass().getName());
            }
        }
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

    private BeanDefinition getPluginBeanDefinition(@NonNull JavaPlugin plugin) {
        Set<Class<?>> implementInterfaces = new HashSet<>(Arrays.asList(plugin.getClass().getInterfaces()));
        Class<?> superClass = plugin.getClass().getSuperclass();
        if(superClass != null) implementInterfaces.add(superClass);
        return PluginBeanDefinition.builder()
                .pluginInstance(plugin)
                .name(plugin.getClass().getName())
                .beanClass(plugin.getClass())
                .implementInterfaces(implementInterfaces)
                .scope("singleton")
                .build();

    }
}
