package com.wiseasy.ecr.sdk.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.tv_btn_wlan_mode

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_btn_wlan_mode.setOnClickListener {
            startActivity(Intent(applicationContext, WLanActivity::class.java))
        }
    }
}