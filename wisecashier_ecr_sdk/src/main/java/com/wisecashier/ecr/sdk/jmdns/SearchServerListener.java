package com.wisecashier.ecr.sdk.jmdns;

/**
 * @author pupan
 * listener search server
 */
public interface SearchServerListener {
    public void onServerFind(String ip, String port, String deviceName);
}
