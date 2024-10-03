package ru.kiscode.kplugdi.context.resource.impl;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Component;
import ru.kiscode.kplugdi.context.resource.CollectionResourceLoader;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.util.List;
import java.util.Set;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;

public class PluginDirectoryResourceLoader implements CollectionResourceLoader<Class<?>> {

    private final JavaPlugin plugin;

    public PluginDirectoryResourceLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<Class<?>> loadResource() {
        ReflectionUtil reflectionUtil = new ReflectionUtil(plugin);
        List<Class<?>> classes = reflectionUtil.getAllClasses();
        logger.warning("PluginDirectoryResourceLoader: " + plugin.getName() + " Found classes: " + classes.size());
        return classes;
    }

}
