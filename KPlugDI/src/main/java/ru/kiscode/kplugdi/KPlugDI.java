package ru.kiscode.kplugdi;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.registry.BeanProcessRegistry;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.minecraftevents.ServerStart;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

@Getter
public final class KPlugDI extends JavaPlugin {
    private final BeanRegistry beanRegistry = new BeanRegistry();
    private final BeanProcessRegistry beanProcessRegistry = new BeanProcessRegistry();

    public static KPlugDI getInstance() {
        return getPlugin(KPlugDI.class);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new ServerStart(), this);
        ApplicationContext.run(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static <T> T getBean(Class<T> type, JavaPlugin plugin) {
        return KPlugDI.getInstance().getBeanRegistry().getBean(type, plugin);
    }

}
