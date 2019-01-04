package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * 皮肤切换广播接收
 * Created by fancyLou on 2017/11/17.
 * Copyright © 2017 O2. All rights reserved.
 */

class SkinChangeBroadReceiver(val callback:(() -> Unit)): BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val isChanged = intent?.extras?.getBoolean(O2.SKIN_CHANGE_BROAD_CAST_KEY) ?: false
        if (isChanged) {
            XLog.info("skin is changed........................")
            callback.invoke()
        }
    }
}