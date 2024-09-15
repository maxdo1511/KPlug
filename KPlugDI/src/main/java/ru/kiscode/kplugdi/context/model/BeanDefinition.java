package ru.kiscode.kplugdi.context.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.kiscode.kplugdi.context.scope.ScopeType;

import java.util.Objects;


@Getter
@Setter
@SuperBuilder
public abstract class BeanDefinition {

    private String name;
    private Class<?> beanClass;
    private ScopeType scopeType;
    private Class<?>[] implementInterfaces;

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
