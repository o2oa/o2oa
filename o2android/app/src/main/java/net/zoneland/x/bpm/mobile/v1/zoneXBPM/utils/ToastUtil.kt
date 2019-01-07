package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R


/**
 * 通知工具类
 * 可以自定义背景和字体
 * Created by FancyLou on 2015/12/11.
 */
class ToastUtil(context: Context) {

    /**
     * 获取Toast
     * @return
     */
    val toast: Toast

    init {
        toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(R.layout.toast, null)
        toast.view = view
    }


    /**
     * 设置Toast字体及背景颜色
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    fun setToastColor(messageColor: Int, backgroundColor: Int): ToastUtil {
        val view = toast.view
        if (view != null) {
            val message = view.findViewById<View>(R.id.tv_toast_message) as TextView
            view.setBackgroundColor(backgroundColor)
            message.setTextColor(messageColor)
        }
        return this
    }

    fun setToastBackgroundDrawable(messageColor: Int, background: Drawable): ToastUtil {
        val view = toast.view
        if (view != null) {
            val message = view.findViewById<View>(R.id.tv_toast_message) as TextView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = background
            } else {
                view.setBackgroundDrawable(background)
            }
            message.setTextColor(messageColor)
        }
        return this
    }

    /**
     * 设置Toast字体及背景
     * @param messageColor
     * @param background
     * @return
     */
    fun setToastBackground(messageColor: Int, background: Int): ToastUtil {
        val view = toast.view
        if (view != null) {
            val message = view.findViewById<View>(R.id.tv_toast_message) as TextView
            view.setBackgroundResource(background)
            message.setTextColor(messageColor)
        }
        return this
    }


    /**
     * 短时间显示Toast
     */
    fun Short(message: CharSequence): ToastUtil {
        toast.duration = Toast.LENGTH_SHORT
        val view = toast.view
        val textView = view.findViewById<View>(R.id.tv_toast_message) as TextView
        textView.text = message
        return this
    }

    /**
     * 短时间显示Toast
     */
    fun Short(message: Int): ToastUtil {
        toast.duration = Toast.LENGTH_SHORT
        val view = toast.view
        val textView = view.findViewById<View>(R.id.tv_toast_message) as TextView
        textView.setText(message)
        return this
    }

    /**
     * 长时间显示Toast
     */
    fun Long(message: CharSequence): ToastUtil {
        toast.duration = Toast.LENGTH_LONG
        val view = toast.view
        val textView = view.findViewById<View>(R.id.tv_toast_message) as TextView
        textView.text = message
        return this
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    fun Long(message: Int): ToastUtil {
        toast.duration = Toast.LENGTH_LONG
        val view = toast.view
        val textView = view.findViewById<View>(R.id.tv_toast_message) as TextView
        textView.setText(message)
        return this
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    fun Indefinite(message: CharSequence, duration: Int): ToastUtil {
        toast.duration = duration
        val view = toast.view
        val textView = view.findViewById<View>(R.id.tv_toast_message) as TextView
        textView.text = message
        return this
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    fun Indefinite(message: Int, duration: Int): ToastUtil {
        toast.duration = duration
        val view = toast.view
        val textView = view.findViewById<View>(R.id.tv_toast_message) as TextView
        textView.setText(message)
        return this
    }

    /**
     * 显示Toast
     * @return
     */
    fun show(): ToastUtil {
        toast.show()
        return this
    }
}
