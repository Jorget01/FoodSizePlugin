package ru.example.foodsizeplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Plugin for Minecraft Paper 1.21.8 that changes player size based on eaten food.
 * Uses vanilla /attribute command for size modification.
 * Configuration: config.yml and messages.yml
 */
public final class FoodSizePlugin extends JavaPlugin implements Listener {

    private double MIN_SCALE = 0.3;
    private double MAX_SCALE = 3.0;
    private double DEFAULT_SCALE = 1.0;
    private boolean MESSAGE_ENABLED = true;
    private boolean PARTICLES_ENABLED = true;
    
    private final Map<UUID, Double> playerScales = new HashMap<>();
    private final Map<Material, Double> foodScaleMap = new HashMap<>();
    
    // Messages configuration
    private YamlConfiguration messages;
    private File messagesFile;

    @Override
    public void onEnable() {
        getLogger().info(getMessage("message-enable", "FoodSizePlugin enabled!"));
        
        // Load configuration files
        saveDefaultConfig();
        loadConfiguration();
        loadMessages();
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register commands
        getCommand("size").setExecutor(new SizeCommand(this));
        getCommand("resetsize").setExecutor(new ResetSizeCommand(this));
        getCommand("sizereload").setExecutor(new ReloadCommand(this));
        
        getLogger().info("Food effects loaded: " + foodScaleMap.size() + " items");
    }

    @Override
    public void onDisable() {
        getLogger().info(getMessage("message-disable", "FoodSizePlugin disabled!"));
        playerScales.clear();
    }

    private void loadConfiguration() {
        // Load settings
        ConfigurationSection settings = getConfig().getConfigurationSection("settings");
        if (settings != null) {
            MIN_SCALE = settings.getDouble("min-scale", 0.3);
            MAX_SCALE = settings.getDouble("max-scale", 3.0);
            DEFAULT_SCALE = settings.getDouble("default-scale", 1.0);
            MESSAGE_ENABLED = settings.getBoolean("message-enabled", true);
            PARTICLES_ENABLED = settings.getBoolean("particles-enabled", true);
        }

        // Load food effects
        foodScaleMap.clear();
        for (String key : getConfig().getKeys(false)) {
            if (key.equals("settings")) continue;
            
            try {
                Material material = Material.valueOf(key);
                double value = getConfig().getDouble(key, 0.0);
                foodScaleMap.put(material, value);
            } catch (IllegalArgumentException e) {
                // Skip unknown materials
            }
        }
    }

    private void loadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Reload all configuration (can be called from command)
     */
    public void reloadPlugin() {
        reloadConfig();
        loadConfiguration();
        loadMessages();
        getLogger().info(getMessage("message-reload-console", "Configuration reloaded!"));
    }

    /**
     * Get message from messages.yml with fallback
     */
    public String getMessage(String key, String fallback) {
        if (messages != null && messages.contains(key)) {
            return messages.getString(key, fallback);
        }
        return fallback;
    }

    /**
     * Format message with variables
     */
    public String formatMessage(String message, String... vars) {
        String result = message;
        for (int i = 0; i < vars.length; i += 2) {
            result = result.replace(vars[i], vars[i + 1]);
        }
        return result;
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material food = event.getItem().getType();
        
        if (foodScaleMap.containsKey(food)) {
            double scaleChange = foodScaleMap.get(food);
            
            // If value is 0, no change
            if (scaleChange == 0) return;
            
            double currentScale = getPlayerScale(player);
            double newScale = calculateNewScale(currentScale, scaleChange);
            
            setPlayerScale(player, newScale);
            
            if (MESSAGE_ENABLED) {
                String percentage = String.format("%.0f%%", newScale * 100);
                String messageKey = scaleChange > 0 ? "message-increase" : "message-decrease";
                String message = getMessage(messageKey, 
                    scaleChange > 0 ? "§6Твой размер увеличился до %size%% от нормы!" : "§6Твой размер уменьшился до %size%% от нормы!");
                message = formatMessage(message, "%size%", percentage);
                player.sendMessage(message);
            }
            
            if (PARTICLES_ENABLED) {
                addVisualEffects(player);
            }
        }
    }

    private double calculateNewScale(double currentScale, double change) {
        double newScale = currentScale + change;
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, newScale));
    }

    public double getPlayerScale(Player player) {
        return playerScales.getOrDefault(player.getUniqueId(), DEFAULT_SCALE);
    }

    public void setPlayerScale(Player player, double scale) {
        playerScales.put(player.getUniqueId(), scale);
        applyScale(player, scale);
    }

    private void applyScale(Player player, double scale) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "attribute " + player.getName() + " minecraft:scale base set " + scale);
    }

    public void resetPlayerScale(Player player) {
        playerScales.remove(player.getUniqueId());
        applyScale(player, DEFAULT_SCALE);
        player.sendMessage(getMessage("message-reset", "§aТвой размер сброшен к норме!"));
    }

    private void addVisualEffects(Player player) {
        player.getWorld().spawnParticle(
            org.bukkit.Particle.HAPPY_VILLAGER,
            player.getLocation().add(0, 1, 0),
            10, 0.5, 0.5, 0.5, 0.1
        );
    }

    // Getters for settings
    public double getMinScale() { return MIN_SCALE; }
    public double getMaxScale() { return MAX_SCALE; }
    public double getDefaultScale() { return DEFAULT_SCALE; }
}
