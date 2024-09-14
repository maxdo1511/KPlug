package ru.kiscode.kplugdi;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;

public final class KPlugDI extends JavaPlugin {

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
