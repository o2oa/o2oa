package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.baidu

import android.content.Context
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancy on 2017/7/21.
 * Copyright © 2017 O2. All rights reserved.
 */

class AttendanceLocationListener(val context: Context, val receiveListener: LocationReceiveListener) : BDLocationListener {

    val mLocationClient = LocationClient(context)
    var sendKey = ""
    var signDesc = ""
    init {
        mLocationClient.registerLocationListener(this)
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll")//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0)//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true)//可选，设置是否需要地址信息，默认不需要
        option.isOpenGps = true//可选，默认false,设置是否使用gps
        option.isLocationNotify = true//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true)//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true)//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false)//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false)//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false)//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.locOption = option
    }

    override fun onReceiveLocation(location: BDLocation?) {
        XLog.debug("onReceiveLocation.................")
        receiveListener.onReceive(location, sendKey, signDesc)
        mLocationClient.stop()
        XLog.debug("onReceiveLocation.................end")
    }

    override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
        XLog.info("connectHotSpot $p0, $p1")
    }

    /**
     * 开始定位
     */
    fun requestLocation(sendKey: String, signDesc: String) {
        this.sendKey = sendKey
        this.signDesc = signDesc
        mLocationClient.start()
    }


    interface LocationReceiveListener {
        fun onReceive(location: BDLocation?, sendKey: String, signDesc: String)
    }
}
