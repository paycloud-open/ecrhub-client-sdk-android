package com.wiseasy.ecr.sdk.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wiseasy.ecr.sdk.EcrClient
import com.wiseasy.ecr.sdk.listener.EcrConnectListener
import com.wiseasy.ecr.sdk.listener.EcrResponseCallBack
import kotlinx.android.synthetic.main.activity_usb.tv_btn_open
import kotlinx.android.synthetic.main.activity_usb.ll_layout1
import kotlinx.android.synthetic.main.activity_usb.tv_btn_close_order
import kotlinx.android.synthetic.main.activity_usb.tv_btn_init
import kotlinx.android.synthetic.main.activity_usb.tv_btn_query
import kotlinx.android.synthetic.main.activity_usb.tv_btn_refund
import kotlinx.android.synthetic.main.activity_usb.tv_btn_sale

class UsbActivity : Activity(), EcrConnectListener, View.OnClickListener {

    private val mClient = EcrClient.getInstance()
    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)
        mClient.init(this, this)
        tv_btn_open.setOnClickListener(this)
        tv_btn_sale.setOnClickListener(this)
        tv_btn_refund.setOnClickListener(this)
        tv_btn_query.setOnClickListener(this)
        tv_btn_close_order.setOnClickListener(this)
        tv_btn_init.setOnClickListener(this)
    }

    override fun onConnect() {
        Log.e("Test", "onConnect")
        runOnUiThread {
            ll_layout1.visibility = View.VISIBLE
            tv_btn_open.visibility = View.GONE
            Toast.makeText(this, "Connect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onDisconnect() {
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            tv_btn_open.visibility = View.VISIBLE
            Toast.makeText(this, "Disconnect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onError(errorCode: String?, errorMsg: String?) {
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            tv_btn_open.visibility = View.VISIBLE
            Toast.makeText(this, "Connect Fail!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.tv_btn_init -> {
                mClient.getTerminalInfo(object :
                    EcrResponseCallBack {
                    override fun onError(errorCode: String?, errorMsg: String?) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "init error", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onSuccess(data: String) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "init success:$data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                })
            }

            R.id.tv_btn_open -> {
                mClient.connectUsb()
            }


            R.id.tv_btn_cancel -> {
                mClient.disConnect()
            }

            R.id.tv_btn_sale -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, PaymentActivity::class.java))
            }

            R.id.tv_btn_refund -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, RefundActivity::class.java))
            }

            R.id.tv_btn_query -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, QueryActivity::class.java))
            }

        }
    }
}