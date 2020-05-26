package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api

import android.text.TextUtils
import android.util.Log
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.adapter.rxjava.Result.response
import android.R.string
import retrofit2.adapter.rxjava.Result.response





/**
 * Created by fancy on 2017/6/5.
 */

class O2Interceptor : Interceptor {
    val TAG = "O2Interceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
//        Log.d(TAG,"\n")
//        Log.d(TAG,"----------Start----------------")
//        Log.d(TAG, "| $original")

        val originalHttpUrl = original.url()
        val url = originalHttpUrl.newBuilder().addQueryParameter("o", (Math.random()*100).toString()).build()
        val xToken = O2SDKManager.instance().zToken
        val requestBuilder = original.newBuilder()
        if (!TextUtils.isEmpty(xToken)) {
            requestBuilder.addHeader("x-token", xToken)
        }
//        Log.d(TAG, "url: $url")
        val request = requestBuilder.addHeader("x-client", O2.DEVICE_TYPE)
                .method(original.method(), original.body())
                .url(url).build()

        val response = chain.proceed(request)
        val mediaType = response.body()?.contentType()
        val content = response.body()?.string()
//        Log.d(TAG, "| Response:$content")
//        return chain.proceed(request)

        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content ?: ""))
                .build()
    }
}