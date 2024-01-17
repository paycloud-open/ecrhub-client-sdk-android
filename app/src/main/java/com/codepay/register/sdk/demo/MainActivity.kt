package com.codepay.register.sdk.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.ECRHubConfig
import com.codepay.register.sdk.listener.ECRHubConnectListener
import com.codepay.register.sdk.listener.ECRHubPairListener
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import com.codepay.register.sdk.util.ECRHubMessageData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), ECRHubConnectListener, OnClickListener, ECRHubPairListener {
    companion object {
        lateinit var mClient: ECRHubClient
    }

    var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config = ECRHubConfig()
        mClient = ECRHubClient.getInstance()
        mClient.init(config, this)
        tv_btn_1.setOnClickListener(this)
        tv_btn_2.setOnClickListener(this)
        tv_btn_4.setOnClickListener(this)
        tv_btn_6.setOnClickListener(this)
        tv_btn_7.setOnClickListener(this)
        tv_btn_8.setOnClickListener(this)
        tv_btn_9.setOnClickListener(this)
        tv_btn_10.setOnClickListener(this)
    }

    override fun onConnect() {
        Log.e("Test", "onConnect")
        runOnUiThread {
            ll_layout1.visibility = View.VISIBLE
            tv_btn_2.visibility = View.VISIBLE
            Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onDisconnect() {
        Log.e("Test", "onDisconnect")
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            tv_btn_2.visibility = View.GONE
            Toast.makeText(this, "断开连接成功", Toast.LENGTH_LONG).show()
        }
        isConnected = false
    }

    override fun onError(errorCode: String?, errorMsg: String?) {
        Log.e("Test", "onError")
        isConnected = false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_btn_1 -> {
                // mClient.startServerConnect("傻逼YK")
            }

            R.id.tv_btn_2 -> {
                mClient.disConnect()
            }

            R.id.tv_btn_10 -> {
                mClient.disConnect()
                //    mClient.closeServerConnect()
                finish()
            }

            R.id.tv_btn_4 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                tv_text_1.text = tv_text_1.text.toString() + "\n" + "开始取消配对..."
            }

            R.id.tv_btn_6 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, PaymentActivity::class.java))
            }

            R.id.tv_btn_7 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, RefundActivity::class.java))
            }

            R.id.tv_btn_8 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, QueryActivity::class.java))
            }

            R.id.tv_btn_9 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, CloseActivity::class.java))
            }
        }
    }

    override fun onDevicePair(data: ECRHubMessageData?, ip: String?) {

    }
}
