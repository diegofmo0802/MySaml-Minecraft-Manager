package com.mysaml.websocket;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class that manages WebSocket communication.
 * @param <RQ> the type of the request data
 * @param <MSG> the type of the message data
 */
public abstract class WsClient<RQ extends Object, MSG extends Object> {
    private final Map<String, WsPromise<Object>> pending;
    private final Gson gson;
    private final Class<RQ> requestClass;
    private final Class<MSG> messageClass;
    private Socket socket;
    /**
     * Constructs a WebSocket client instance with specified request and message classes.
     * @param endpoint the WebSocket server endpoint
     * @param requestClass the class of the request data
     * @param messageClass the class of the message data
     */
    public WsClient(String endpoint, Class<RQ> requestClass, Class<MSG> messageClass) {
        this.pending = new ConcurrentHashMap<>();
        this.gson = new GsonBuilder().create();
        this.requestClass = requestClass;
        this.messageClass = messageClass;
        this.socket = new Socket(endpoint) {
            @Override
            public void onConnect() { WsClient.this.onConnect(); }
            @Override
            public void onClose(int statusCode, String reason) { WsClient.this.onClose(statusCode, reason); }
            @Override
            public void onError(String reason) { WsClient.this.onError(reason); }
            @Override
            public void onMessage(ByteBuffer data) { WsClient.this.onMessage(data); }
            @Override
            public void onMessage(String message) { WsClient.this.handleMessage(message); }
        };
        this.socket.connect();
    }
    public abstract void onRequest(WsRequest<RQ> request);
    public abstract void onMessage(MSG message);
    public void onMessage(ByteBuffer data) {
        System.out.println("[WsCLient] Received binary message");
    }
    public void onConnect() {
        System.out.println("[WsCLient] Connected to server");
    }
    public void onClose(int statusCode, String reason) {
        System.out.println("[WsCLient] Disconnected from server");
    }
    public void onError(String reason) {
        System.out.println("[WsCLient] Error: " + reason);
    }
    private void handleMessage(String message) {
        try {
            WsInteraction interaction = gson.fromJson(message, WsInteraction.class);
            if (!validateInteraction(interaction)) {
                onError("Invalid interaction: " + message);
                return;
            }
            switch (interaction.type) {
                case "request": handleRequest(interaction); break;
                case "response": handleResponse(interaction); break;
                case "message": handleInteractionMessage(interaction); break;
                default: onError("Unknown interaction type"); break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError("Failed to parse interaction -> " + e);
        }
    }
    private boolean validateInteraction(WsInteraction interaction) {
        if (interaction.type == null) return false;
        if (interaction.type.equals("message") && interaction.uid == null)  return false;
        if (interaction.data == null) return false;
        return true;
    }
    private void handleRequest(WsInteraction interaction) {
        try {
            System.out.println(requestClass);
            RQ requestData = gson.fromJson(gson.toJson(interaction.data), this.requestClass);
            onRequest(new WsRequest<>(this.socket, gson, interaction.uid, requestData));
        } catch (Exception e) {
            // e.printStackTrace();
            onError("Failed to parse request -> " + e);
        }
    }
    private void handleInteractionMessage(WsInteraction interaction) {
        try {
            MSG messageData = gson.fromJson(gson.toJson(interaction.data), this.messageClass);
            onMessage(messageData);
        } catch (Exception e) {
            e.printStackTrace();
            onError("Failed to parse message -> " + e);
        }
    }
    private void handleResponse(WsInteraction interaction) {
        WsPromise<Object> promise = pending.get(interaction.uid);
        if (promise != null) {
            pending.remove(interaction.uid);
            promise.resolve(interaction.data);
        }
    }
    /**
     * Sends a request to the WebSocket server.
     * @param data the data to send
     * @return a promise that will be resolved when the server responds
     */
    public WsPromise<Object> request(Map<String, Object> data) {
        String uid = generateUID();
        WsPromise<Object> promise = new WsPromise<>();
        pending.put(uid, promise);
        WsInteraction interaction = new WsInteraction("request", uid, data);
        socket.send(gson.toJson(interaction));
        return promise;
    }

    /**
     * Sends a message to the WebSocket server.
     * @param data the data to send
     */
    public void send(Map<String, Object> data) {
        WsInteraction interaction = new WsInteraction("message", generateUID(), data);
        socket.send(gson.toJson(interaction));
    }
    /**
     * Closes the WebSocket connection.
     */
    public void disconnect() { socket.disconnect(); }
    /**
     * Generates a unique identifier for a WebSocket interaction.
     * @return the generated unique identifier
     */
    private String generateUID() { return java.util.UUID.randomUUID().toString(); }
}