package ru.kiscode.kplugdi.context.initializer;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Bean;
import ru.kiscode.kplugdi.annotations.BeanConfiguration;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.bean.*;
import ru.kiscode.kplugdi.context.resource.DefaultResourceLoader;
import ru.kiscode.kplugdi.context.resource.PluginMainDirectoryResourceLoader;
import ru.kiscode.kplugdi.context.resource.ResourceLoader;
import ru.kiscode.kplugdi.minectaftutil.AbstractCommand;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultApplicationContextInitializer extends ApplicationContextInitializer {

    private JavaPlugin plugin;

    public DefaultApplicationContextInitializer(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void initialize(JavaPlugin plugin) {
        this.plugin = plugin;

        // Загрузка классов плагина
        Set<Class<?>> classes = new HashSet<>();
        loadAllResources(new PluginMainDirectoryResourceLoader(plugin), classes);

        // Загрузка конфигураций бинов
        Map<Class<?>, Method> beanConfigs = new HashMap<>();
        for (Class<?> clazz : classes) {
            BeanConfiguration beanConfiguration = clazz.getAnnotation(BeanConfiguration.class);
            if (beanConfiguration == null) continue;
            for (Method method : ReflectionUtil.getMethodsAnnotatedWith(clazz, Bean.class)) {
                if (beanConfigs.containsKey(clazz)) {
                    throw new RuntimeException("Bean already initialized. Class: " + clazz.getName());
                }
                beanConfigs.put(clazz, method);
            }
        }

        // Создание BeanDefinitionFactory
        BeanDefinitionFactory beanDefinitionFactory = new BeanDefinitionFactory(beanConfigs);

        // Инициализация бинов
        beanDefinitionFactory.createBeanDefinitions(classes);

        // Инициализация бино дефенишенов пост-процессорами
        for (BeanDefinitionPostProcessor beanDefinitionPostProcessor : beanDefinitionFactory.getBeanDefinitionPostProcessors()) {
            beanDefinitionFactory.getBeanDefinitions().forEach(beanDefinitionPostProcessor::postProcess);
        }

        // Создание бинов
        beanDefinitionFactory.getBeanDefinitions().forEach(beanDefinition -> {
            if (beanDefinition.getScope().equals(ScopeType.SINGLETON)) {
                if (beanDefinition.isShouldInstantiate()) {
                    Method method = beanDefinition.getBeanConfigMethod();
                    if (method != null) {
                        try {
                            Object configurationObject = method.getDeclaringClass().getConstructor().newInstance();
                            Object bean = method.invoke(configurationObject);

                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            throw new RuntimeException("Singleton Bean not created. Class: " + beanDefinition.getBeanClass().getName() + " has no default constructor", e);
                        }
                    } else {
                        try {
                            beanDefinition.getBeanConstructors().get(0).newInstance();
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("Singleton Bean not created. Class: " + beanDefinition.getBeanClass().getName() + " has no default constructor", e);
                        }
                    }
                }
            }
        });

        // Инициализация бинов пост-процессорами
        for (BeanPostProcessor beanPostProcessor : beanDefinitionFactory.getBeanPostProcessors()) {
            for (BeanDefinition beanDefinition : beanDefinitionFactory.getBeanDefinitions()) {

            }
        }



    }

    private void loadAllResources(ResourceLoader resourceLoader, Set<Class<?>> classes) {
        classes.addAll(resourceLoader.loadResource());
        for (Class<?> clazz : classes) {
            BeanConfiguration beanConfiguration = clazz.getAnnotation(BeanConfiguration.class);
            if (beanConfiguration != null) {
                if (!beanConfiguration.packageName().isEmpty()) {
                    loadAllResources(new DefaultResourceLoader(beanConfiguration.packageName()), classes);
                }
            }
        }
    }
}
