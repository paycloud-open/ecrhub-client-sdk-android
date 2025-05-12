package com.wiseasy.ecr.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.wiseasy.ecr.sdk.EcrClient
import com.wiseasy.ecr.sdk.listener.EcrResponseCallBack
import com.wiseasy.ecr.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_cashback.*
import kotlinx.android.synthetic.main.activity_payment.confirm_on_terminal
import kotlinx.android.synthetic.main.activity_payment.tv_btn_3
import kotlinx.android.synthetic.main.activity_payment.tv_btn_cancel

class CashBackActivity : Activity() {
    private val mClient = EcrClient.getInstance()
    private var merchantOrderNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashback)

        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            val cashback = edit_input_cashback_amount.text.toString()
            if (amount.isEmpty() || cashback.isEmpty()) {
                Toast.makeText(this, "Please input amount or cashback amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val params = PaymentRequestParams()
            params.app_id = "wz6012822ca2f1as78"
            params.topic = Constants.PAYMENT_TOPIC
            params.timestamp = System.currentTimeMillis().toString()

            params.biz_data = PaymentRequestParams.BizData()
            params.biz_data.trans_type = Constants.TRANS_TYPE_CASH_BACK
            params.biz_data.order_amount = amount
            params.biz_data.cashback_amount = cashback
            merchantOrderNo = System.currentTimeMillis().toString()
            params.biz_data.merchant_order_no = merchantOrderNo
            params.biz_data.pay_scenario = "SWIPE_CARD"
            params.biz_data.isConfirm_on_terminal = confirm_on_terminal.isChecked

            val json = JSON.toJSONString(params)
            runOnUiThread {
                tv_btn_3.text = "Send CashBack data -->\n$json"
            }

            mClient.doTransaction(json, object :
                EcrResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\nFailure -->\n" + errorMsg
                    }
                }

                override fun onSuccess(data: String) {
                    val editor = sharedPreferences.edit()
                    editor.putString("merchant_order_no", merchantOrderNo)
                    editor.apply()
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\nReceive CashBack Result -->\n" + data
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