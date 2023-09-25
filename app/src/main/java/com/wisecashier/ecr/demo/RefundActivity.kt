package com.wisecashier.ecr.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.wisecashier.ecr.sdk.client.payment.PaymentParams
import com.wisecashier.ecr.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_refund.edit_input_amount
import kotlinx.android.synthetic.main.activity_refund.edit_input_merchant_order_no
import kotlinx.android.synthetic.main.activity_refund.tv_btn_1
import kotlinx.android.synthetic.main.activity_refund.tv_btn_2
import kotlinx.android.synthetic.main.activity_refund.tv_btn_3

class RefundActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "请输入地址", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val merchantOrderNo = edit_input_merchant_order_no.text.toString()
            if (merchantOrderNo.isEmpty()) {
                Toast.makeText(this, "请输入商户订单号", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val params = PaymentParams()
            params.transType = 2
            params.appId = "wz6012822ca2f1as78"
            params.merchantOrderNo = merchantOrderNo
            params.payMethod = "BANKCARD"
            params.transAmount = "2"
            params.msgId = "111111"
            val voiceData = params.voice_data
            voiceData.content = "AddpayCashier2 Received a new order"
            voiceData.content_locale = "en-US"
            params.voice_data = voiceData
            runOnUiThread {
                tv_btn_3.text =
                    tv_btn_3.text.toString() + "\n" + "交易发送数据" + params.toJSON().toString()
            }
            MainActivity.mClient.payment.purchase(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "交易失败" + errorMsg
                    }
                }

                override fun onSuccess(data: String?) {
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "交易结果数据" + data.toString()
                    }
                }
            })
        }
    }
}