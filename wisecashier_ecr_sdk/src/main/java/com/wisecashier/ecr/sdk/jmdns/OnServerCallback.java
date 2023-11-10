package com.wisecashier.ecr.sdk.jmdns;

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
     * onMessageReceived
     *
     * @param connection
     * @param message
     */
    void onMessageReceived(WebSocket connection, String message);

}
