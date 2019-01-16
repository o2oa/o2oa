package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.content.Context
import android.graphics.Color
import android.widget.Toast

import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * 自定义toast 工具类
 *
 * Created by FancyLou on 2016/8/18.
 */
object XToast {

    fun toastShort(context: Context, message: String) {
        ToastUtil(context).Short(message).setToastBackgroundDrawable(Color.WHITE,
                FancySkinManager.instance().getDrawable(context, R.drawable.toast_background)!!).show()
    }

    fun toastShort(context: Context, messageRes: Int) {
        ToastUtil(context).Short(messageRes).setToastBackgroundDrawable(Color.WHITE,
                FancySkinManager.instance().getDrawable(context, R.drawable.toast_background)!!).show()
    }

    fun toastLong(context: Context, message: String) {
        ToastUtil(context).Long(message).setToastBackgroundDrawable(Color.WHITE,
                FancySkinManager.instance().getDrawable(context, R.drawable.toast_background)!!).show()
    }

    fun toastLong(context: Context, messageRes: Int) {
        ToastUtil(context).Long(messageRes).setToastBackgroundDrawable(Color.WHITE,
                FancySkinManager.instance().getDrawable(context, R.drawable.toast_background)!!).show()
    }

    fun getXToast(context: Context): Toast {
        return ToastUtil(context).Short(R.string.exit_click_double_back_btn).setToastBackgroundDrawable(Color.WHITE,
                FancySkinManager.instance().getDrawable(context, R.drawable.toast_background)!!).toast
    }
}
