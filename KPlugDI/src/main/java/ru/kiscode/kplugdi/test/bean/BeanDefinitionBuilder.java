package ru.kiscode.kplugdi.test.bean;

import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.annotations.Scope;
import ru.kiscode.kplugdi.context.scope.ScopeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeanDefinitionBuilder {

    private String name;
    private Class<?> beanClass;
    private boolean shouldInstantiate;
    private ScopeType scopeType;
    private Method beanConfigMethod;
    private List<Constructor<?>> beanConstructors;
    private List<Class<?>> implementedInterfaces;

    public BeanDefinitionBuilder setName(String name) {
        if (name == null) throw new IllegalArgumentException("Name cannot be null");
        this.name = name;
        return this;
    }

    public BeanDefinitionBuilder setBeanClass(Class<?> beanClass) {
        if (beanClass == null) throw new IllegalArgumentException("Bean class cannot be null");
        this.beanClass = beanClass;
        return this;
    }

    public BeanDefinitionBuilder setShouldInstantiate(boolean shouldInstantiate) {
        this.shouldInstantiate = shouldInstantiate;
        return this;
    }

    public BeanDefinitionBuilder setScope() {
        if (beanClass == null) throw new IllegalArgumentException("Bean class is null, scope cannot be set");
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope != null) {
            this.scopeType = scope.value();
        } else {
            this.scopeType = ScopeType.SINGLETON;
        }
        return this;
    }

    public BeanDefinitionBuilder setBeanConfigMethod(Method beanConfigMethod) {
        this.beanConfigMethod = beanConfigMethod;
        return this;
    }

    /**
     * Пустой конструктор должен быть у всех, он стоит в начале списка
     */
    public BeanDefinitionBuilder setBeanConstructors() {
        if (beanClass == null) throw new IllegalArgumentException("Bean class is null, bean constructors cannot be set");
        this.beanConstructors = Arrays.asList(beanClass.getConstructors());
        for (Constructor<?> constructor : beanConstructors) {
            if (constructor.getParameterCount() == 0) {
                this.beanConstructors.remove(constructor);
                this.beanConstructors.add(0, constructor);
                break;
            }
        }
        return this;
    }

    public BeanDefinitionBuilder setImplementedInterfaces() {
        if (beanClass == null) throw new IllegalArgumentException("Bean class is null, implemented interfaces cannot be set");
        implementedInterfaces = new ArrayList<>();
        addImplementedInterfacesRecursively(Arrays.asList(beanClass.getInterfaces()));
        return this;
    }

    public BeanDefinition build() {
        if (name == null) generateBeanName();
        return new BeanDefinition(
                name,
                beanClass,
                shouldInstantiate,
                scopeType,
                beanConfigMethod,
                beanConstructors,
                implementedInterfaces
        );
    }

    private void addImplementedInterfacesRecursively(List<Class<?>> interfaces) {
        for (Class<?> interfaceClass : interfaces) {
            if (interfaceClass.isInterface()) {
                addImplementedInterfacesRecursively( Arrays.asList(interfaceClass.getInterfaces()));
                this.implementedInterfaces.add(interfaceClass);
            }
        }
    }

    private void generateBeanName() {
        if (beanClass == null) throw new IllegalArgumentException("Bean class is null, name cannot be set");
        Component component = beanClass.getAnnotation(Component.class);
        if (component == null) throw new IllegalArgumentException("Bean class doesn't have @Component annotation");
        String name = component.name();
        if (name.isEmpty()) {
            this.name = beanClass.getSimpleName();
        }
    }
}
