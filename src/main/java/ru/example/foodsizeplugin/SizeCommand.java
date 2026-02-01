package ru.example.foodsizeplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SizeCommand implements CommandExecutor {

    private final FoodSizePlugin plugin;

    public SizeCommand(FoodSizePlugin plugin) {
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

        if (args.length == 0) {
            // Show current size
            double currentScale = plugin.getPlayerScale(player);
            String message = plugin.getMessage("message-current", "§aТвой текущий размер: §6%size%%");
            message = plugin.formatMessage(message, "%size%", String.format("%.0f%%", currentScale * 100));
            player.sendMessage(message);
            return true;
        }

        try {
            double scale = Double.parseDouble(args[0]);
            
            // Get max scale from config
            double maxScale = plugin.getMaxScale() * 100;
            double minScale = plugin.getMinScale() * 100;
            
            // Check range
            if (scale < minScale || scale > maxScale) {
                String message = plugin.getMessage("message-error-range", "§cРазмер должен быть от %min%% до %max%%!");
                message = plugin.formatMessage(message, "%min%", String.format("%.0f%%", minScale), "%max%", String.format("%.0f%%", maxScale));
                player.sendMessage(message);
                return true;
            }

            double normalizedScale = scale / 100.0;
            plugin.setPlayerScale(player, normalizedScale);
            String message = plugin.getMessage("message-set", "§aТвой размер установлен на: §6%size%%");
            message = plugin.formatMessage(message, "%size%", String.format("%.0f%%", scale));
            player.sendMessage(message);

        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("message-error-number", "§cПожалуйста, введите корректное число!"));
        }

        return true;
    }
}
