package ru.kiscode.kplugdi.context.resource.impl;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.util.Set;

public class PluginDirectoryResourceLoader implements CollectionResourceLoader<Class<?>> {

    private final JavaPlugin plugin;

    public PluginDirectoryResourceLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<Class<?>> loadResource() {
        ReflectionUtil reflectionUtil = new ReflectionUtil(plugin);
        return reflectionUtil.getAllClasses();
    }

}
