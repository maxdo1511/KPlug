package ru.kiscode.kplugdi.context.reader;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface ConfigReader {

    <T> T readValue(String path, Class<T> type, T defaultValue);

}
