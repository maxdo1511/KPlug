package ru.kiscode.kplugboot.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.stream.Collectors;

public interface KPlugCommand {

    default void noArgs(CommandSender sender) {};
    default void wrongArgs(CommandSender sender) {};
    default List<String> tabComplete(CommandSender sender, String[] args) {return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());};

}
