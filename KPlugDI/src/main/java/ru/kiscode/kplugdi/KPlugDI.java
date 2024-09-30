package ru.kiscode.kplugdi;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;

@Getter
public final class KPlugDI extends JavaPlugin {
    private final BeanRegistry beanRegistry = new BeanRegistry();

    public static KPlugDI getInstance() {
        return getPlugin(KPlugDI.class);
    }

    @Override
    public void onEnable() {
        ApplicationContext.run(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
