package net.zoneland.x.bpm.mobile.v1.zoneXBPM

import android.app.Activity
import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.O2AppUpdateBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.O2AppUpdateBeanData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*


class O2AppUpdateManager private constructor() {

    companion object {
        private var INSTANCE: O2AppUpdateManager? = null
        fun instance(): O2AppUpdateManager {
            if (INSTANCE == null) {
                INSTANCE = O2AppUpdateManager()
            }
            return INSTANCE!!
        }
    }

    //更新的json地址
//    private val o2AppVersionJsonUrl = "https://sample.o2oa.net/app/app.json"
    private val o2AppVersionJsonUrl = "https://app.o2oa.net/download/app.json"
    private val client = OkHttpClient()



    fun checkUpdate(activity: Activity, call: O2AppUpdateCallback) {
        val ranStr = getRandomStringOfLength(6)
        Observable.just("$o2AppVersionJsonUrl?$ranStr").subscribeOn(Schedulers.io())
                .map { url ->
                    XLog.debug(url)
                    val request = Request.Builder().url(url).build()
                    try {
                        val response = client.newCall(request).execute()
                        val json = response.body()?.string()
                        XLog.debug("json: $json")
                        if (json != null && !TextUtils.isEmpty(json)) {
                            val data = O2SDKManager.instance().gson.fromJson(json, O2AppUpdateBeanData::class.java)
                            if (data?.android != null) {
                                data.android
                            }else {
                                throw Exception("Json解析出错，请检查版本更新的json格式！")
                            }
                        }else {
                            throw Exception("没有获取到版本更新的json文件内容！")
                        }
                    }catch (e: Exception) {
                        XLog.error("", e)
                        throw e
                    }
                }.observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext {
                        val vcode = AndroidUtils.getAppVersionCode(activity)
                        try {
                            XLog.debug("vcode: $vcode , build:${it?.buildNo}")
                            if (it != null && it.buildNo.toInt() > vcode) {
                                call.onUpdate(it)
                            }else {
                                call.onNoneUpdate("没有新版本！")
                            }
                        } catch (e: Exception) {
                            call.onNoneUpdate(e.message ?: "")
                        }
                    }
                    onError { e, _ ->
                        XLog.error("", e)
                        call.onNoneUpdate(e?.message ?: "")
                    }
                }
    }


    private val characters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

    fun getRandomStringOfLength(len: Int): String {
        if (len < 0) {
            throw RuntimeException("len 不能小于 1")
        }
        var ret = ""
        val r = Random()
        for (x in 0..len ) {
            val index = r.nextInt(characters.size)
            val rc = characters[index]
            ret += rc.toString()
        }
        return ret
    }


}

interface O2AppUpdateCallback {
    fun onUpdate(version: O2AppUpdateBean)
    fun onNoneUpdate(error: String)
}