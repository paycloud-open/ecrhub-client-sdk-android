package com.wiseasy.ecr.sdk;

import static com.wiseasy.ecr.sdk.util.Constants.ECR_HUB_TOPIC_UNPAIR;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wiseasy.ecr.sdk.bean.EcrDevice;
import com.wiseasy.ecr.sdk.bean.EcrMessageData;
import com.wiseasy.ecr.sdk.listener.EcrPairListener;
import com.wiseasy.ecr.sdk.server.EcrWebSocketServer;
import com.wiseasy.ecr.sdk.server.OnServerCallback;
import com.wiseasy.ecr.sdk.util.Constants;
import com.wiseasy.ecr.sdk.util.NetUtils;
import com.wiseasy.ecr.sdk.util.SharePreferenceUtil;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class EcrWifiDiscoveryService implements OnServerCallback, ServiceListener {

    private static final String TAG = "EcrWifiDiscoveryService";
    private final static String REMOTE_CLIENT_TYPE = "_ecr-hub-client._tcp.local.";

    public final static String REMOTE_SERVER_TYPE = "_ecr-hub-server._tcp.local.";
    private static int PORT = 35779;
    private final Context context;
    private String deviceName = "";
    EcrWebSocketServer socketServer;
    private JmDNS mJmdns;
    private boolean isServerStart = false;
    private EcrPairListener pairListener;

    private WebSocket connection;

    public EcrWifiDiscoveryService(Context context) {
        this.context = context;
        SharePreferenceUtil.init(context);
        try {
            String host = NetUtils.getWifiIPAddress(context);
            InetAddress ip = InetAddress.getByName(host);
            mJmdns = JmDNS.create(ip, "ECRHubServerName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String name, EcrPairListener listener) {
        deviceName = name;
        pairListener = listener;
        if (!isServerStart) {
            if (null == socketServer) {
                InetSocketAddress address = new InetSocketAddress(PORT);
                socketServer = new EcrWebSocketServer(address, this);
            }
            socketServer.start();
        }
    }

    public void start(EcrPairListener listener) {
        start(Build.MODEL, listener);
    }

    public void stop() {
        try {
            isServerStart = false;
            if (null != mJmdns) {
                mJmdns.close();
                mJmdns.unregisterAllServices();
                mJmdns.removeServiceListener(REMOTE_SERVER_TYPE, this);
                mJmdns = null;
            }
            socketServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unPair(EcrDevice device) {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                EcrDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), EcrDevice.class);
                if (deviceData.getName().equals(device.getName())) {
                    array.remove(i);
                    break;
                }
            }
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
        if (EcrClient.getInstance().isConnected()) {
            if (deviceName.isEmpty()) {
                deviceName = Build.MODEL;
            }
            device.setName(deviceName);
            device.setWs_address(NetUtils.getMacAddress(context));
            device.setPort("" + PORT);
            device.setIp_address(NetUtils.getWifiIPAddress(context));

            EcrMessageData data = new EcrMessageData();
            data.getDevice_data().setDevice_name(device.getName());
            data.getDevice_data().setMac_address(device.getWs_address());
            data.getDevice_data().setPort(device.getPort());
            data.getDevice_data().setIp_address(device.getIp_address());
            data.getDevice_data().setAlias_name(device.getName());
            data.setTopic(ECR_HUB_TOPIC_UNPAIR);

            EcrClient.getInstance().sendData(JSON.toJSONString(data));
            EcrClient.getInstance().disConnect();
        }
    }

    private void deletePairList(EcrMessageData data) {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        EcrDevice device = new EcrDevice();
        device.setPort(data.getDevice_data().getPort());
        device.setIp_address(data.getDevice_data().getIp_address());
        if (null != data.getDevice_data().getAlias_name() && !"".equals(data.getDevice_data().getAlias_name())) {
            device.setTerminal_sn(data.getDevice_data().getAlias_name());
        } else {
            device.setTerminal_sn(data.getDevice_data().getDevice_name());
        }
        device.setWs_address(data.getDevice_data().getMac_address());
        device.setName(data.getDevice_data().getDevice_name());
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                EcrDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), EcrDevice.class);
                if (deviceData.getName().equals(device.getName())) {
                    array.remove(i);
                    break;
                }
            }
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
    }

    private void registerService() throws IOException {
        if (null != mJmdns) {
            mJmdns.unregisterAllServices();
            final JSONObject clientInfo = new JSONObject();
            clientInfo.put("mac_address", NetUtils.getMacAddress(context));
            clientInfo.put("ip_address", NetUtils.getWifiIPAddress(context) + ":" + PORT);
            clientInfo.put("name", deviceName);
            Log.i(TAG, "registerService: " + clientInfo.toJSONString());
            ServiceInfo mServiceInfo = ServiceInfo.create(REMOTE_CLIENT_TYPE, deviceName + "_" + System.currentTimeMillis(), PORT, 0, 0, clientInfo.toJSONString());
            mJmdns.registerService(mServiceInfo);
        }
    }


    @Override
    public void onServerStart() {
        isServerStart = true;
        try {
            if (null != mJmdns) {
                mJmdns.unregisterAllServices();
                mJmdns = null;
            }
            String host = NetUtils.getWifiIPAddress(context);
            InetAddress ip = InetAddress.getByName(host);
            mJmdns = JmDNS.create(ip, "ECRHubServerName");
            final JSONObject clientInfo = new JSONObject();
            clientInfo.put("mac_address", NetUtils.getMacAddress(context));
            clientInfo.put("ip_address", host + ":" + PORT);
            clientInfo.put("name", deviceName);
            Log.i(TAG, clientInfo.toJSONString());
            ServiceInfo mServiceInfo = ServiceInfo.create(REMOTE_CLIENT_TYPE, deviceName + "_" + System.currentTimeMillis(), PORT, 0, 0, clientInfo.toJSONString());
            mJmdns.registerService(mServiceInfo);
            mJmdns.addServiceListener(REMOTE_SERVER_TYPE, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(String errorMsg) {
        isServerStart = false;
        PORT += 1;
        InetSocketAddress address = new InetSocketAddress(PORT);
        socketServer = new EcrWebSocketServer(address, this);
        socketServer.start();
    }

    @Override
    public void onClose() {
        isServerStart = false;
        socketServer = null;
    }

    /**
     * get paired list
     *
     * @return paired list
     */
    public List<EcrDevice> getPairedDeviceList() {
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        List<EcrDevice> pairedDeviceList = new ArrayList<>();
        if (!deviceList.isEmpty()) {
            JSONArray array = JSON.parseArray(deviceList);
            for (int i = 0; i < array.size(); i++) {
                EcrDevice data = JSON.parseObject(array.getJSONObject(i).toString(), EcrDevice.class);
                pairedDeviceList.add(data);
            }
        }
        return pairedDeviceList;
    }

    private void addPairedDevice(EcrMessageData data) {
        JSONArray array = new JSONArray();
        EcrDevice device = new EcrDevice();
        device.setPort(data.getDevice_data().getPort());
        device.setIp_address(data.getDevice_data().getIp_address());
        if (null != data.getDevice_data().getAlias_name() && !"".equals(data.getDevice_data().getAlias_name())) {
            device.setTerminal_sn(data.getDevice_data().getAlias_name());
        } else {
            device.setTerminal_sn(data.getDevice_data().getDevice_name());
        }
        device.setWs_address(data.getDevice_data().getMac_address());
        device.setName(data.getDevice_data().getDevice_name());
        if (null == device.getName() || "".equals(device.getName())) {
            return;
        }
        String deviceList = SharePreferenceUtil.getString(Constants.ECR_HUB_PAIR_LIST_KEY, "");
        if (!deviceList.isEmpty()) {
            array = JSON.parseArray(deviceList);
            boolean isHas = false;
            for (int i = 0; i < array.size(); i++) {
                EcrDevice deviceData = JSON.parseObject(array.getJSONObject(i).toString(), EcrDevice.class);
                if (deviceData.getWs_address().equals(data.getDevice_data().getMac_address()) || deviceData.getIp_address().equals(data.getDevice_data().getIp_address()) || deviceData.getName().equals(data.getDevice_data().getDevice_name())) {
                    isHas = true;
                    break;
                }
            }
            if (!isHas) {
                array.add(JSON.toJSON(device));
            }
        } else {
            array.add(JSON.toJSON(device));
        }
        if (!array.isEmpty()) {
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
    }

    public boolean confirmPair(EcrMessageData data) {
        if (null != connection && connection.isOpen()) {
            if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_PAIR)) {
                data.setResponse_code("000");
                connection.send(JSON.toJSON(data).toString());
                addPairedDevice(data);
                Log.i(TAG, "getPairedDeviceList: " + getPairedDeviceList());
            }
            return true;
        }
        return false;
    }

    public void cancelPair(EcrMessageData data) {
        if (null != connection && connection.isOpen()) {
            if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_PAIR)) {
                data.setResponse_code("001");
                connection.send(JSON.toJSON(data).toString());
            }
        }
    }

    @Override
    public void onMessageReceived(WebSocket connection, String message) {
        Log.i(TAG, "Received message: " + message);
        this.connection = connection;
        EcrMessageData data = JSON.parseObject(message, EcrMessageData.class);
        if (data.getTopic().equals(Constants.ECR_HUB_TOPIC_UNPAIR)) {
            data.setResponse_code("000");
            connection.send(JSON.toJSON(data).toString());
            deletePairList(data);
            pairListener.onDeviceUnpair(data);
        } else {
            pairListener.onDevicePair(data, "ws://" + data.getDevice_data().getIp_address() + ":" + data.getDevice_data().getPort());
        }
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        Log.i(TAG, "serviceAdded");
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Log.i(TAG, "serviceRemoved");
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        Log.i(TAG, "serviceResolved");
        try {
            registerService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject info = toJsonObject(event.getInfo());
        Log.i(TAG, "serviceResolved:" + info.toString());
        List<EcrDevice> list = getPairedDeviceList();
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            EcrDevice device = list.get(i);
            if (info.containsKey("name") && device.getWs_address().equals(info.getString("name"))) {
                if (!info.getString("ip_address").equals(device.getIp_address()) || !info.getString("port").equals(device.getPort())) {
                    if (!"".equals(info.getString("port"))) {
                        device.setPort(info.getString("port"));
                    }
                    if (!"".equals(info.getString("ip_address"))) {
                        device.setIp_address(info.getString("ip_address"));
                    }
                }
            }
            array.add(JSON.toJSON(device));
        }
        if (!array.isEmpty()) {
            SharePreferenceUtil.put(Constants.ECR_HUB_PAIR_LIST_KEY, array.toString());
        }
    }

    /**
     * 解析获取到的客户端的信息
     *
     * @param sInfo 客户端信息
     * @return json格式信息
     */
    private JSONObject toJsonObject(ServiceInfo sInfo) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject();
            String ipv4 = "";
            if (sInfo.getInet4Addresses().length > 0) {
                ipv4 = sInfo.getInet4Addresses()[0].getHostAddress();
            }
            if (sInfo.getName().contains("_")) {
                jsonObj.put("name", sInfo.getName().split("_")[0]);
            } else {
                jsonObj.put("name", sInfo.getName());
            }
            jsonObj.put("ip_address", ipv4);
            jsonObj.put("port", sInfo.getPort());
//            byte[] allInfo = sInfo.getTextBytes();
//            int allLen = allInfo.length;
//            byte fLen;
//            for (int index = 0; index < allLen; index += fLen) {
//                fLen = allInfo[index++];
//                byte[] fData = new byte[fLen];
//                System.arraycopy(allInfo, index, fData, 0, fLen);
//                String fInfo = new String(fData, StandardCharsets.UTF_8);
//                Log.i("test", fInfo);
//                JSONObject jsonInfo = JSONObject.parseObject(fInfo);
//                if (!jsonInfo.isEmpty()) {
//                    for (String key : jsonInfo.keySet()) {
//                        jsonObj.put(key, jsonInfo.getString(key));
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }
}
