package com.samwolfson.bookmark;

import com.samwolfson.bookmark.commands.BookmarkCommand;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class Bookmark extends JavaPlugin {

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        AssassinDetector detector = new AssassinDetector(this.getServer().getPluginManager());
        this.saveDefaultConfig();   // load default config file if not exists

        BookmarkCommand bookmarkCommand = new BookmarkCommand(this, detector);

        this.getCommand("bookmark").setExecutor(bookmarkCommand);
        this.getCommand("bookmark").setTabCompleter(bookmarkCommand);

        PlayerNavTask.setPlugin(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public static String prettyLocation(Location l) {
        return "X: " + l.getBlockX() +
                " Y: " + l.getBlockY() +
                " Z: " + l.getBlockZ();
    }
}
