package com.wiseasy.ecr.sdk.bean;


public class EcrMessageData {
    //消息主题
    public final static String ECR_HUB_TOPIC_PAIR = "ecrhub.pair";
    public final static String ECR_HUB_TOPIC_UNPAIR = "ecrhub.unpair";


    public final static String ECR_HUB_TOPIC_WLAN_PAY = "ecrhub.pay.order";
    public final static String ECR_HUB_TOPIC_WLAN_QUERY = "ecrhub.pay.query";
    public final static String ECR_HUB_TOPIC_WLAN_CLOSE = "ecrhub.pay.close";


    /**
     * 银行卡支付方式
     */
    public final static String ECR_HUB_BANKCARD_PAY_TYPE = "BANKCARD";
    /**
     * 扫码主扫
     */
    public final static String ECR_HUB_QR_C_SCAN_B_PAY_TYPE = "QR_C_SCAN_B";
    /**
     * 扫码被扫
     */
    public final static String ECR_HUB_QR_B_SCAN_C = "QR_B_SCAN_C";



    /**
     * 消息主题
     */
    private String topic;
    /**
     * 应用id
     */
    private String app_id;

    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 交易是否成功
     */
    private String response_code;

    private String response_msg;

    private BizData biz_data;

    private DeviceData device_data;

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_msg(String response_msg) {
        this.response_msg = response_msg;
    }

    public String getResponse_msg() {
        return response_msg;
    }

    public void setBiz_data(BizData biz_data) {
        this.biz_data = biz_data;
    }

    public void setDevice_data(DeviceData device_data) {
        this.device_data = device_data;
    }

    public BizData getBiz_data() {
        if (null == biz_data) {
            biz_data = new BizData();
        }
        return biz_data;
    }

    public DeviceData getDevice_data() {
        if (null == device_data) {
            device_data = new DeviceData();
        }
        return device_data;
    }


    public void setCall_app_mode(String call_app_mode) {
        this.call_app_mode = call_app_mode;
    }

    public String getCall_app_mode() {
        return call_app_mode;
    }

    private String call_app_mode;

    public void setCallAppMode(String callAppMode) {
        this.call_app_mode = callAppMode;
    }

    public String getCallAppMode() {
        return call_app_mode;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getApp_id() {
        return app_id;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public String getTopic() {
        return topic;
    }


    public class DeviceData {
        /**
         * device mac address
         */
        private String mac_address = "";
        /**
         * device name
         */
        private String device_name = "";
        /**
         * device alias name
         */
        private String alias_name = "";
        /**
         * server ip address
         */
        private String ip_address = "";
        /**
         * server port number
         */
        private String port = "";

        public void setAlias_name(String alias_name) {
            this.alias_name = alias_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public void setMac_address(String mac_address) {
            this.mac_address = mac_address;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getAlias_name() {
            return alias_name;
        }

        public String getDevice_name() {
            return device_name;
        }

        public String getIp_address() {
            return ip_address;
        }

        public String getMac_address() {
            return mac_address;
        }

        public String getPort() {
            return port;
        }
    }

    public class BizData {
        /**
         * 是否需要终端确认
         */
        private boolean confirm_on_terminal;

        /**
         * 时间有效期
         */
        private int expires;

        /**
         * 交易号
         */
        private String trans_no;

        /**
         * 调用方订单号
         */
        private String merchant_order_no;

        /**
         * 原调用方订单号
         */
        private String orig_merchant_order_no;

        /**
         * 支付方式类目
         */
        private String pay_scenario;

        /**
         * 支付方式id
         */
        private String pay_method_id;

        /**
         * 附加数据
         */
        private String attach;

        /**
         * 描述信息
         */
        private String description;

        /**
         * 回调地址
         */
        private String notify_url;

        /**
         * 交易状态
         */
        private String trans_status;

        private String order_amount;

        private String cashback_amount;

        private String tip_amount;

        private String trans_type;



        public boolean isConfirm_on_terminal() {
            return confirm_on_terminal;
        }

        public String getPay_scenario() {
            return pay_scenario;
        }

        public void setPay_scenario(String pay_scenario) {
            this.pay_scenario = pay_scenario;
        }


        String token;

        public void setCashback_amount(String cashback_amount) {
            this.cashback_amount = cashback_amount;
        }

        public String getCashback_amount() {
            return cashback_amount;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public String getOrder_amount() {
            return order_amount;
        }

        public String getTip_amount() {
            return tip_amount;
        }

        public String getTrans_type() {
            return trans_type;
        }

        public void setOrder_amount(String order_amount) {
            this.order_amount = order_amount;
        }

        public void setTip_amount(String tip_amount) {
            this.tip_amount = tip_amount;
        }

        public void setTrans_type(String trans_type) {
            this.trans_type = trans_type;
        }

        public String getTrans_status() {
            return trans_status;
        }

        public int getExpires() {
            return expires;
        }

        public void setTrans_status(String trans_status) {
            this.trans_status = trans_status;
        }

        public void setAttach(String attach) {
            this.attach = attach;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMerchant_order_no(String merchant_order_no) {
            this.merchant_order_no = merchant_order_no;
        }

        public void setNotify_url(String notify_url) {
            this.notify_url = notify_url;
        }

        public void setOrig_merchant_order_no(String orig_merchant_order_no) {
            this.orig_merchant_order_no = orig_merchant_order_no;
        }

        public void setPay_method_id(String pay_method_id) {
            this.pay_method_id = pay_method_id;
        }

        public void setTrans_no(String trans_no) {
            this.trans_no = trans_no;
        }

        public String getAttach() {
            return attach;
        }

        public String getDescription() {
            return description;
        }

        public String getMerchant_order_no() {
            return merchant_order_no;
        }

        public String getNotify_url() {
            return notify_url;
        }

        public String getOrig_merchant_order_no() {
            return orig_merchant_order_no;
        }

        public String getPay_method_id() {
            return pay_method_id;
        }

        public String getEndAmount() {
            try {
                int amount = Integer.parseInt(order_amount);
                if (null != order_amount) {
                    if (null != tip_amount) {
                        amount += Integer.parseInt(tip_amount);
                    }
                    if (null != cashback_amount) {
                        amount += Integer.parseInt(cashback_amount);
                    }
                }
                return "" + amount;
            } catch (Exception e) {
                return order_amount;
            }
        }

        public String getTrans_no() {
            return trans_no;
        }


        /**
         * 获取Cashier识别应用间调用的支付方式
         */
        public String getCashierInvokePayType(String type) {
            switch (type) {
                case ECR_HUB_BANKCARD_PAY_TYPE:
                    return "CARD";
                case ECR_HUB_QR_C_SCAN_B_PAY_TYPE:
                    return "SCANQR";
                case ECR_HUB_QR_B_SCAN_C:
                    return "BSCANQR";
            }
            return "CARD";
        }

        public void setConfirm_on_terminal(boolean confirm_on_terminal) {
            this.confirm_on_terminal = confirm_on_terminal;
        }

        public boolean getConfirm_on_terminal() {
            return confirm_on_terminal;
        }


        public void setExpires(int expires) {
            this.expires = expires;
        }

    }

}