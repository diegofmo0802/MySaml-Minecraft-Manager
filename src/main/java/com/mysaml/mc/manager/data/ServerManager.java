package com.mysaml.mc.manager.data;

import org.bukkit.Bukkit;

public class ServerManager {
    public static ServerInfo getServerInfo() {
        ServerInfo info = new ServerInfo();
        info.name = Bukkit.getServer().getName();
        info.motd = Bukkit.getServer().getMotd();
        info.version = Bukkit.getServer().getVersion();
        info.maxPlayers = Bukkit.getServer().getMaxPlayers();
        return info;
    }
}
