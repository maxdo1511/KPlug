package ru.kiscode.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import ru.kiscode.commands.annotations.Command;
import ru.kiscode.commands.annotations.CommandParam;
import ru.kiscode.commands.annotations.CommandParams;
import ru.kiscode.commands.interfaces.KPlugCommand;

import java.util.ArrayList;
import java.util.List;

@Command(command = "test")
public class TestCommand implements KPlugCommand {


    @CommandParams(args = "player {name} {action} {points}")
    public void setPlayerPoints(CommandSender sender, @CommandParam String name, @CommandParam String action, @CommandParam int points) {
        System.out.println(name + " " + action + " " + points);
    }

    @CommandParams(args = "player {name}")
    public void helloPlayer(CommandSender sender, @CommandParam String name) {
        System.out.println("Hello " + name);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("player");
        }
        if (args.length == 2) {
            return Lists.newArrayList("Kisapa", "Hbb");
        }
        if (args.length == 3) {
            return Lists.newArrayList("set", "add", "remove");
        }
        if (args.length == 4) {
            return Lists.newArrayList("<value>");
        }
        return new ArrayList<>();
    }
}
