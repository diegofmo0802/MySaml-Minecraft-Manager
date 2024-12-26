package com.mysaml.websocket;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * Class representing a WebSocket interaction.
 */
public class WsInteraction {
    @SerializedName("type")
    public String type;
    @SerializedName("uid")
    public String uid;
    @SerializedName("data")
    public Map<String, Object> data;
    public WsInteraction(String type, String uid, Map<String, Object> data) {
        this.type = type;
        this.uid = uid;
        this.data = data;
    }
}