package ru.kiscode.kplugdi.pluginutil.commands;

import org.bukkit.command.CommandSender;
import ru.kiscode.kplugdi.pluginutil.commands.annotations.Command;
import ru.kiscode.kplugdi.pluginutil.commands.annotations.CommandParam;
import ru.kiscode.kplugdi.pluginutil.commands.annotations.CommandParams;

@Command(command = "test")
public class TestCommand {

    public void noArgs(CommandSender sender) {
        System.out.println("No args");
    }

    @CommandParams(args = "player {player} {action} {points}")
    public void setPlayerPoints(CommandSender sender, @CommandParam String player, @CommandParam String action, @CommandParam int points) {
        System.out.println(player + " " + action + " " + points);
    }

    @CommandParams(args = "player {name}")
    public void helloPlayer(CommandSender sender, @CommandParam String name) {
        System.out.println("Hello " + name);
    }
}
