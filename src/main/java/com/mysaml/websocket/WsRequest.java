package com.mysaml.websocket;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Class representing a WebSocket request.
 * @param <T> the type of the request data
 */
public class WsRequest<T> {
    private final Socket socket;
    private final Gson gson;
    private final String uid;
    public final T data;
    private boolean isReplied;
    /**
     * Constructs a WebSocket request instance.
     * @param socket the WebSocket connection
     * @param uid the unique identifier for the request
     * @param data the data associated with the request
     */
    public WsRequest(Socket socket, Gson gson, String uid, T data) {
        this.socket = socket;
        this.gson = gson;
        this.uid = uid;
        this.data = data;
        this.isReplied = false;
    }
    /**
     * Sends a response to the request.
     * @param data the response data to send
     * @throws IllegalStateException if the request has already been replied to
     */
    private<U> void send(U data) {
        if (this.isReplied) {
            throw new IllegalStateException("Request already replied");
        }
        this.isReplied = true;
        Map<String, Object> response = new HashMap<>();
        response.put("type", "response");
        response.put("uid", this.uid);
        response.put("data", data);
        socket.send(gson.toJson(response));
    }
    /**
     * Sends a response to the request.
     * @param data the response data to send
    */
    public<U> void reply(U data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        send(response);
    }
    /**
     * Sends an error response to the request.
     * @param error the error message to send
     */
    public void replyError(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        send(response);
    }
    @Override
    public String toString() {
        return "[WsRequest]: " + uid + " -> " + data;
    }
}
