package ru.kiscode.kplugdi.context.processor.beanpostprocessors;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ConfigurationProperties;
import ru.kiscode.kplugdi.annotations.Value;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.reader.ConfigReader;
import ru.kiscode.kplugdi.context.reader.impl.DefaultConfigReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationPropertiesBeanPostProcessor implements BeanPostProcessor {

    private Map<JavaPlugin, ConfigReader> configReaders = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        ConfigurationProperties annotation = bean.getClass().getAnnotation(ConfigurationProperties.class);
        if (annotation != null) {
            ConfigReader configReader = getConfigReader(plugin);
            for (Field field : bean.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object configParam = configReader.readValue(field.getName(), field.getType(), null);
                    field.set(bean, configParam);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't access field " + field.getName(), e);
                }
            }

            return bean;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        Field[] fields = bean.getClass().getDeclaredFields();
        ConfigReader configReader = getConfigReader(plugin);
        for (Field field : fields) {
            field.setAccessible(true);
            Value value = field.getAnnotation(Value.class);
            if (value != null) {
                try {
                    Object configParam = configReader.readValue(value.value(), field.getType(), null);
                    field.set(bean, configParam);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't access field " + field.getName(), e);
                }
            }
        }
        return bean;
    }

    private ConfigReader getConfigReader(JavaPlugin plugin) {
        ConfigReader configReader = configReaders.get(plugin);
        if (configReader == null) {
            configReader = new DefaultConfigReader(plugin);
            configReaders.put(plugin, configReader);
        }
        return configReader;
    }
}
