package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.app.Activity
import android.app.Service
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.BottomSheetMenu
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog
import org.json.JSONObject
import org.json.JSONTokener


/**
 * Created by fancyLou on 2019-04-28.
 * Copyright © 2019 O2. All rights reserved.
 */


class JSInterfaceO2mNotification  private constructor (val activity: Activity?) {

    companion object {
        const val JSInterfaceName = "o2mNotification"
        fun with(activity: Activity) = JSInterfaceO2mNotification(activity)
        fun with(fragment: Fragment) = JSInterfaceO2mNotification(fragment.activity)
    }
    private var loading: LoadingDialog? = null
    private val gson: Gson by lazy { Gson() }
    private lateinit var webView: WebView

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
                when(type) {
                    "alert" -> alert(message!!)
                    "confirm" -> confirm(message!!)
                    "prompt" -> prompt(message!!)
                    "vibrate" -> vibrate(message!!)
                    "toast" -> toast(message!!)
                    "actionSheet" -> actionSheet(message!!)
                    "showLoading" -> showLoading(message!!)
                    "hideLoading" -> hideLoading(message!!)
                }
            }else {
                XLog.error("message 格式错误！！！")
            }
        }else {
            XLog.error("o2mNotification.postMessage error, 没有传入message内容！")
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
        }else {
            XLog.error("没有注入webView，无法执行回调函数！！！！")
        }
    }

    private fun alert(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationAlertMessage>>() {}.type
        val alert: O2NotificationMessage<O2NotificationAlertMessage> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val content = alert.data?.message ?: ""
            val title = alert.data?.title ?: ""
            val buttonName = alert.data?.buttonName ?: "确定"
            activity.runOnUiThread {
                MaterialDialog.Builder(activity)
                        .title(title)
                        .content(content)
                        .positiveText(buttonName)
                        .onPositive { _, _ ->
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback()")
                            }
                        }
                        .show()
            }

        }else{
            XLog.error("activity不存在，无法打开dialog！！")
        }

    }
    private fun confirm(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationConfirm>>() {}.type
        val alert: O2NotificationMessage<O2NotificationConfirm> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val content = alert.data?.message ?: ""
            val title = alert.data?.title ?: ""
            var buttonLabels = alert.data?.buttonLabels
            if (buttonLabels==null || buttonLabels.isEmpty()) {
                buttonLabels = ArrayList<String>()
                buttonLabels[0] = "确定"
                buttonLabels[1] = "取消"
            }
            if (buttonLabels.size != 2) {
                XLog.error("按钮个数不等于2。。。。。")
                return
            }
            activity.runOnUiThread {
                MaterialDialog.Builder(activity)
                        .title(title)
                        .content(content)
                        .positiveText(buttonLabels[0])
                        .negativeText(buttonLabels[1])
                        .onPositive { _, _ ->
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback(0)")
                            }
                        }
                        .onNegative { _, _ ->
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback(1)")
                            }
                        }
                        .show()
            }

        }else{
            XLog.error("activity不存在，无法打开dialog！！")
        }

    }
    private fun prompt(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationConfirm>>() {}.type
        val alert: O2NotificationMessage<O2NotificationConfirm> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val content = alert.data?.message ?: ""
            val title = alert.data?.title ?: ""
            var buttonLabels = alert.data?.buttonLabels
            if (buttonLabels==null || buttonLabels.isEmpty()) {
                buttonLabels = ArrayList<String>()
                buttonLabels[0] = "确定"
                buttonLabels[1] = "取消"
            }
            if (buttonLabels.size != 2) {
                XLog.error("按钮个数不等于2。。。。。")
                return
            }

            activity.runOnUiThread {
                val mDialog = MaterialDialog.Builder(activity)
                        .title(title)
                        .customView(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.layout.dialog_prompt, true)
                        .positiveText(buttonLabels[0])
                        .negativeText(buttonLabels[1])
                        .onPositive { dialog, _ ->
                            val input = dialog.findViewById(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.id.dialog_prompt_input) as EditText
                            val json = "{buttonIndex: 0, value: \"${input.text}\"}"
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback('$json')")
                            }
                        }
                        .onNegative { dialog, _ ->
                            val input = dialog.findViewById(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.id.dialog_prompt_input) as EditText
                            val json = "{buttonIndex: 1, value: \"${input.text}\"}"
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback('$json')")
                            }
                        }
                        .show()
                val messageTv = mDialog.findViewById(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.id.dialog_prompt_message) as TextView
                messageTv.text = content
            }

        }else{
            XLog.error("activity不存在，无法打开dialog！！")
        }
    }
    private fun vibrate(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationToast>>() {}.type
        val alert: O2NotificationMessage<O2NotificationToast> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val duration = alert.data?.duration ?: 300
            val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            vib.vibrate(duration.toLong())
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback()")
            }
        }else{
            XLog.error("activity不存在，无法震动！！")
        }
    }
    private fun toast(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationToast>>() {}.type
        val alert: O2NotificationMessage<O2NotificationToast> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val text = alert.data?.message ?: ""
            XToast.toastShort(activity, text)
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback()")
            }
        }else{
            XLog.error("activity不存在，无法打开toast！！")
        }
    }
    private fun actionSheet(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationActionSheet>>() {}.type
        val alert: O2NotificationMessage<O2NotificationActionSheet> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val title = alert.data?.title ?: ""
            val cancelBtn = alert.data?.cancelButton ?: "取消"
            val buttons = alert.data?.otherButtons
            if (buttons == null || buttons.isEmpty()) {
                XLog.error("按钮列表为空！！！")
                return
            }
            activity.runOnUiThread {
                BottomSheetMenu(activity)
                        .setTitle(title)
                        .setItems(buttons, activity.resources.getColor(R.color.z_color_text_primary)) {
                            index ->
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback($index)")
                            }
                        }
                        .setCancelButton(cancelBtn, activity.resources.getColor(R.color.z_color_text_hint)) {
                            XLog.debug("取消。。。。。")
                            if (!TextUtils.isEmpty(callback)) {
                                callbackJs("$callback(-1)")
                            }
                        }
                        .show()
            }
        }else{
            XLog.error("activity不存在，无法打开Dialog！！")
        }
    }
    private fun showLoading(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationLoading>>() {}.type
        val alert: O2NotificationMessage<O2NotificationLoading> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            val text = alert.data?.text ?: ""
            activity.runOnUiThread {
                loading = LoadingDialog(activity, text)
                loading?.show()
            }
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback()")
            }
        }
    }

    private fun hideLoading(message: String) {
        val type = object : TypeToken<O2NotificationMessage<O2NotificationLoading>>() {}.type
        val alert: O2NotificationMessage<O2NotificationLoading> = gson.fromJson(message, type)
        if (activity != null) {
            val callback = alert.callback
            loading?.dismiss()
            if (!TextUtils.isEmpty(callback)) {
                callbackJs("$callback()")
            }
        }
    }

}