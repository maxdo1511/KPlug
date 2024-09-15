package ru.kiscode.kplugdi.context.processor.beanpostprocessors;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ConfigurationProperties;
import ru.kiscode.kplugdi.annotations.Value;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.reader.ConfigReader;
import ru.kiscode.kplugdi.context.reader.impl.DefaultConfigReader;

import java.lang.reflect.Field;

public class ConfigurationPropertiesBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        ConfigurationProperties annotation = bean.getClass().getAnnotation(ConfigurationProperties.class);
        if (annotation != null) {
            ConfigReader configReader = new DefaultConfigReader(plugin);
            for (Field field : bean.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                //TODO позаботиться о преобразовании типов
                try {
                    field.set(bean, configReader.readValue(field.getName(), ""));
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
        for (Field field : fields) {
            field.setAccessible(true);
            Value value = field.getAnnotation(Value.class);
            if (value != null) {
                // TODO перенести как-то синглтон, чтобы не открывать 1000 раз
                ConfigReader configReader = new DefaultConfigReader(plugin);
                //TODO позаботиться о преобразовании типов
                try {
                    field.set(bean, configReader.readValue(value.value(), value.defaultValue()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Can't access field " + field.getName(), e);
                }
            }
        }
        return bean;
    }
}
