package com.mysaml.mc.manager.remote;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.mysaml.mc.manager.data.PlayerInfo;
import com.mysaml.mc.manager.data.PlayerManager;
import com.mysaml.mc.manager.data.ServerManager;
import com.mysaml.websocket.WsClient;
import com.mysaml.websocket.WsRequest;

public class Remote extends WsClient<Request, Message> {
    public Remote() {
        super("wss://test.mysaml.com/server", Request.class, Message.class);
    }
    @Override
    public void onRequest(WsRequest<Request> request) {
        switch (request.data.command) {
            case "server-info": serverInfoHandler(request); break;
            case "player-list": playerListHandler(request); break;
            default: request.replyError("Unknown command"); break;
        }
    }
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
    private void serverInfoHandler(WsRequest<Request> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("info", ServerManager.getServerInfo());
        request.reply(response);
    }
    private void playerListHandler(WsRequest<Request> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("players", PlayerManager.getPlayerList());
        request.reply(response);
    }
    public void sendEvent(Object data) {
        Map<String, Object> message = Map.of(
            "type", "event",
            "data", data
        );
        this.send(message);
    }
    public void sendPlayerEvent(String event, Player player) {
        Map<String, Object> message = Map.of(
            "event", event,
            "player", new PlayerInfo(player)
        );
        this.sendEvent(message);
    }
}
