package ru.kiscode.kplugdi.test.bean;

import lombok.Getter;
import ru.kiscode.kplugdi.context.scope.ScopeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class BeanDefinition {

    private String name;
    private Class<?> beanClass;
    private boolean shouldInstantiate;
    private ScopeType scopeType;
    @Getter
    private Method beanConfigMethod;
    private List<Constructor<?>> beanConstructors;
    private List<Class<?>> implementedInterfaces;

    public BeanDefinition(String name, Class<?> beanClass, boolean shouldInstantiate, ScopeType scopeType, Method beanConfigMethod, List<Constructor<?>> beanConstructors, List<Class<?>> implementedInterfaces) {
        this.name = name;
        this.beanClass = beanClass;
        this.shouldInstantiate = shouldInstantiate;
        this.scopeType = scopeType;
        this.beanConfigMethod = beanConfigMethod;
        this.beanConstructors = beanConstructors;
        this.implementedInterfaces = implementedInterfaces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public boolean isShouldInstantiate() {
        return shouldInstantiate;
    }

    public void setShouldInstantiate(boolean shouldInstantiate) {
        this.shouldInstantiate = shouldInstantiate;
    }

    public ScopeType getScope() {
        return scopeType;
    }

    public void setScope(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public void setBeanConfigMethod(Method beanConfigMethod) {
        this.beanConfigMethod = beanConfigMethod;
    }

    public List<Constructor<?>> getBeanConstructors() {
        return beanConstructors;
    }

    public void setBeanConstructors(List<Constructor<?>> beanConstructors) {
        this.beanConstructors = beanConstructors;
    }

    public List<Class<?>> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    public void setImplementedInterfaces(List<Class<?>> implementedInterfaces) {
        this.implementedInterfaces = implementedInterfaces;
    }
}
