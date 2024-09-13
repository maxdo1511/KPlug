package ru.kiscode.kplugdi.context.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.kiscode.kplugdi.context.scope.ScopeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@Builder
public class BeanDefinition {

    private String name;
    private Class<?> beanClass;
    private ScopeType scopeType;
    private List<Class<?>> dependencyClasses;

    private Method beanConfigMethod;
    private Constructor<?> constructor;
    boolean extendFromAbstractClass;

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
