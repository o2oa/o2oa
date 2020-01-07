package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api

import android.text.TextUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by fancy on 2017/6/5.
 */

class O2Interceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url()
        val url = originalHttpUrl.newBuilder().addQueryParameter("o", (Math.random()*100).toString()).build()
        val xToken = O2SDKManager.instance().zToken
        val requestBuilder = original.newBuilder()
        if (!TextUtils.isEmpty(xToken)) {
            requestBuilder.addHeader("x-token", xToken)
        }
//        XLog.debug("url: $url")
        val request = requestBuilder.addHeader("x-client", O2.DEVICE_TYPE)
                .method(original.method(), original.body())
                .url(url).build()
        return chain.proceed(request)
    }
}