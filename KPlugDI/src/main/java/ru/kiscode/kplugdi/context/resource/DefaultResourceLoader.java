package ru.kiscode.kplugdi.context.resource;

import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.util.Set;

public class DefaultResourceLoader implements ResourceLoader {

    private String path;

    public DefaultResourceLoader(String path) {
        this.path = path;
    }

    @Override
    public Set<Class<?>> loadResource() {
        ReflectionUtil reflectionUtil = new ReflectionUtil(path, KPlugDI.class.getClassLoader());
        return reflectionUtil.getAllClasses();
    }
}
