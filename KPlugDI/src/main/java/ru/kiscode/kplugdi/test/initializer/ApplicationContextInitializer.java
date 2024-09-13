package ru.kiscode.kplugdi.test.initializer;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.test.ApplicationContext;

import java.lang.reflect.Method;

public abstract class ApplicationContextInitializer {

    protected ApplicationContext applicationContext;

    public ApplicationContextInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public abstract void initialize(JavaPlugin plugin);

    protected void createBeanDefinition(Class<?> clazz, Method configMethod) {

    }

    protected void createBeanDefinition(Class<?> clazz, Method configMethod, boolean shouldInstantiate) {

    }
}
