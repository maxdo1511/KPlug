package ru.kiscode.kplugdi.context.resource.impl;

import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.context.resource.ResourceLoader;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.util.Set;

public class DefaultResourceLoader implements ResourceLoader {

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
