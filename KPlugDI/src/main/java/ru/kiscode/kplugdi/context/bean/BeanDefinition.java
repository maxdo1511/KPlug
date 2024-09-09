package ru.kiscode.kplugdi.context.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class BeanDefinition {

    private String name;
    private Class<?> beanClass;
    private boolean shouldInstantiate;
    private BeanScope scope;
    private Method beanConfigMethod;
    private List<Constructor<?>> beanConstructors;
    private List<Field> beanFields;
    private List<Method> beanMethods;

}
