package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2

/**
 * Created by fancyLou on 2018/11/22.
 * Copyright © 2018 O2. All rights reserved.
 */


class SharedPreferencesHelper(cxt: Context)   {

    private var context: Context = cxt

    fun prefs(): SharedPreferences = context.getSharedPreferences(O2.PREFERENCE_FILE, Context.MODE_PRIVATE)

    
}