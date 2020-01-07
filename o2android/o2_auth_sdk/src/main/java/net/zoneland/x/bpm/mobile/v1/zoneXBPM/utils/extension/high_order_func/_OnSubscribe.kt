package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.high_order_func

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.exception.O2ResponseException
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiErrorResponse
import retrofit2.adapter.rxjava.HttpException
import rx.Subscriber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

/**
 * Created by fancy on 2017/7/20.
 * Copyright © 2017 O2. All rights reserved.
 */

class _OnSubscribe<T> : Subscriber<T>() {
    private val TAG = "onSubscribe"

    private var _onCompleted: (() -> Unit)? = null
    private var _onNext: ((t: T) -> Unit)? = null
    private var _onError: ((e: Throwable?, isNetworkError: Boolean) -> Unit)? = null


    fun onCompleted(func: () -> Unit) {
        _onCompleted = func
    }

    override fun onCompleted() {
        _onCompleted?.invoke()
    }

    fun onNext(func: (t: T) -> Unit) {
        _onNext = func
    }

    override fun onNext(t: T) {
        _onNext?.invoke(t)
    }

    fun onError(func: (e: Throwable?, isNetworkError: Boolean) -> Unit) {
        _onError = func
    }

    override fun onError(e: Throwable?) {
        var isNetworkError = false
        var o2err: O2ResponseException? = null
        when (e) {
            is TimeoutException -> {
                isNetworkError = true
            }
            is SocketTimeoutException -> {
                isNetworkError = true
            }
            is ConnectException -> {
                isNetworkError = true
            }
            is HttpException ->{
                o2err = showO2ErrorMessage(e)
            }
            else -> Log.e(TAG, "", e)
        }

        if (o2err != null) {
            _onError?.invoke(o2err, isNetworkError)
        }else {
            _onError?.invoke(e, isNetworkError)
        }
    }

    private fun showO2ErrorMessage(exception: HttpException): O2ResponseException? {
        val buffer = StringBuffer()
        buffer.append("响应代码:" + exception.code())
        try {
            val json = exception.response().errorBody()?.string()
            Log.e(TAG, "http error body:$json")
            buffer.append(" 反馈信息:$json")
            if (json != null && "" != json) {
                val gson = Gson()
                val api = gson.fromJson(json, ApiErrorResponse::class.java)
                if (api != null) {
                    Log.e(TAG, buffer.toString())
                    return O2ResponseException(api.message)
                }
            }
        } catch (e1: Exception) {
            Log.e(TAG,"", e1)
        }
        Log.e(TAG, buffer.toString())
        return null
    }
}