package ru.kiscode.kplugdi.context.reader;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.util.Storage;

public interface ConfigReader {

    String readValue(String path, String defaultValue);

}
