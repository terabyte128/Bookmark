package com.samwolfson.bookmark;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerListener implements Listener {
    public static final String LOCATION_NAME = "rip";

    private final Plugin plugin;

    public PlayerListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ConfigManager configManager = new ConfigManager(event.getEntity(), plugin);
        configManager.addLocation(LOCATION_NAME, event.getEntity().getLocation());
        configManager.saveConfig();

        event.getEntity().sendMessage(ChatColor.GREEN + "Your death location was bookmarked as " + ChatColor.BOLD + LOCATION_NAME);
        event.getEntity().sendMessage(ChatColor.GREEN +  "You can navigate back to it with " + ChatColor.BOLD + "/bm nav " + LOCATION_NAME);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerNavTask.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        event.getPlayer().sendMessage(ChatColor.GREEN + "Since you changed worlds, your navigation was cancelled.");
        PlayerNavTask.removePlayer(event.getPlayer());
    }
}
