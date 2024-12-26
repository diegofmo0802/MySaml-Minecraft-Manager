package com.mysaml.mc.manager.remote;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("type")
    public String type;
    @SerializedName("data")
    public Map<String, Object> data;
    public Message(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }
    @Override
    public String toString() {
        return "[" + type + "] -> " + data;
    }
}
