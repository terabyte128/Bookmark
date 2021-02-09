package com.samwolfson.bookmark;

import org.bukkit.Location;

import java.util.Objects;

public class NamedLocation {
    private final String name;
    private final Location location;

    public NamedLocation(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

//    @Override
//    public int compareTo(NamedLocation o) {
//        double d1, d2;
//
//
//        if (Objects.equals(this.location.getWorld(), this.playerPosition.getWorld())) {
//            d1 = this.location.distanceSquared(this.playerPosition);
//        } else {
//
//        }
//
//        double d2 = o.location.distanceSquared(o.playerPosition);
//
////        double rxt = this.location.getBlockX() - this.playerPosition.getBlockX();
////        double ryt = this.location.getBlockY() - this.playerPosition.getBlockY();
////
////        double rxo = o.location.getBlockX() - this.playerPosition.getBlockX();
////        double ryo = o.location.getBlockY() - this.playerPosition.getBlockY();
////
////        double dt = Math.pow(rxt, 2) + Math.pow(ryt, 2);
////        double do_ = Math.pow(rxo, 2) + Math.pow(ryo, 2);
//
//        return (int) (d1 - d2);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedLocation that = (NamedLocation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }
}
