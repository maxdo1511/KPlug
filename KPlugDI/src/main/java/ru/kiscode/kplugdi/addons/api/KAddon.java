package ru.kiscode.kplugdi.addons.api;

public abstract class KAddon {

    protected String name;

    public KAddon(String name) {
        this.name = name;
    }

    public abstract void onLoad();

    public String getName() {
        return name;
    }

}
