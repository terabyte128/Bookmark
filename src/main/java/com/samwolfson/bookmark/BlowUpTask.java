package com.samwolfson.bookmark;

import java.util.Random;

import org.bukkit.entity.Player;

public class BlowUpTask implements Runnable {

    Player target;

    public BlowUpTask(Player target) {
        this.target = target;
    }

    @Override
    public void run() {
        target.getWorld().createExplosion(target, target.getLocation(), 4f, false, false);
        double health = target.getHealth();
        int damageAmount = new Random().nextInt((int) (health+2));
        target.damage(damageAmount);
    }
}
