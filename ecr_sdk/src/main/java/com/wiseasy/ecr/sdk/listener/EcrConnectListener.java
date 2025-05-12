package com.wiseasy.ecr.sdk.listener;

/**
 * connect status listener
 */
public interface EcrConnectListener {

    /**
     * connect successfully
     */
    void onConnect();

    /**
     * connect failed
     */
    void onDisconnect();

    /**
     * connect error
     *
     * @param errorCode error code
     * @param errorMsg  error message
     */
    void onError(String errorCode, String errorMsg);
}
