package com.mysaml.mc.manager.remote;

import com.google.gson.annotations.SerializedName;

public class Request {
    @SerializedName("command")
    public String command;
    @SerializedName("data")
    public Object data;
    public Request(String command, Object data) {
        this.command = command;
        this.data = data;
    }
    @Override
    public String toString() {
        return "[" + command + "] -> " + data;
    }
}
