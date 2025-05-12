package com.wiseasy.ecr.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.wiseasy.ecr.sdk.EcrClient
import com.wiseasy.ecr.sdk.listener.EcrResponseCallBack
import com.wiseasy.ecr.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_query.edit_input_merchant_order_no
import kotlinx.android.synthetic.main.activity_query.tv_btn_1
import kotlinx.android.synthetic.main.activity_query.tv_btn_2
import kotlinx.android.synthetic.main.activity_query.tv_btn_3

class QueryActivity : Activity() {

    private val mClient = EcrClient.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)

        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val no = sharedPreferences.getString("merchant_order_no", "")
        edit_input_merchant_order_no.setText(no)


        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val merchantOrderNo = edit_input_merchant_order_no.text.toString()

            if (merchantOrderNo.isEmpty()) {
                Toast.makeText(this, "Please input merchant order no", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val params = PaymentRequestParams()
            params.app_id = "wz6012822ca2f1as78"
            params.topic = Constants.QUERY_TOPIC
            params.biz_data = PaymentRequestParams.BizData()
            params.biz_data.merchant_order_no = merchantOrderNo

            val json = JSON.toJSONString(params)

            runOnUiThread {
                tv_btn_3.text = "Send Query data -->\n$json"
            }

            mClient.queryTransaction(json, object :
                EcrResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\nFailure -->\n" + errorMsg
                    }
                }

                override fun onSuccess(data: String) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\nReceive Query Result -->\n" + data
                    }
                }

            })
        }
    }
}