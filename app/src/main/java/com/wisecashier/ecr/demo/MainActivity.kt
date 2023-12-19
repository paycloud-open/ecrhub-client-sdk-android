package com.wisecashier.ecr.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.wisecashier.ecr.sdk.client.ECRHubClient
import com.wisecashier.ecr.sdk.client.ECRHubConfig
import com.wisecashier.ecr.sdk.listener.ECRHubConnectListener
import com.wisecashier.ecr.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), ECRHubConnectListener, OnClickListener {
    companion object {
        lateinit var mClient: ECRHubClient
    }

    var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config = ECRHubConfig()
        mClient = ECRHubClient(this, config, this)
        tv_btn_1.setOnClickListener(this)
        tv_btn_2.setOnClickListener(this)
        tv_btn_4.setOnClickListener(this)
        tv_btn_5.setOnClickListener(this)
        tv_btn_6.setOnClickListener(this)
        tv_btn_7.setOnClickListener(this)
        tv_btn_8.setOnClickListener(this)
        tv_btn_9.setOnClickListener(this)
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
                mClient.startServerConnect("傻逼YK")
            }

            R.id.tv_btn_2 -> {
                mClient.disConnect()
            }

            R.id.tv_btn_4 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                tv_text_1.text = tv_text_1.text.toString() + "\n" + "开始取消配对..."
                mClient.requestUnPair("傻逼YK", object :
                    ECRHubResponseCallBack {
                    override fun onError(errorCode: String?, errorMsg: String?) {
                        runOnUiThread {
                            tv_text_1.text =
                                tv_text_1.text.toString() + "\n" + "取消配对失败" + errorMsg
                        }
                    }

                    override fun onSuccess(data: String?) {
                        runOnUiThread {
                            tv_text_1.text =
                                tv_text_1.text.toString() + "\n" + "取消配对成功" + data.toString()
                        }
                    }
                })
            }

            R.id.tv_btn_5 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "未连接服务器", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                tv_text_1.text = tv_text_1.text.toString() + "\n" + "开始初始化..."
                mClient.init(object :
                    ECRHubResponseCallBack {
                    override fun onError(errorCode: String?, errorMsg: String?) {
                        runOnUiThread {
                            tv_text_1.text =
                                tv_text_1.text.toString() + "\n" + "初始化失败" + errorMsg
                        }
                    }

                    override fun onSuccess(data: String?) {
                        runOnUiThread {
                            tv_text_1.text =
                                tv_text_1.text.toString() + "\n" + "初始化成功" + data.toString()
                        }
                    }
                })
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
}
