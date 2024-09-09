package ru.kiscode.kplugdi.addons;


import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.addons.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class AddonExtractor<T> {

    private final File file;
    private final Class<T> clazz;

    public AddonExtractor(String fileName, Class<T> clazz) {
        File file = new File(fileName);
        if (file.isFile()) {
            this.file = file;
        } else {
            throw new IllegalArgumentException("Addon directory not found: " + fileName);
        }
        this.clazz = clazz;
    }

    public AddonExtractor(File file, Class<T> clazz) {
        if (file.isFile()) {
            this.file = file;
        } else {
            throw new IllegalArgumentException("Addon directory not found: " + file);
        }
        this.clazz = clazz;
    }

    public Class<? extends T> extract() throws IOException, ClassNotFoundException {
        Class<? extends T> loaded = FileUtil.findClass(file, clazz);
        if (loaded == null) {
            KPlugDI.getInstance().getLogger().warning("Main class not found: " + file.getName() + ". Please, inherit addon class from KAddon");
        }
        return loaded;
    }
}
