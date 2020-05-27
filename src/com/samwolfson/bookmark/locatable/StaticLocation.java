package com.samwolfson.bookmark.locatable;

import org.bukkit.Location;

public class StaticLocation implements Locatable {
    private Location location;

    public StaticLocation(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
