package com.mysaml.mc.manager.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
}
