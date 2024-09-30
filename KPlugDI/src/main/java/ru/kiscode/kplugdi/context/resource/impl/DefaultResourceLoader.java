package ru.kiscode.kplugdi.context.resource.impl;

import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.util.Set;

public class DefaultResourceLoader implements CollectionResourceLoader<Class<?>> {

    private final String path;

    public DefaultResourceLoader(String path) {
        this.path = path;
    }

    @Override
    public Set<Class<?>> loadResource() {
        ReflectionUtil reflectionUtil = new ReflectionUtil(path, KPlugDI.class.getClassLoader());
        return reflectionUtil.getAllClasses();
    }
}
