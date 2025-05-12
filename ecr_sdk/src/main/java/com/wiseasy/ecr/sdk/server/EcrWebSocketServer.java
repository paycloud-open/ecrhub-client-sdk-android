package com.wiseasy.ecr.sdk.server;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class EcrWebSocketServer extends WebSocketServer {

    private static final String TAG = "EcrWebSocketServer";
    private final OnServerCallback onServerCallback;

    public EcrWebSocketServer(InetSocketAddress address, OnServerCallback callback) {
        super(address);
        onServerCallback = callback;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.i(TAG, conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        Log.i(TAG, "New connection opened");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.i(TAG, "Connection closed");
        onServerCallback.onClose();
        conn.close();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.i(TAG, "Received message: " + message);
        onServerCallback.onMessageReceived(conn, message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        super.onMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (null != conn) {
            conn.close();
        }
        Log.i(TAG, "Error occurred: " + ex.getMessage());
        onServerCallback.onError(ex.getMessage());
    }

    @Override
    public void onStart() {
        onServerCallback.onServerStart();
        Log.i(TAG, "WebSocket server started");
    }
}
