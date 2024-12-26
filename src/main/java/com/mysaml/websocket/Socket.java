package com.mysaml.websocket;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

public abstract class Socket implements WebSocket.Listener {
    public static final int MAX_BUFFER_SIZE = 1048576;
    protected WebSocket socket;
    private String endpoint;
    private HttpClient client;
    private ArrayList<ByteBuffer> binaryReceived;
    private StringBuilder textReceived;
    public Socket(String endpoint) {
        this.endpoint = endpoint;
        this.client = HttpClient.newHttpClient();
        this.socket = null;
    }
    public abstract void onConnect();
    public abstract void onClose(int statusCode, String reason);
    public abstract void onError(String reason);
    public abstract void onMessage(ByteBuffer data);
    public abstract void onMessage(String message);
    public boolean isConnected() { return this.socket != null; }
    public void connect() {
        try {
            this.socket = client.newWebSocketBuilder()
            .buildAsync(URI.create(endpoint), this)
            .join();
        } catch (Exception e) {
            this.onError("Error al conectar: " + e.getMessage());
        }
    }
    public void disconnect() throws RuntimeException {
        if (!isConnected()) throw new RuntimeException("the socket is not connected");
        this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "disconnect");
    }
    public void send(ByteBuffer binary) {
        if (!isConnected()) throw new RuntimeException("the socket is not connected");
        this.socket.sendBinary(binary, true);
    }
    public void send(ByteBuffer binary, boolean last) {
        if (!isConnected()) throw new RuntimeException("the socket is not connected");
        this.socket.sendBinary(binary, last);
    }
    public void send(String message) {
        if (!isConnected()) throw new RuntimeException("the socket is not connected");
        this.socket.sendText(message, true);
    }
    public void send(String message, boolean last) {
        if (!isConnected()) throw new RuntimeException("the socket is not connected");
        this.socket.sendText(message, last);
    }

    @Override
    public final void onOpen(WebSocket webSocket) {
        this.socket = webSocket;
        this.binaryReceived = new ArrayList<>();
        this.textReceived = new StringBuilder();
        this.onConnect();
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public final void onError(WebSocket webSocket, Throwable error) {
        String errorMessage = "Error en WebSocket: " + error.getClass().getName() + " - " + error.getMessage();
        this.onError(errorMessage);
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        this.onClose(statusCode, reason);
        this.socket = null;
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        textReceived.append(data);
        if (last) {
            this.onMessage(textReceived.toString());
            textReceived.setLength(0); // Limpiar
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        binaryReceived.add(data);
        if (last) {
            int totalSize = binaryReceived.stream().mapToInt(ByteBuffer::remaining).sum();
            if (totalSize > MAX_BUFFER_SIZE) {
                this.onError("Tamaño máximo de buffer excedido");
                binaryReceived.clear();
                return WebSocket.Listener.super.onBinary(webSocket, data, last);
            }

            ByteBuffer mergedBuffer = ByteBuffer.allocate(totalSize);
            binaryReceived.forEach(buffer -> mergedBuffer.put(buffer.duplicate()));
            mergedBuffer.flip();
            this.onMessage(mergedBuffer);
            binaryReceived.clear();
        }
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }
}
