package com.wiseasy.ecr.sdk.listener;
import com.wiseasy.ecr.sdk.util.ECRHubMessageData;

/**
 * Device Pairing Listening
 */
public interface ECRHubPairListener {
    /**
     * The ip address of the device
     *
     * @param ip
     * @return
     */
    void onDevicePair(ECRHubMessageData data, String ip);


    /**
     * unpair
     */
    void onDeviceUnpair(ECRHubMessageData data);
}
