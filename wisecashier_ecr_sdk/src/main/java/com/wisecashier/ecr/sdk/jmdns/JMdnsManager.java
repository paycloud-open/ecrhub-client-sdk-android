package com.wisecashier.ecr.sdk.jmdns;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.wisecashier.ecr.sdk.client.ECRHubClient;
import com.wisecashier.ecr.sdk.util.Constants;
import com.wisecashier.ecr.sdk.util.ECRHubMessageData;
import com.wisecashier.ecr.sdk.util.NetUtils;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class JMdnsManager implements OnServerCallback {
    private final static String CLIENT_REMOTE_TYPE = "_ecr-hub-client._tcp.local.";
    private static int PORT = 35779;

    private Context context;

    private String deviceName = "";

    ECRHubWebSocketServer socketServer;
    private JmDNS mJmdns;

    private boolean isServerStart = false;

    private ECRHubClient client;

    public JMdnsManager(Context context) {
        this.context = context;
        new Thread(() -> {
            InetAddress ip = NetUtils.getLocalIPAddress();
            try {
                mJmdns = JmDNS.create(ip, "ECRHubServerName");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void startServerConnect(String name, ECRHubClient client) {
        deviceName = name;
        this.client = client;
        if (!isServerStart) {
            if (null == socketServer) {
                InetSocketAddress address = new InetSocketAddress(PORT);
                socketServer = new ECRHubWebSocketServer(address, this);
            }
            socketServer.start();
        }
    }

    @Override
    public void onServerStart() {
        isServerStart = true;
        final HashMap<String, String> values = new HashMap<>();
        values.put("mac_address", NetUtils.getMacAddress(context));
        ServiceInfo mServiceInfo = ServiceInfo.create(CLIENT_REMOTE_TYPE, deviceName, PORT, 0, 0, values);
        try {
            mJmdns.registerService(mServiceInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(String errorMsg) {
        isServerStart = false;
        PORT += 1;
        InetSocketAddress address = new InetSocketAddress(PORT);
        socketServer = new ECRHubWebSocketServer(address, this);
        socketServer.start();
    }

    @Override
    public void onClose() {
        isServerStart = false;
        socketServer = null;
    }

    @Override
    public void onMessageReceived(WebSocket connection, String message) {
        ECRHubMessageData data = JSON.parseObject(message, ECRHubMessageData.class);
        client.autoConnect("ws://" + data.getDevice_data().getIp_address() + ":" + data.getDevice_data().getPort());
        if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_PAIR)) {
            data.setResponse_code("000");
            connection.send(JSON.toJSON(data).toString());
        }
    }
}
