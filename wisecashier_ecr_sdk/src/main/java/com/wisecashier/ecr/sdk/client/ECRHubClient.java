package com.wisecashier.ecr.sdk.client;

import static com.wisecashier.ecr.sdk.util.Constants.ECR_HUB_TOPIC_PAIR;
import static com.wisecashier.ecr.sdk.util.Constants.ECR_HUB_TOPIC_UNPAIR;
import static com.wisecashier.ecr.sdk.util.Constants.HEART_BEAT_TOPIC;
import static com.wisecashier.ecr.sdk.util.Constants.INIT_TOPIC;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wisecashier.ecr.sdk.client.payment.Payment;
import com.wisecashier.ecr.sdk.jmdns.JMdnsManager;
import com.wisecashier.ecr.sdk.listener.ECRHubConnectListener;
import com.wisecashier.ecr.sdk.listener.ECRHubResponseCallBack;
import com.wisecashier.ecr.sdk.util.ECRHubMessageData;
import com.wisecashier.ecr.sdk.util.NetUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author pupan
 */
public class ECRHubClient {

    private static final String TAG = "ECRHubClient";
    //server config
    private final ECRHubConfig config;
    // listener
    private final ECRHubConnectListener connectListener;
    // server ip
    private String ipAddress;
    private WebSocketClient webSocketClient;

    public Payment payment;
    private ECRHubResponseCallBack initCallback;
    private JMdnsManager jmdnsManager;

    ECRHubResponseCallBack pairCallBack;

    private Context context;

    public ECRHubClient(String ip, ECRHubConfig config, ECRHubConnectListener listener) {
        this.config = config;
        this.ipAddress = ip;
        this.connectListener = listener;
        initSocketClient();
    }

    public ECRHubClient(Context context, ECRHubConfig config, ECRHubConnectListener listener) {
        this.config = config;
        this.connectListener = listener;
        jmdnsManager = new JMdnsManager(context);
        this.context = context;
    }

    public void autoConnect(String ip) {
        this.ipAddress = ip;
        initSocketClient();
        connect();
    }

    public void startServerConnect(String name) {
        jmdnsManager.startServerConnect(name, this);
    }

    public void closeServerConnect() {
        if (null != jmdnsManager) {
            jmdnsManager.unRegisterService();
        }
    }

    public void requestUnPair(String deviceName, ECRHubResponseCallBack callBack) {
        pairCallBack = callBack;
        ECRHubMessageData data = new ECRHubMessageData();
        data.getDevice_data().setDevice_name(deviceName);
        data.getDevice_data().setMac_address(NetUtils.getMacAddress(context));
        data.setTopic(ECR_HUB_TOPIC_UNPAIR);
        webSocketClient.send(JSON.toJSON(data).toString());

    }

    public void init(ECRHubResponseCallBack callBack) {
        initCallback = callBack;
        if (isConnected()) {
            ECRHubMessageData data = new ECRHubMessageData();
            data.getBiz_data().setConfirm_on_terminal(true);
            data.getBiz_data().setIs_auto_settlement(true);
            data.setTopic(INIT_TOPIC);
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    private void sendHeartBeat() {
        if (isConnected()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("topic", HEART_BEAT_TOPIC);
            webSocketClient.send(jsonObject.toString());
        }
    }

    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        URI uri = URI.create(ipAddress);
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                ECRHubMessageData data = JSON.parseObject(message, ECRHubMessageData.class);
                if (data.getTopic().equals(INIT_TOPIC)) {
                    initCallback.onSuccess(JSON.toJSON(data).toString());
                } else if (data.getTopic().equals(ECR_HUB_TOPIC_PAIR) || data.getTopic().equals(ECR_HUB_TOPIC_UNPAIR)) {
                    pairCallBack.onSuccess(JSON.toJSON(data).toString());
                } else if (!data.getTopic().equals(HEART_BEAT_TOPIC)) {
                    if (null != payment && null != payment.getResponseCallBack()) {
                        payment.getResponseCallBack().onSuccess(JSON.toJSON(data).toString());
                    }
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.e(TAG, "websocket connect success");
                if (null != connectListener) {
                    connectListener.onConnect();
                }
                //   heartBeatRunnable.run();
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "websocket connect error：" + ex);
                if (null != connectListener) {
                    connectListener.onError(ex.getLocalizedMessage(), ex.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "websocket disconect：·code:" + code + "·reason:" + reason + "·remote:" + remote);
                if (null != connectListener) {
                    connectListener.onDisconnect();
                }
            }
        };
        if (config.getConnectionTimeout() != 0) {
            webSocketClient.setConnectionLostTimeout(config.getConnectionTimeout());
        }
        payment = new Payment((webSocketClient));
    }

    public boolean isConnected() {
        return null != webSocketClient && webSocketClient.isOpen();
    }


    /**
     * connect
     */
    public void connect() {
        if (webSocketClient.isOpen()) {
            return;
        }
        webSocketClient.connect();
    }

    public void disConnect() {
        if (null == webSocketClient || !webSocketClient.isOpen()) {
            return;
        }
        webSocketClient.close();
    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
        Log.e(TAG, "reconnectWs");
        initSocketClient();
    }


    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private final Handler mHandler = new Handler();
    //心跳检测
    private final Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(heartBeatRunnable);
            if (webSocketClient != null) {
                if (webSocketClient.isClosed()) {
                    Log.e(TAG, "close status");
                    reconnectWs();
                } else {
                    if (isConnected()) {
                        sendHeartBeat();
                    } else {
                        reconnectWs();
                    }
                }
            } else {
                initSocketClient();
            }
            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        }
    };
}
