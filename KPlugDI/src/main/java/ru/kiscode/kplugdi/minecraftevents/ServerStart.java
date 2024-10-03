package ru.kiscode.kplugdi.minecraftevents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import ru.kiscode.kplugdi.context.ApplicationContext;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;

public class ServerStart implements Listener {

    @EventHandler
    private void onServerStart(WorldLoadEvent event) {
        if (!event.getWorld().getName().equals("world")) return;
        logger.warning("ServerStart: " + event.getWorld().getName());
        ApplicationContext.refresh();
    }

}
