package ru.kiscode.kplugdi.addons.builder;

import lombok.Data;

import java.io.File;

@Data
public class AddonBuildInfo {

    private String name;
    private String version;
    private boolean isDownload = false;
    private File file;
    private int byteArrayLength;

    public AddonBuildInfo(String addon) {
        this.name = addon;
    }

    public AddonBuildInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
