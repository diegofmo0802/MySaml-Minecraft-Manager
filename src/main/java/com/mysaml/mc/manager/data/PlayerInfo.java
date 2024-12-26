package com.mysaml.mc.manager.data;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PlayerInfo {
    public String name;
    public String id;
    public double health;
    public double maxHealth;
    public int food;
    public int maxFood;
    public PlayerInfo(Player player) {
        this.name = player.getName();
        this.id = player.getUniqueId().toString();
        this.health = player.getHealth();
        this.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        this.food = player.getFoodLevel();
    }
}
