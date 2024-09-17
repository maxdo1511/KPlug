package ru.kiscode.kplugdi.context.reader;

public interface ConfigReader {

    <T> T readValue(String path, Class<T> type, T defaultValue);

}
