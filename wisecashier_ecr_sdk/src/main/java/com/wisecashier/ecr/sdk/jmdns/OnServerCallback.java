package com.wisecashier.ecr.sdk.jmdns;

import org.java_websocket.WebSocket;

import java.nio.ByteBuffer;

public interface OnServerCallback {
    /**
     * Server start
     */
    void onServerStart();

    /**
     * error
     *
     * @param errorMsg error message
     */
    void onError(String errorMsg);

    /**
     * server disconnect
     */
    void onClose();

    /**
     * onMessageReceived
     *
     * @param connection
     * @param message
     */
    void onMessageReceived(WebSocket connection, ByteBuffer message);

}
