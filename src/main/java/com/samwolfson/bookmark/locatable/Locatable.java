package com.samwolfson.bookmark.locatable;

import org.bukkit.Location;

/**
 * Locatable represents anything that can be located.
 * E.g., static locations or player locations.
 */
public interface Locatable {
    public Location getLocation();
}
