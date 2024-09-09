package ru.kiscode.kplugdi.context.initializer;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Bean;
import ru.kiscode.kplugdi.annotations.BeanConfiguration;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.minectaftutil.AbstractCommand;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultApplicationContextInitializer extends ApplicationContextInitializer {

    public DefaultApplicationContextInitializer(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void initialize(JavaPlugin plugin) {
        ReflectionUtil reflectionUtil = new ReflectionUtil(plugin);

        Map<Class<?>, Method> beanConfigs = new HashMap<>();
        for (Class<?> clazz : reflectionUtil.getClassesAnnotatedWith(BeanConfiguration.class)) {
            for (Method method : reflectionUtil.getMethodsAnnotatedWith(clazz, Bean.class)) {
                beanConfigs.put(clazz, method);
            }
        }

        Set<Class<?>> classes = reflectionUtil.getAllPluginClasses();

        for (Class<?> clazz : classes) {
            Component component = clazz.getAnnotation(Component.class);
            if (component != null) {
                createBeanDefinition(clazz, beanConfigs.get(clazz));
                continue;
            }

            if (Listener.class.isAssignableFrom(clazz)) {
                createBeanDefinition(clazz, beanConfigs.get(clazz));
                continue;
            }

            if (AbstractCommand.class.isAssignableFrom(clazz)) {
                createBeanDefinition(clazz, beanConfigs.get(clazz));
                continue;
            }
        }
    }
}
