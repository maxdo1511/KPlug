package ru.kiscode.kplugdi.context.initializer;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Set;

public abstract class ApplicationContextInitializer {

    private ApplicationContext applicationContext;

    public ApplicationContextInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public abstract void initialize(JavaPlugin plugin);

    protected void createBeanDefinition(Class<?> clazz, Method configMethod) {

    }

    protected void createBeanDefinition(Class<?> clazz, Method configMethod, boolean shouldInstantiate) {

    }
}
