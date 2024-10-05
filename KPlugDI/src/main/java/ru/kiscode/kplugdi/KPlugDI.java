package ru.kiscode.kplugdi;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.listener.ServerStartListener;

@Getter
public final class KPlugDI extends JavaPlugin {
    private final BeanProcessRegistry beanProcessRegistry = new BeanProcessRegistry();
    private final BeanRegistry beanRegistry = new BeanRegistry(beanProcessRegistry);

    public static KPlugDI getInstance() {
        return getPlugin(KPlugDI.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new ServerStartListener(), this);
        ApplicationContext.run(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
