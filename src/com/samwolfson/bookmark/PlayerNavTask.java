package com.samwolfson.bookmark;

import com.samwolfson.bookmark.locatable.Locatable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerNavTask implements Runnable {
    private static final ConcurrentHashMap<Player, Locatable> playerList = new ConcurrentHashMap<>();
    private static JavaPlugin plugin;
    private static int task = -1;

    @Override
    public void run() {
        Set<Player> toRemove = new HashSet<>(); // remove players that have reached their dest

        for (Map.Entry<Player, Locatable> pl : playerList.entrySet()) {
            double dist = pl.getKey().getLocation().distance(pl.getValue().getLocation());

            if (((int) dist) == 0) {
                toRemove.add(pl.getKey());
                continue;
            }

            /*
            Gets the yaw of this location, measured in degrees.
            A yaw of 0 or 360 represents the positive z direction.
            A yaw of 180 represents the negative z direction.
            A yaw of 90 represents the negative x direction.
            A yaw of 270 represents the positive x direction.
            Increasing yaw values are the equivalent of turning to your right-facing,
            increasing the scale of the next respective axis, and decreasing the scale of the previous axis.
             */
            double yaw = pl.getKey().getLocation().getYaw();
            // for some reason, yaw is not normalized between 0 and 360,
            // so fix that
            yaw = normalAbsoluteAngleDegrees(yaw);


            double dx = -(pl.getKey().getLocation().getX() - pl.getValue().getLocation().getX()); // positive x is downwards
            double dy = pl.getValue().getLocation().getY() - pl.getKey().getLocation().getY();
            double dz = pl.getKey().getLocation().getZ() - pl.getValue().getLocation().getZ();

            double angleToDest = Math.toDegrees(Math.atan2(dx, dz));
            angleToDest = normalAbsoluteAngleDegrees(180+angleToDest);

            // the angle between where the player is facing and the destination
            // normalized to between 0 and 360
            // 0 or 360 are facing towards the block
            // 90 is facing right
            // 180 is facing backwards
            double angleFacingDest =  normalAbsoluteAngleDegrees(yaw - angleToDest);

            // convert degrees to positions on a clock face
            int clockPos = (int) (angleFacingDest + 15) / 30;
            clockPos = 12 - clockPos;   // angle that player must *turn* to face dest
            clockPos = (clockPos == 0) ? 12 : clockPos;

            String yDir;

            if (dy > 0) yDir = "upward";
            else if (dy < 0) yDir = "downward";
            else yDir = "straight ahead";

            pl.getKey().sendTitle(
                    "",
                     clockPos + " o'clock in " + (int) dist + " blocks " + "(" + yDir + ")",
                    0, Integer.MAX_VALUE, 0);
        }

        for (Player p : toRemove) {
            removePlayer(p);
        }

    }

    public static void addPlayer(Player p, Locatable dest) {
        playerList.put(p, dest);

        // if this is the first player, schedule the task
        if (task == -1) {
            task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new PlayerNavTask(), 0L, 10L);
        }
    }

    public static void removePlayer(Player p) {
        playerList.remove(p);
        p.sendTitle("", "", 20, 0, 20);

        // if the player list is now empty, remove the task
        if (playerList.isEmpty()) {
            plugin.getServer().getScheduler().cancelTask(task);
            task = -1;
        }
    }

    public static void setPlugin(JavaPlugin p) {
        plugin = p;
    }

    private static double normalAbsoluteAngleDegrees(double angle) {
        return (angle %= 360) >= 0 ? angle : (angle + 360);
    }
}
