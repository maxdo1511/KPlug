package ru.kiscode.kplugdi.context.initializer.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ComponentScan;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.initializer.ApplicationContextInitializer;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.DefaultResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.PluginDirectoryResourceLoader;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class DefaultApplicationContextInitializer extends ApplicationContextInitializer {

    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

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

        // плагин как бин
        beanDefinitions.add(getPluginBeanDefinition(plugin));

        //прочие бины
        beanDefinitions.addAll(beanDefinitionFactory.createBeanDefinitions(plugin));
    }

    @Override
    public void run(@NonNull JavaPlugin plugin) {
        //создаем beanDefinitions
        List<BeanDefinition> beanDefinitions = this.beanDefinitions.stream().filter(bd -> bd.getPlugin().getName().equals(plugin.getName())).collect(Collectors.toList());

        //ивент BeanDefinitionPostProcess
        beanDefinitions
                .forEach(beanDefinition -> beanProcessRegistry.getBeanDefinitionPostProcessors()
                        .forEach(beanDefinitionPostProcessor -> beanDefinitionPostProcessor.postProcess(beanDefinition)));

        //создаем бины
        beanRegistry.createBeans(beanDefinitions, plugin);
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
                .plugin(plugin)
                .name(plugin.getClass().getName())
                .beanClass(plugin.getClass())
                .implementInterfaces(implementInterfaces)
                .scope("singleton")
                .build();

    }
}
