package com.samwolfson.bookmark;

import java.lang.reflect.Field;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AssassinDetector {
    Object assassinModel;
    PluginManager pluginManager;

    public AssassinDetector(PluginManager pluginManager) {
        this.pluginManager = pluginManager;

        try {
            JavaPlugin assassin = (JavaPlugin) pluginManager.getPlugin("Assassin");
            CommandExecutor assassinExecutor = assassin.getCommand("ass").getExecutor();

            Class<?> clazz = assassinExecutor.getClass();
            Field modelField = clazz.getDeclaredField("model");
            modelField.setAccessible(true);

            assassinModel = modelField.get(assassinExecutor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean gameInProgress() {
        try {
            Class<?> clazz = assassinModel.getClass();
            Object isStarted = clazz.getMethod("isGameStarted").invoke(assassinModel);
            Object isOver = clazz.getMethod("isGameOver").invoke(assassinModel);
            return (Boolean) isStarted && !((Boolean) isOver);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
