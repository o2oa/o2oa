package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by fancy on 2017/4/7.
 */


fun ViewGroup.inflate(resId: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(resId, this, attachToRoot)
}