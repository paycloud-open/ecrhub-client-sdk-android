package com.wisecashier.ecr.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.wisecashier.ecr.sdk.client.ECRHubClient
import com.wisecashier.ecr.sdk.client.ECRHubConfig
import com.wisecashier.ecr.sdk.client.payment.PaymentParams
import com.wisecashier.ecr.sdk.listener.ECRHubConnectListener
import com.wisecashier.ecr.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : Activity(), ECRHubConnectListener {
    companion object {
        lateinit var mClient: ECRHubClient
    }
    var isConnected: Boolean = false
    var merchantOrderNo: String? = null
    fun getCurDateStr(format: String?): String? {
        val c = Calendar.getInstance()
        return date2Str(c, format)
    }

    fun date2Str(c: Calendar?, format: String?): String? {
        return if (c == null) null else date2Str(
            c.time,
            format
        )
    }

    fun date2Str(d: Date?, format: String?): String? {
        var format = format
        return if (d == null) {
            null
        } else {
            if (format == null || format.length == 0) {
                format = "yyyy-MM-dd HH:mm:ss"
            }
            val sdf = SimpleDateFormat(format)
            sdf.format(d)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config = ECRHubConfig()
        tv_btn_5.setOnClickListener {
            val text = edit_input_ip.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "请输入ip地址", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mClient = ECRHubClient("ws://" + text, config, this)
            mClient.connect()
        }
        tv_btn_2.setOnClickListener {
            startActivity(Intent(applicationContext, PaymentActivity::class.java))
        }
        tv_btn_4.setOnClickListener {
            val params = PaymentParams()
            params.origMerchantOrderNo = merchantOrderNo
            params.appId = "wz6012822ca2f1as78"
            params.msgId = "11322"
            mClient.payment.close(params, object :
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

        tv_btn_1.setOnClickListener {
            if(!isConnected) {
                runOnUiThread {
                    Toast.makeText(this, "请先初始化", Toast.LENGTH_LONG).show()
                }
            }
            tv_btn_3.text = "开始初始化..."
            /**
             * 初始化连接接口
             * @param url 服务器IP地址
             * @param cackback 请求回调
             */
            mClient.init(object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "初始化失败" + errorMsg
                    }
                }

                override fun onSuccess(data: String?) {
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "初始化成功" + data.toString()
                    }
                }
            })
        }
    }

    override fun onConnect() {
        Log.e("Test", "onConnect")
        runOnUiThread {
            Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onDisconnect() {
        Log.e("Test", "onDisconnect")
        isConnected = false
    }

    override fun onError(errorCode: String?, errorMsg: String?) {
        Log.e("Test", "onError")
        isConnected = false
    }
}
