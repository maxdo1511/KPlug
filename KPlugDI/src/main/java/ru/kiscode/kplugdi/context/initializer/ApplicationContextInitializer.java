package ru.kiscode.kplugdi.context.initializer;

import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;

public abstract class ApplicationContextInitializer {
    protected final ApplicationContext applicationContext;

    public ApplicationContextInitializer(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public abstract void initialize(@NonNull JavaPlugin plugin);


}
