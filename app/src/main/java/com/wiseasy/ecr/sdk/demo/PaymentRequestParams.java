package com.wiseasy.ecr.sdk.demo;


public class PaymentRequestParams {

    String topic;

    String app_id;

    BizData biz_data;

    String timestamp;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public BizData getBiz_data() {
        return biz_data;
    }

    public void setBiz_data(BizData biz_data) {
        this.biz_data = biz_data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class BizData {

        public BizData() {
        }

        private String trans_type;

        private String merchant_order_no;

        private String orig_merchant_order_no;

        private String order_amount;

        private String tip_amount;

        private String cashback_amount;

        private String pay_method_id;

        private String pay_scenario;

        private String attach;

        private String description;

        private String notify_url;

        private int expires;

        private boolean confirm_on_terminal;

        private boolean required_terminal_authentication;

        private String trans_no;

        private String trans_status;

        private String trans_end_time;

        public String getTrans_type() {
            return trans_type;
        }

        public void setTrans_type(String trans_type) {
            this.trans_type = trans_type;
        }

        public String getMerchant_order_no() {
            return merchant_order_no;
        }

        public void setMerchant_order_no(String merchant_order_no) {
            this.merchant_order_no = merchant_order_no;
        }

        public String getOrig_merchant_order_no() {
            return orig_merchant_order_no;
        }

        public void setOrig_merchant_order_no(String orig_merchant_order_no) {
            this.orig_merchant_order_no = orig_merchant_order_no;
        }

        public String getOrder_amount() {
            return order_amount;
        }

        public void setOrder_amount(String order_amount) {
            this.order_amount = order_amount;
        }

        public String getTip_amount() {
            return tip_amount;
        }

        public void setTip_amount(String tip_amount) {
            this.tip_amount = tip_amount;
        }

        public String getCashback_amount() {
            return cashback_amount;
        }

        public void setCashback_amount(String cashback_amount) {
            this.cashback_amount = cashback_amount;
        }

        public String getPay_method_id() {
            return pay_method_id;
        }

        public void setPay_method_id(String pay_method_id) {
            this.pay_method_id = pay_method_id;
        }

        public String getPay_scenario() {
            return pay_scenario;
        }

        public void setPay_scenario(String pay_scenario) {
            this.pay_scenario = pay_scenario;
        }

        public String getAttach() {
            return attach;
        }

        public void setAttach(String attach) {
            this.attach = attach;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getNotify_url() {
            return notify_url;
        }

        public void setNotify_url(String notify_url) {
            this.notify_url = notify_url;
        }

        public int getExpires() {
            return expires;
        }

        public void setExpires(int expires) {
            this.expires = expires;
        }

        public boolean isConfirm_on_terminal() {
            return confirm_on_terminal;
        }

        public void setConfirm_on_terminal(boolean confirm_on_terminal) {
            this.confirm_on_terminal = confirm_on_terminal;
        }

        public boolean isRequired_terminal_authentication() {
            return required_terminal_authentication;
        }

        public void setRequired_terminal_authentication(boolean required_terminal_authentication) {
            this.required_terminal_authentication = required_terminal_authentication;
        }

        public String getTrans_no() {
            return trans_no;
        }

        public void setTrans_no(String trans_no) {
            this.trans_no = trans_no;
        }

        public String getTrans_status() {
            return trans_status;
        }

        public void setTrans_status(String trans_status) {
            this.trans_status = trans_status;
        }

        public String getTrans_end_time() {
            return trans_end_time;
        }

        public void setTrans_end_time(String trans_end_time) {
            this.trans_end_time = trans_end_time;
        }
    }



}

