package com.wiseasy.ecr.sdk;

import static com.wiseasy.ecr.sdk.util.Constants.GETINFO_TOPIC;
import static com.wiseasy.ecr.sdk.util.Constants.PAYMENT_TOPIC;
import static com.wiseasy.ecr.sdk.util.Constants.QUERY_TOPIC;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wiseasy.ecr.sdk.listener.EcrConnectListener;
import com.wiseasy.ecr.sdk.listener.EcrResponseCallBack;
import com.wiseasy.ecr.sdk.util.Constants;
import com.wiseasy.ecr.sdk.util.NetUtils;
import com.wiseecr.host.sdk.InitEcrHostSdkListener;
import com.wiseecr.host.sdk.WiseEcrHostSdk;
import com.wiseecr.host.sdk.cdc.EcrCdcHost;
import com.wiseecr.host.sdk.cdc.EcrCdcListener;
import com.wiseecr.host.sdk.common.ConnectionStatus;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class EcrClient {

    private static final String TAG = "EcrClient";
    // context
    private Context context;
    // listener
    private EcrConnectListener connectListener;
    // server ip
    private String ipAddress;

    private WebSocketClient webSocketClient;
    private EcrCdcHost ecrCdcHost;

    private Constants.ECRHubType type = Constants.ECRHubType.WLAN;

    Map<String, EcrResponseCallBack> callBackHashMap = new HashMap<>();


    private EcrClient() {
    }

    private static class SingletonHolder {
        private static final EcrClient INSTANCE = new EcrClient();
    }

    public static EcrClient getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void init(Context context, EcrConnectListener listener) {
        this.context = context;
        this.connectListener = listener;
    }

    public void connectWifi(String address) {
        type = Constants.ECRHubType.WLAN;
        if (isConnected()) {
            disConnect();
        } else {
            webSocketClient = null;
        }
        ipAddress = address;
        connectWlanServer();
    }

    public void connectUsb() {
        type = Constants.ECRHubType.USB;
        openUsbServer();
    }

    public void disConnect() {
        if (type == Constants.ECRHubType.WLAN) {
            if (null == webSocketClient || !webSocketClient.isOpen()) {
                return;
            }
            webSocketClient.close();
            webSocketClient = null;
        } else {
            ecrCdcHost.close();
            if (null != connectListener) {
                connectListener.onDisconnect();
            }
        }
    }

    public boolean isConnected() {
        if (type == Constants.ECRHubType.WLAN) {
            return null != webSocketClient && webSocketClient.isOpen();
        } else {
            if (null == ecrCdcHost) {
                return false;
            } else {
                int status = ecrCdcHost.getConnectionStatus();
                return status == ConnectionStatus.STATUS_PORT_CONNECTED;
            }
        }
    }


    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        URI uri;
        if (null != context) {
            uri = URI.create(ipAddress + "/" + "mac_address=" + NetUtils.getMacAddress(context));
        } else {
            uri = URI.create(ipAddress);
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e(TAG, message);
                JSONObject data = JSON.parseObject(message);
                String topic = data.getString("topic");

                EcrResponseCallBack callBack = callBackHashMap.get(topic);
                if (callBack != null) {
                    callBack.onSuccess(message);
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.e(TAG, "websocket connect success");
                if (null != connectListener) {
                    connectListener.onConnect();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "websocket connect error：" + ex);
                disConnect();
                if (null != connectListener) {
                    connectListener.onError(ex.getLocalizedMessage(), ex.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "websocket disconect：·code:" + code + "·reason:" + reason + "·remote:" + remote);
                if ((code != 1000 && code != -1) || remote) {
                    if (null != connectListener) {
                        connectListener.onDisconnect();
                    }
                }
            }
        };
        webSocketClient.setConnectionLostTimeout(5);
    }

    private void initUsbConnect() {
        WiseEcrHostSdk.getInstance().initEcrHostSdk(context, new InitEcrHostSdkListener() {
            @Override
            public void onInitEcrHostSdkSuccess() {
                ecrCdcHost = WiseEcrHostSdk.getInstance().getEcrCdcHost();
                ecrCdcHost.initForRawData(new EcrCdcListener() {
                    @Override
                    public void onStatusChanged(int i) {
                        if (i == ConnectionStatus.STATUS_CABLE_UNPLUGGED) {
                            if (null != ecrCdcHost) {
                                ecrCdcHost.close();
                            }
                        } else if (i == ConnectionStatus.STATUS_CABLE_PLUGGED) {
                            if (null != ecrCdcHost) {
                                ecrCdcHost.open();
                            }
                        }
                    }

                    @Override
                    public void onRawDataReceived(byte[] bytes) {
                        String message = new String(bytes);
                        Log.e(TAG, "收到消息" + message);
                        JSONObject data = JSON.parseObject(message);
                        String topic = data.getString("topic");

                        EcrResponseCallBack callBack = callBackHashMap.get(topic);
                        if (callBack != null) {
                            callBack.onSuccess(message);
                        }
                    }

                    @Override
                    public void onPacketDataReceived(String s) {

                    }

                    @Override
                    public void onError(int i) {

                    }
                });
                int ret = ecrCdcHost.open();
                if (ret == 0) {
                    if (null != connectListener) {
                        connectListener.onConnect();
                    }
                } else {
                    if (null != connectListener) {
                        Log.e(TAG, "返回错误码" + ret + "");
                        connectListener.onError(ret + "", "");
                    }
                }
            }

            @Override
            public void onInitEcrHostSdkFail(int errCode) {
                if (null != connectListener) {
                    connectListener.onError(errCode + "", "");
                }
            }
        });
    }

    /**
     * connect
     */
    private void connectWlanServer() {
        if (null == webSocketClient) {
            initSocketClient();
        }
        if (webSocketClient.isOpen()) {
            if (null != connectListener) {
                connectListener.onConnect();
            }
            return;
        }
        if (webSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
            webSocketClient.connect();
        } else if (webSocketClient.getReadyState().equals(ReadyState.CLOSING) || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
            webSocketClient.reconnect();
        }
    }

    private void openUsbServer() {
        if (null == ecrCdcHost) {
            initUsbConnect();
        } else {
            int type = ecrCdcHost.getConnectionStatus();
            if (type == ConnectionStatus.STATUS_PORT_CONNECTED) {
                if (null != connectListener) {
                    connectListener.onConnect();
                }
            } else if (type == ConnectionStatus.STATUS_PORT_DISCONNECTED || type == ConnectionStatus.STATUS_CABLE_PLUGGED) {
                int ret = ecrCdcHost.open();
                if (ret == 0) {
                    if (null != connectListener) {
                        connectListener.onConnect();
                    }
                } else {
                    if (null != connectListener) {
                        connectListener.onError(ret + "", "");
                    }
                }
            } else {
                if (null != connectListener) {
                    connectListener.onError("-1", "");
                }
            }
        }
    }

    public void getTerminalInfo(EcrResponseCallBack callBack){
        addCallBack(GETINFO_TOPIC, callBack);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("topic", GETINFO_TOPIC);
        JSONObject device = new JSONObject();
        device.put("device_name", Build.MODEL);
        jsonObject.put("device_data", device);

        sendData(jsonObject.toString());
    }

    public void doTransaction(String params, EcrResponseCallBack callBack){
        addCallBack(PAYMENT_TOPIC, callBack);
        sendData(params);
    }

    public void cancelTransaction(String params) {
        sendData(params);
    }

    public void queryTransaction(String params, EcrResponseCallBack callBack) {
        addCallBack(QUERY_TOPIC, callBack);
        sendData(params);
    }

    protected void sendData(String params) {
        if (type == Constants.ECRHubType.WLAN) {
            if (null != webSocketClient && webSocketClient.isOpen()) {
                webSocketClient.send(params);
            }
        } else {
            if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
                byte[] request = params.getBytes(StandardCharsets.UTF_8);
                int ret = ecrCdcHost.sendRawData(request, request.length);
                Log.i(TAG, "CDC send result: " + ret);
            }
        }
    }


    private void addCallBack(String topic, EcrResponseCallBack callBack) {
        if (callBackHashMap.containsKey(topic)) {
            callBackHashMap.remove(topic);
        }
        callBackHashMap.put(topic, callBack);
    }


}
