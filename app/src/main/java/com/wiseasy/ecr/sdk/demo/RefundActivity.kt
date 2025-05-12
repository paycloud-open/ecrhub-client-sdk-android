package com.wiseasy.ecr.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.wiseasy.ecr.sdk.EcrClient
import com.wiseasy.ecr.sdk.listener.EcrResponseCallBack
import com.wiseasy.ecr.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_payment.confirm_on_terminal
import kotlinx.android.synthetic.main.activity_payment.tv_btn_3
import kotlinx.android.synthetic.main.activity_refund.*

class RefundActivity : Activity() {

    private val mClient = EcrClient.getInstance()
    private var merchantOrderNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund)

        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val no = sharedPreferences.getString("merchant_order_no", "")
        edit_input_merchant_order_no.setText(no)


        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val origMerchantOrderNo = edit_input_merchant_order_no.text.toString()
            if (origMerchantOrderNo.isEmpty()) {
                Toast.makeText(this, "Please input orig merchant order no", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val params = PaymentRequestParams()
            params.app_id = "wz6012822ca2f1as78"
            params.topic = Constants.PAYMENT_TOPIC
            params.timestamp = System.currentTimeMillis().toString()

            params.biz_data = PaymentRequestParams.BizData()
            params.biz_data.trans_type = Constants.TRANS_TYPE_REFUND
            params.biz_data.order_amount = amount
            merchantOrderNo = System.currentTimeMillis().toString()
            params.biz_data.merchant_order_no = merchantOrderNo
            params.biz_data.orig_merchant_order_no = origMerchantOrderNo
            params.biz_data.pay_scenario = "SWIPE_CARD"
            params.biz_data.isConfirm_on_terminal = confirm_on_terminal.isChecked

            val json = JSON.toJSONString(params)

            runOnUiThread {
                tv_btn_3.text = "Send Refund data -->\n$json"
            }

            mClient.doTransaction(json, object :
                EcrResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\nFailure -->\n" + errorMsg
                    }
                }

                override fun onSuccess(data: String) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\nReceive Refund Result -->\n" + data
                    }
                }
            })
        }


        tv_btn_cancel.setOnClickListener {

            if (merchantOrderNo.isNullOrEmpty()) {
                Toast.makeText(this, "Transaction does not exist", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val params = PaymentRequestParams()
            params.app_id = "wz6012822ca2f1as78"
            params.topic = Constants.CLOSE_TOPIC
            params.biz_data = PaymentRequestParams.BizData()
            params.biz_data.merchant_order_no = merchantOrderNo

            val json = JSON.toJSONString(params)

            runOnUiThread {
                tv_btn_3.text = tv_btn_3.text.toString() + "\nSend Close data -->\n $json"
            }
            mClient.cancelTransaction(json)
        }
    }
}