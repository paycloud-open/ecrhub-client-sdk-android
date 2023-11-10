package com.wisecashier.ecr.sdk.jmdns;

import android.content.Context;

import com.wisecashier.ecr.sdk.util.NetUtils;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class JMdnsManager implements ServiceListener, OnServerCallback {
    private final static String CLIENT_REMOTE_TYPE = "_ecr-hub-client._tcp.local.";
    private static int PORT = 35779;
    public final static String SERVER_REMOTE_TYPE = "_ecr-hub-server._tcp.local.";

    private Context context;

    ECRHubWebSocketServer socketServer;
    private JmDNS mJmdns;

    SearchServerListener mSearchServerListener;

    public JMdnsManager(Context context) {
        this.context = context;
        new Thread(() -> {
            InetAddress ip = NetUtils.getLocalIPAddress();
            try {
                mJmdns = JmDNS.create(ip, "ECRHubServerName");
                if (null == socketServer) {
                    InetSocketAddress address = new InetSocketAddress(PORT);
                    socketServer = new ECRHubWebSocketServer(address, this);
                }
                socketServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void startManualConnection(SearchServerListener listener) {
        mSearchServerListener = listener;
        if (null != mJmdns) {
            mJmdns.addServiceListener(SERVER_REMOTE_TYPE, this);
        }
    }

    @Override
    public void serviceAdded(ServiceEvent event) {

    }

    @Override
    public void serviceRemoved(ServiceEvent event) {

    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        ServiceInfo info = event.getInfo();
        if (null != info) {
            String ipv4 = "";
            if (null != info.getInet4Addresses()) {
                ipv4 = info.getInet4Addresses()[0].getHostAddress();
            }
            mSearchServerListener.onServerFind(ipv4, "" + info.getPort(), info.getName());
        }
    }

    @Override
    public void onServerStart() {
        final HashMap<String, String> values = new HashMap<>();
        values.put("mac_address", NetUtils.getMacAddress(context));
        ServiceInfo mServiceInfo = ServiceInfo.create(CLIENT_REMOTE_TYPE, NetUtils.getMacAddress(context), PORT, 0, 0, values);
        try {
            mJmdns.registerService(mServiceInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(String errorMsg) {
        PORT += 1;
        InetSocketAddress address = new InetSocketAddress(PORT);
        socketServer = new ECRHubWebSocketServer(address, this);
        socketServer.start();
    }

    @Override
    public void onMessageReceived(WebSocket connection, String message) {

    }
}
