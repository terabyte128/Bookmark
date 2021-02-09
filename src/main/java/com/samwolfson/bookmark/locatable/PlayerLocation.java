package com.samwolfson.bookmark.locatable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerLocation implements Locatable {

    private Player player;

    public PlayerLocation(Player player) {
        this.player = player;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }
}
