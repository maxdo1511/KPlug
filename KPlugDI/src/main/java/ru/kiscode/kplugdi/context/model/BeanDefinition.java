package ru.kiscode.kplugdi.context.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.factory.BeanFactory;

import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@SuperBuilder
public abstract class BeanDefinition {

    private String name;
    private Class<?> beanClass;
    private String scope;
    private Set<Class<?>> implementInterfaces;
    private BeanFactory beanFactory;
    private JavaPlugin plugin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
