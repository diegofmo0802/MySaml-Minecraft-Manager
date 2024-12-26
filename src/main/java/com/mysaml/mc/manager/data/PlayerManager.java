package com.mysaml.mc.manager.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerManager {
    public static List<PlayerInfo> getPlayerList() {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        List<PlayerInfo> list = new ArrayList<>();
        for (Player player : players) {
            PlayerInfo info = new PlayerInfo(player);
            list.add(info);
        }
        return list;
    }
    public static void sendMessage(String id, String message) {
        UUID uuid = UUID.fromString(id);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) player.sendMessage(message);
    }
    public static void broadCastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }
}
