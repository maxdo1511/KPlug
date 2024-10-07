package ru.kiscode.kplugdi;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.listener.ServerStartListener;

@Getter
public final class KPlugDI extends JavaPlugin {
    @Getter
    private static KPlugDI instance;
    private final BeanProcessRegistry beanProcessRegistry = new BeanProcessRegistry();
    private final BeanRegistry beanRegistry = new BeanRegistry(beanProcessRegistry);

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new ServerStartListener(), this);
        ApplicationContext.run(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
