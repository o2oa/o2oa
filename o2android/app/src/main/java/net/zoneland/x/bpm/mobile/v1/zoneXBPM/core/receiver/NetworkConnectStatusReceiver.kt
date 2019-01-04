package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * 网络连接情况监听
 * Created by fancyLou on 21/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class NetworkConnectStatusReceiver(private val callBack:(isConnected:Boolean)->Unit): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            val cManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cManager.activeNetworkInfo
            if (activeNetwork==null || !activeNetwork.isConnected){
                callBack(false)
            }else{
                callBack(true)
            }
        }
    }
}