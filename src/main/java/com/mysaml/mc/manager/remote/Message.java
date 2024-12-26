package com.mysaml.mc.manager.remote;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("type")
    public String type;
    @SerializedName("data")
    public Object data;
    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }
    @Override
    public String toString() {
        return "[" + type + "] -> " + data;
    }
}
