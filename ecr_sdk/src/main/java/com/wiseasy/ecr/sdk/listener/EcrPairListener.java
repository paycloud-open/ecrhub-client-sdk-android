package com.wiseasy.ecr.sdk.listener;

import com.wiseasy.ecr.sdk.bean.EcrMessageData;

/**
 * Device Pairing Listening
 */
public interface EcrPairListener {
    /**
     * The ip address of the device
     *
     * @param ip
     * @return
     */
    void onDevicePair(EcrMessageData data, String ip);


    /**
     * unpair
     */
    void onDeviceUnpair(EcrMessageData data);
}
