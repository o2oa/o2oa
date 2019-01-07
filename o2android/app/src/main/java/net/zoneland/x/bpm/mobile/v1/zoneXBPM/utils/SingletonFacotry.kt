package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.content.Context

/**
 * Created by fancy on 2017/6/5.
 */

interface SingletonFactory<T> {
    fun instance(): T
}

interface SingletonFactoryWithContext<T> {
    fun instance(context: Context): T
}