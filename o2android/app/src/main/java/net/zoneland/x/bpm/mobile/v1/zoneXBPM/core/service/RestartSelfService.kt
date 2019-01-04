package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils

/**
 * Created by fancyLou on 21/03/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class RestartSelfService : Service() {

    companion object {
        val RESTART_PACKAGE_NAME_EXTRA_NAME = "RESTART_PACKAGE_NAME_EXTRA_NAME"
    }
    private val handler:Handler by lazy { Handler() }
    private val DELAYED_STOP_TIME = 1000L
    private var restartPackageName: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        restartPackageName = intent?.getStringExtra(RESTART_PACKAGE_NAME_EXTRA_NAME) ?: ""
        if (!TextUtils.isEmpty(restartPackageName)) {
            handler.postDelayed({
                val launchIntent = packageManager.getLaunchIntentForPackage(restartPackageName)
                startActivity(launchIntent)
                this@RestartSelfService.stopSelf()
            }, DELAYED_STOP_TIME)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}