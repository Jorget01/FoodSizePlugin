package ru.example.foodsizeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final FoodSizePlugin plugin;

    public ReloadCommand(FoodSizePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foodsizeplugin.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        plugin.reloadPlugin();
        sender.sendMessage("§aFoodSizePlugin configuration reloaded!");
        return true;
    }
}
