package ru.kiscode.kplugdi.utils;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.kiscode.kplugdi.KPlugDI;

import java.io.*;
import java.nio.file.Files;

public class Storage {

    private final File file;
    @Getter
    private FileConfiguration config;

    public Storage(String name){
        file = new File(KPlugDI.getInstance().getDataFolder(), name);
        try {
            if(!file.exists() && !file.createNewFile()) throw new IOException();
        } catch (IOException e){
            throw new RuntimeException("Failed to create file", e);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getName() {return file.getName();}

    public void save(){
        try {
            config.save(file);
        } catch (IOException e){
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public static void savebackup(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = Files.newInputStream(source.toPath());
            os = Files.newOutputStream(dest.toPath());
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            assert is != null;
            is.close();
            assert os != null;
            os.close();
        }
}
}
