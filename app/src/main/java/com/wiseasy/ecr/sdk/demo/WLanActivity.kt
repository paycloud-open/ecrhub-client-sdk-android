package com.wiseasy.ecr.sdk.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.wiseasy.ecr.sdk.EcrClient
import com.wiseasy.ecr.sdk.EcrWifiDiscoveryService
import com.wiseasy.ecr.sdk.listener.EcrConnectListener
import com.wiseasy.ecr.sdk.listener.EcrPairListener
import com.wiseasy.ecr.sdk.bean.EcrMessageData
import kotlinx.android.synthetic.main.activity_wlan.*

class WLanActivity : Activity(), EcrConnectListener, OnClickListener,
    EcrPairListener {

    private val TAG = "WLanActivity"

    private var discoveryService: EcrWifiDiscoveryService? = null
    private val mClient = EcrClient.getInstance()

    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wlan)


        mClient.init(this, this)
        discoveryService = EcrWifiDiscoveryService(this)
        tv_btn_start.setOnClickListener(this)
        tv_btn_connect.setOnClickListener(this)
        tv_btn_disconnect.setOnClickListener(this)
        tv_btn_unpair.setOnClickListener(this)
        tv_btn_sale.setOnClickListener(this)
        tv_btn_refund.setOnClickListener(this)
        tv_btn_auth.setOnClickListener(this)
        tv_btn_complete.setOnClickListener(this)
        tv_btn_cashback.setOnClickListener(this)
        tv_btn_query.setOnClickListener(this)
        tv_btn_exit.setOnClickListener(this)

        val mPairedList = discoveryService!!.pairedDeviceList
        if (mPairedList.isNotEmpty()) {
            val ip = "ws://" + mPairedList[0].ip_address + ":" + mPairedList[0].port
            tv_text_1.text = "paired $ip"
        }


    }

    override fun onConnect() {
        Log.e(TAG, "onConnect")
        runOnUiThread {
            ll_layout1.visibility = View.VISIBLE
            val mPairedList = discoveryService!!.pairedDeviceList
            val ip = "ws://" + mPairedList[0].ip_address + ":" + mPairedList[0].port
            tv_text_1.text = "connect $ip"
            Toast.makeText(this, "Connect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onDisconnect() {
        Log.e(TAG, "onDisconnect")
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            Toast.makeText(this, "Disconnect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = false
    }

    override fun onError(errorCode: String?, errorMsg: String?) {
        Log.e(TAG, "onError")
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            Toast.makeText(this, "Connect Error:$errorMsg", Toast.LENGTH_LONG).show()
        }
        isConnected = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_btn_start -> {
                // Get the connectivity manager
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                // Check if the network is connected and is of type WI-FI
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    discoveryService?.start(this@WLanActivity)
                    runOnUiThread {
                        Toast.makeText(this, "Start Server", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // If not connected to WI-FI, display a prompt
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Please connect to WI-FI before performing this operation",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            R.id.tv_btn_connect -> {
                // Get the connectivity manager
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                // Check if the network is connected and is of type WI-FI
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    val mPairedList = discoveryService!!.pairedDeviceList
                    for (device in mPairedList) {
                        Log.e(TAG, "terminal_sn " + device.terminal_sn)
                        Log.e(TAG, "ws_address " +  device.ws_address)
                    }
                    if (mPairedList.isEmpty()) {
                        runOnUiThread {
                            Toast.makeText(this, "Paired list is empty", Toast.LENGTH_LONG).show()
                        }
                        return
                    }
                    if (isConnected) {
                        runOnUiThread {
                            Toast.makeText(this, "Server is connected", Toast.LENGTH_LONG).show()
                        }
                        return
                    }
                    // Perform the connection operation
                    val ip = "ws://" + mPairedList[0].ip_address + ":" + mPairedList[0].port
                    mClient.connectWifi(ip)
                    runOnUiThread {
                        tv_text_1.text = "connect $ip"
                    }
                } else {
                    // If not connected to WI-FI, display a prompt
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Please connect to WI-FI before performing this operation",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }


            R.id.tv_btn_disconnect -> {
                if (!isConnected) {
                    Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    return
                }
                mClient.disConnect()
                isConnected = false

                ll_layout1.visibility = View.GONE

                val mPairedList = discoveryService!!.pairedDeviceList
                if (mPairedList.isNotEmpty()) {
                    val ip = "ws://" + mPairedList[0].ip_address + ":" + mPairedList[0].port
                    tv_text_1.text = "paired $ip"
                }
            }

            R.id.tv_btn_unpair -> {
                if (!isConnected) {
                    Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    return
                }
                val mPairedList = discoveryService!!.pairedDeviceList
                Log.i(TAG, "mPairedList: $mPairedList")
                if (mPairedList.isEmpty()) {
                    Toast.makeText(this, "Paired list is empty", Toast.LENGTH_LONG).show()
                    return
                }

                discoveryService?.unPair(mPairedList[0])

                ll_layout1.visibility = View.GONE
                tv_text_1.text = ""

            }

            R.id.tv_btn_exit -> {
                runOnUiThread {
                    tv_text_1.text = "The APP is exiting..."
                }
                discoveryService?.stop()
                mClient.disConnect()
                finish()
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

            R.id.tv_btn_auth -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, AuthActivity::class.java))
            }

            R.id.tv_btn_complete -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, AuthCompleteActivity::class.java))
            }

            R.id.tv_btn_cashback -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, CashBackActivity::class.java))
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

    override fun onDevicePair(data: EcrMessageData?, ip: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pair Device")
            builder.setMessage("Are you pair the " + data?.device_data?.device_name + " device?")
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Pair"
            ) { p0, _ ->
                discoveryService?.confirmPair(data)
                mClient.connectWifi(ip)
                p0?.dismiss()
            }
            builder.setNegativeButton("Cancel") { p0, _ ->
                discoveryService?.cancelPair(data)
                p0?.dismiss()
            }
            builder.show()
        }
    }

    override fun onDeviceUnpair(data: EcrMessageData?) {
        val mPairedList = discoveryService!!.pairedDeviceList
        if (mPairedList.isEmpty()) {
            runOnUiThread{
                tv_text_1.text = ""
            }
        }
    }
}
