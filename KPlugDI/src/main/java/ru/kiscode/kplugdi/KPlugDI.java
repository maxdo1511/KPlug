package ru.kiscode.kplugdi;

import org.bukkit.plugin.java.JavaPlugin;

public final class KPlugDI extends JavaPlugin {

    public static KPlugDI getInstance() {
        return getPlugin(KPlugDI.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
