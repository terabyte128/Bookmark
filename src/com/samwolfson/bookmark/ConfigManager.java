package com.samwolfson.bookmark;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {

    private final Player player;
    private final Plugin plugin;

    private final Map<String, Location> savedLocations = new HashMap<>();

    public ConfigManager(Player player, Plugin plugin) {
        this.player = player;
        this.plugin = plugin;

        String configNode = getConfigNode(player);

        ConfigurationSection locConfig = plugin.getConfig().getConfigurationSection(configNode);
        Map<String, Object> locationsFromConfig;

        if (locConfig != null) {
            locationsFromConfig = locConfig.getValues(true);

            for (Map.Entry<String, Object> location : locationsFromConfig.entrySet()) {
                savedLocations.put(location.getKey(), (Location) location.getValue());
            }
        }
    }

    public Map<String, Location> getSavedLocations() {
        return Collections.unmodifiableMap(savedLocations);
    }

    public Location getLocation(String name) {
        return savedLocations.get(name);
    }

    public void addLocation(String name, Location location) {
        savedLocations.put(name, location);
    }

    public void removeLocation(String name) {
        savedLocations.remove(name);
    }

    public boolean hasLocation(String name) {
        return savedLocations.containsKey(name);
    }

    public void saveConfig() {
        plugin.getConfig().createSection(getConfigNode(player), savedLocations);
        plugin.saveConfig();
    }


    private String getConfigNode(Player p) {
        return "players." + p.getName() + ".bookmarks";
    }

}
