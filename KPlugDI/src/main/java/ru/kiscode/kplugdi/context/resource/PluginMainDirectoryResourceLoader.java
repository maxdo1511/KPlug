package ru.kiscode.kplugdi.context.resource;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.util.Set;

public class PluginMainDirectoryResourceLoader implements ResourceLoader {

    private JavaPlugin plugin;

    public PluginMainDirectoryResourceLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<Class<?>> loadResource() {
        ReflectionUtil reflectionUtil = new ReflectionUtil(plugin);
        return reflectionUtil.getAllClasses();
    }

}
