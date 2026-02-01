package ru.example.foodsizeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSizeCommand implements CommandExecutor {

    private final FoodSizePlugin plugin;

    public ResetSizeCommand(FoodSizePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only OP can use this command
        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        plugin.resetPlayerScale(player);
        return true;
    }
}
