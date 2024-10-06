package ru.kiscode.kplugboot;

import net.sf.cglib.proxy.Enhancer;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;

public final class KPlugBoot extends JavaPlugin {

    @Override
    public void onEnable() {
        new Enhancer();
        saveDefaultConfig();
        ApplicationContext.run(this);
    }
}
