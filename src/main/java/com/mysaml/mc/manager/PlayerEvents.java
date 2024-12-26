package com.mysaml.mc.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mysaml.mc.api.EventListener;
import com.mysaml.mc.manager.remote.Remote;

public class PlayerEvents implements EventListener {
    private final Remote remote;
    private final Map<Player, Double> currentHealth;
    private final Map<Player, Integer> currentFood;

    public PlayerEvents(Remote remote) {
        this.remote = remote;
        this.currentHealth = new HashMap<>();
        this.currentFood = new HashMap<>();
    }
    private void healthChange(Player player) {
        double health = player.getHealth();
        if (health != currentHealth.getOrDefault(player, health)) {
            currentHealth.put(player, health);
            remote.sendPlayerEvent("player-health-change", player);
        }
    }
    private void foodChange(Player player) {
        int foodLevel = player.getFoodLevel();
        if (foodLevel != currentFood.getOrDefault(player, foodLevel)) {
            currentFood.put(player, foodLevel);
            remote.sendPlayerEvent("player-food-change", player);
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        currentHealth.put(player, player.getHealth());
        currentFood.put(player, player.getFoodLevel());
        remote.sendPlayerEvent("player-join", player);
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        remote.sendPlayerEvent("player-leave", player);
    }
    @EventHandler
    public void onPlayerHealthChange(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        healthChange(player);
    }
    @EventHandler
    public void onPlayerHealthChange(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        healthChange(player);
    }
    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        foodChange(player);
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        foodChange(player);
        healthChange(player);
    }
}
