package ru.kiscode.kplugdi.context.initializer;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.ComponentScan;
import ru.kiscode.kplugdi.context.resource.ResourceLoader;
import ru.kiscode.kplugdi.context.resource.impl.DefaultResourceLoader;

import java.util.Set;

public interface ApplicationContextInitializer {

    void initialize(@NonNull JavaPlugin plugin);

    default void loadAllResources(@NonNull ResourceLoader resourceLoader, @NonNull Set<Class<?>> classes) {
        classes.addAll(resourceLoader.loadResource());

        for (Class<?> clazz : classes) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            if(componentScan == null) continue;
            String[] basePackages = componentScan.basePackages();
            if(basePackages == null) continue;
            for (String basePackage : basePackages) {
                loadAllResources(new DefaultResourceLoader(basePackage), classes);
            }
        }
    }

}
