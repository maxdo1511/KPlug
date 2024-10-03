package ru.kiscode.kplugdi.context.reader.impl;

import lombok.SneakyThrows;
 import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.reader.ConfigReader;
import ru.kiscode.kplugdi.utils.Storage;

import java.io.File;
import java.lang.reflect.Method;

public class DefaultConfigReader implements ConfigReader {

    private Storage storage;

    public DefaultConfigReader(JavaPlugin plugin){
        readStorage(plugin);
    }

    @SneakyThrows
    @Override
    public <T> T readValue(String path, Class<T> type, T defaultValue) {
        String methodName = "get" + type.getSimpleName();
        Method method = storage.getClass().getMethod(methodName, type);
        return type.cast(method.invoke(storage, defaultValue));
    }

    public void readStorage(JavaPlugin plugin) {
        storage = new Storage(plugin,"kplug.yml");
    }
}
