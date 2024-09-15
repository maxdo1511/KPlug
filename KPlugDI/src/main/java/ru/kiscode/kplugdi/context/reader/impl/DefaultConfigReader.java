package ru.kiscode.kplugdi.context.reader.impl;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.reader.ConfigReader;
import ru.kiscode.kplugdi.util.Storage;

import java.io.File;

public class DefaultConfigReader implements ConfigReader {

    private Storage storage;

    public DefaultConfigReader(JavaPlugin plugin){
        readStorage(plugin);
    }

    @Override
    public String readValue(JavaPlugin plugin, String path, String defaultValue) {
        return storage.getConfig().getString(path, defaultValue);
    }

    public void readStorage(JavaPlugin plugin) {
        storage = new Storage(new File(plugin.getDataFolder(), "kplug.yml").getPath());
    }
}
