package ru.kiscode.kplugdi.context.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class BeanDefinition {

    private String name;
    private Class<?> beanClass;
    private boolean shouldInstantiate;
    private Scope scope;
    private Method beanConfigMethod;
    private List<Constructor<?>> beanConstructors;
    private List<Class<?>> implementedInterfaces;

}
