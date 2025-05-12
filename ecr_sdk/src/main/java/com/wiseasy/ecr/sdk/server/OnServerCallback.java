package com.wiseasy.ecr.sdk.server;

import org.java_websocket.WebSocket;

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
    void onMessageReceived(WebSocket connection, String message);

}
