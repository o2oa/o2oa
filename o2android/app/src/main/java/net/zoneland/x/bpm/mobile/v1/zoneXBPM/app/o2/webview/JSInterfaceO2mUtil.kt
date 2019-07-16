package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.util.DisplayMetrics
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wugang.activityresult.library.ActivityResult
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.view.BBSWebViewSubjectActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view.CMSWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2JsPhoneInfoResponse

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2JsPostMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2UtilDatePickerMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2UtilNavigationMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.zxing.activity.CaptureActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialogfragment.CalendarDateTimePickerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialogfragment.DateTimePickerFragment
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by fancyLou on 2019-05-05.
 * Copyright © 2019 O2. All rights reserved.
 */

class JSInterfaceO2mUtil private constructor(val activity: FragmentActivity?) {
    companion object {
        const val JSInterfaceName = "o2mUtil"
        fun with(activity: FragmentActivity) = JSInterfaceO2mUtil(activity)
        fun with(fragment: Fragment) = JSInterfaceO2mUtil(fragment.activity)
    }

    private lateinit var webView: WebView
    private val gson: Gson by lazy { Gson() }

    fun setupWebView(webView: WebView) {
        this.webView = webView
    }

    @JavascriptInterface
    fun postMessage(message: String?) {
        if (!TextUtils.isEmpty(message)) {
            XLog.debug(message)
            val json = JSONTokener(message).nextValue()
            if (json is JSONObject) {
                val type = json.getString("type")
                when (type) {
                    "date.datePicker" -> datePicker(message!!)
                    "date.timePicker" -> timePicker(message!!)
                    "date.dateTimePicker" -> dateTimePicker(message!!)
                    "calendar.chooseOneDay" -> calendarDatePicker(message!!)
                    "calendar.chooseDateTime" -> calendarDateTimePicker(message!!)
                    "calendar.chooseInterval" -> calendarDateIntervalPicker(message!!)
                    "device.getPhoneInfo" -> deviceGetPhoneInfo(message!!)
                    "device.scan" -> deviceScan(message!!)
                    "navigation.setTitle" -> navigationSetTitle(message!!)
                    "navigation.close" -> navigationClose(message!!)
                    "navigation.goBack" -> navigationGoBack(message!!)
                }
            } else {
                XLog.error("message 格式错误！！！")
            }
        } else {
            XLog.error("o2mUtil.postMessage error, 没有传入message内容！")
        }
    }

    private fun callbackJs(js: String) {
        if (::webView.isInitialized && !TextUtils.isEmpty(js)) {
            activity?.runOnUiThread {
                XLog.debug("执行js：$js")
                webView.evaluateJavascript(js) { value ->
                    XLog.debug("js执行完成, result:$value")
                }
            }
        } else {
            XLog.error("没有注入webView，无法执行回调函数！！！！")
        }
    }


    private fun showPicker(default: String, pickerType: String, callback: (String) -> Unit) {
        if (activity != null) {
            val dialog = DateTimePickerFragment()
            val arg = Bundle()
            arg.putString(DateTimePickerFragment.PICKER_TYPE, pickerType)
            arg.putString(DateTimePickerFragment.DEFAULT_TIME, default)
            dialog.arguments = arg
            dialog.setListener(object : DateTimePickerFragment.OnDateTimeSetListener {
                override fun onSet(time: String, pickerType: String) {
                    callback(time)
                }
            })
            dialog.show(activity.supportFragmentManager, pickerType)
        } else {
            XLog.error("activity不存在，无法打开dialog！！")
        }
    }


    private fun datePicker(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilDatePickerMessage>>() {}.type
        val pickerValue: O2JsPostMessage<O2UtilDatePickerMessage> = gson.fromJson(message, type)
        val callback = pickerValue.callback
        var defaultValue = pickerValue.data?.value
        if (TextUtils.isEmpty(defaultValue)) {
            defaultValue = DateHelper.nowByFormate("yyyy-MM-dd")
        }
        showPicker(defaultValue!!, DateTimePickerFragment.DATEPICKER_TYPE) { result ->
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback('{\"value\":\"$result\"}')")
            }
        }
    }

    private fun timePicker(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilDatePickerMessage>>() {}.type
        val pickerValue: O2JsPostMessage<O2UtilDatePickerMessage> = gson.fromJson(message, type)
        val callback = pickerValue.callback
        var defaultValue = pickerValue.data?.value
        if (TextUtils.isEmpty(defaultValue)) {
            defaultValue = DateHelper.nowByFormate("HH:ss")
        }
        showPicker(defaultValue!!, DateTimePickerFragment.TIMEPICKER_TYPE) { result ->
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback('{\"value\":\"$result\"}')")
            }
        }
    }

    private fun dateTimePicker(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilDatePickerMessage>>() {}.type
        val pickerValue: O2JsPostMessage<O2UtilDatePickerMessage> = gson.fromJson(message, type)
        val callback = pickerValue.callback
        var defaultValue = pickerValue.data?.value
        if (TextUtils.isEmpty(defaultValue)) {
            defaultValue = DateHelper.nowByFormate("yyyy-MM-dd HH:ss")
        }
        showPicker(defaultValue!!, DateTimePickerFragment.DATETIMEPICKER_TYPE) { result ->
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback('{\"value\":\"$result\"}')")
            }
        }
    }

    private fun calendarDatePicker(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilDatePickerMessage>>() {}.type
        val pickerValue: O2JsPostMessage<O2UtilDatePickerMessage> = gson.fromJson(message, type)
        val callback = pickerValue.callback
        var defaultValue = pickerValue.data?.value
        if (TextUtils.isEmpty(defaultValue)) {
            defaultValue = DateHelper.nowByFormate("yyyy-MM-dd")
        }
        if (activity != null) {
            activity.runOnUiThread {
                val dialog = CalendarDateTimePickerFragment()
                val arg = Bundle()
                arg.putString(CalendarDateTimePickerFragment.PICKER_TYPE_KEY, CalendarDateTimePickerFragment.DATE_PICKER_TYPE)
                arg.putString(CalendarDateTimePickerFragment.DEFAULT_VALUE_KEY, defaultValue)
                dialog.arguments = arg
                dialog.setOnDateTimeSetListener(object : CalendarDateTimePickerFragment.OnDateTimeSetListener {
                    override fun onSet(time: String) {
                        if (!TextUtils.isEmpty(callback)) {
                            callbackJs("$callback('{\"value\":\"$time\"}')")
                        }
                    }

                    override fun onSetInterval(startDate: String, endDate: String) {
                    }

                })
                dialog.show(activity.supportFragmentManager, "calendarDatePicker")
            }
        } else {
            XLog.error("activity不存在，无法打开dialog！！")
        }
    }

    private fun calendarDateTimePicker(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilDatePickerMessage>>() {}.type
        val pickerValue: O2JsPostMessage<O2UtilDatePickerMessage> = gson.fromJson(message, type)
        val callback = pickerValue.callback
        var defaultValue = pickerValue.data?.value
        if (TextUtils.isEmpty(defaultValue)) {
            defaultValue = DateHelper.nowByFormate("yyyy-MM-dd HH:ss")
        }
        if (activity != null) {
            activity.runOnUiThread {
                val dialog = CalendarDateTimePickerFragment()
                val arg = Bundle()
                arg.putString(CalendarDateTimePickerFragment.PICKER_TYPE_KEY, CalendarDateTimePickerFragment.DATE_TIME_PICKER_TYPE)
                arg.putString(CalendarDateTimePickerFragment.DEFAULT_VALUE_KEY, defaultValue)
                dialog.arguments = arg
                dialog.setOnDateTimeSetListener(object : CalendarDateTimePickerFragment.OnDateTimeSetListener {
                    override fun onSet(time: String) {
                        if (!TextUtils.isEmpty(callback)) {
                            callbackJs("$callback('{\"value\":\"$time\"}')")
                        }
                    }

                    override fun onSetInterval(startDate: String, endDate: String) {
                    }

                })
                dialog.show(activity.supportFragmentManager, "calendarDateTimePicker")
            }
        } else {
            XLog.error("activity不存在，无法打开dialog！！")
        }
    }

    private fun calendarDateIntervalPicker(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilDatePickerMessage>>() {}.type
        val pickerValue: O2JsPostMessage<O2UtilDatePickerMessage> = gson.fromJson(message, type)
        val callback = pickerValue.callback
        var startValue = pickerValue.data?.startDate
        var endValue = pickerValue.data?.startDate
        if (TextUtils.isEmpty(startValue)) {
            startValue = DateHelper.nowByFormate("yyyy-MM-dd")
        }
        if (TextUtils.isEmpty(endValue)) {
            endValue = DateHelper.nowByFormate("yyyy-MM-dd")
        }
        if (activity != null) {
            activity.runOnUiThread {
                val dialog = CalendarDateTimePickerFragment()
                val arg = Bundle()
                arg.putString(CalendarDateTimePickerFragment.PICKER_TYPE_KEY, CalendarDateTimePickerFragment.DATEINTERVAL_PICKER_TYPE)
                arg.putString(CalendarDateTimePickerFragment.DEFAULT_START_VALUE_KEY, startValue)
                arg.putString(CalendarDateTimePickerFragment.DEFAULT_END_VALUE_KEY, endValue)
                dialog.arguments = arg
                dialog.setOnDateTimeSetListener(object : CalendarDateTimePickerFragment.OnDateTimeSetListener {
                    override fun onSet(time: String) {
                    }

                    override fun onSetInterval(startDate: String, endDate: String) {
                        if (!TextUtils.isEmpty(callback)) {
                            callbackJs("$callback('{\"startDate\":\"$startDate\", \"endDate\":\"$endDate\"}')")
                        }
                    }

                })
                dialog.show(activity.supportFragmentManager, "calendarDateTimePicker")
            }

        } else {
            XLog.error("activity不存在，无法打开dialog！！")
        }
    }


    private fun navigationClose(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilNavigationMessage>>() {}.type
        val value: O2JsPostMessage<O2UtilNavigationMessage> = gson.fromJson(message, type)
        val callback = value.callback
        if (activity != null) {
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback('{}')")
            }
            activity.runOnUiThread {
                activity.finish()
            }
        } else {
            XLog.error("activity不存在 navigationClose失败 ！！")
        }
    }

    private fun navigationGoBack(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilNavigationMessage>>() {}.type
        val value: O2JsPostMessage<O2UtilNavigationMessage> = gson.fromJson(message, type)
        val callback = value.callback
        if (activity != null) {
            activity.runOnUiThread {
                if (::webView.isInitialized && webView.canGoBack()) {
                    webView.goBack()
                    if (!TextUtils.isEmpty(callback)) {
                        callbackJs("$callback('{}')")
                    }
                } else {
                    if (!TextUtils.isEmpty(callback)) {
                        callbackJs("$callback('{}')")
                    }
                    activity.finish()
                }
            }
        } else {
            XLog.error("activity不存在 navigationGoBack ！！")
        }
    }

    private fun navigationSetTitle(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilNavigationMessage>>() {}.type
        val value: O2JsPostMessage<O2UtilNavigationMessage> = gson.fromJson(message, type)
        val title = value.data?.title ?: ""
        val callback = value.callback
        if (activity != null) {
            activity.runOnUiThread {
                if (activity is TaskWebViewActivity) {//比较麻烦 因为自定义了ActionBar。。。
                    activity.updateToolbarTitle(title)
                }else if (activity is PortalWebViewActivity) {
                    activity.updateToolbarTitle(title)
                }else if (activity is CMSWebViewActivity) {
                    activity.updateToolbarTitle(title)
                }else if (activity is BBSWebViewSubjectActivity) {
                    activity.updateToolbarTitle(title)
                }
            }
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback('{}')")
            }
        } else {
            XLog.error("activity不存在 navigationClose失败 ！！")
        }
    }

    private fun deviceScan(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilNavigationMessage>>() {}.type
        val value: O2JsPostMessage<O2UtilNavigationMessage> = gson.fromJson(message, type)
        val callback = value.callback
        if (activity != null) {
            activity.runOnUiThread {
                PermissionRequester(activity)
                        .request(Manifest.permission.CAMERA)
                        .o2Subscribe {
                            onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                                XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                                if (!granted) {
                                    O2DialogSupport.openAlertDialog(activity, "需要摄像头权限才能进行扫一扫功能！")
                                } else {
                                    ActivityResult.of(activity)
                                            .className(CaptureActivity::class.java)
                                            .params(CaptureActivity.BACK_SCAN_RESULT_KEY, true)
                                            .greenChannel()
                                            .forResult { _, data ->
                                                val result = data?.getStringExtra(CaptureActivity.SCAN_RESULT_KEY)
                                                        ?: ""
                                                if (!TextUtils.isEmpty(callback)) {
                                                    callbackJs("$callback('{\"text\":\"$result\"}')")
                                                }
                                            }
                                }
                            }
                        }
            }
        } else {
            XLog.error("activity不存在 deviceScan 失败！！")
        }
    }

    private fun deviceGetPhoneInfo(message: String) {
        val type = object : TypeToken<O2JsPostMessage<O2UtilNavigationMessage>>() {}.type
        val value: O2JsPostMessage<O2UtilNavigationMessage> = gson.fromJson(message, type)
        val callback = value.callback
        if (activity != null) {
            val res = O2JsPhoneInfoResponse()
            res.brand = AndroidUtils.getDeviceBrand()
            res.model = AndroidUtils.getDeviceModelNumber()
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val width = dm.widthPixels
            val height = dm.heightPixels
            res.screenHeight = "$height"
            res.screenWidth = "$width"
            res.version = AndroidUtils.getDeviceOsVersion()
            res.operatorType = AndroidUtils.getCarrier(activity)
            res.netInfo = AndroidUtils.getAPNType(activity)
            if (!TextUtils.isEmpty(callback)) {
                val json = gson.toJson(res)
                callbackJs("$callback('$json')")
            }
        }else {
            XLog.error("activity不存在 deviceGetPhoneInfo失败 ！！")
        }
    }




}