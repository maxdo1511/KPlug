package ru.kiscode.kplugdi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import ru.kiscode.kplugdi.context.ApplicationContext;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;


public class ServerStartListener implements Listener {

    //TODO обновить ивент на более нормальный
    @EventHandler(ignoreCancelled = true,priority = org.bukkit.event.EventPriority.HIGHEST)
    private void onServerStart(WorldLoadEvent event) {
        if (!event.getWorld().getName().equals("world")) return;
        logger.warning("ServerStart: " + event.getWorld().getName());
        ApplicationContext.refresh();
    }

}
