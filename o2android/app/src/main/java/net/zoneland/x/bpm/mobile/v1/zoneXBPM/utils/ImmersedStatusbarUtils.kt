package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.readystatesoftware.systembartint.SystemBarTintManager
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * 设置沉浸式状态栏
 * Created by fancy on 2017/3/20.
 */

object ImmersedStatusBarUtils {

    /**
     * 需要style 设置<item name="android:fitsSystemWindows">true</item>
     */
    fun setImmersedStatusBar(activity: Activity) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = FancySkinManager.instance().getColor(activity, R.color.z_color_primary)
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(window, true)
            val tintManager = SystemBarTintManager(activity)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setNavigationBarTintEnabled(true)
            // 自定义颜色
            tintManager.setTintColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
        }
    }

    @TargetApi(19)
    fun setTranslucentStatus(win: Window, on: Boolean) {
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }



    /**
     * 设置透明状态栏
     * 需要在contentView(resId)之前
     */
    fun setImmersedStatusBarAfterInitContentView(activity: Activity?) {
        if (activity == null) {
            return
        }
        var window = activity.window
        if (Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 给内容区域设置top padding 高度是状态栏的高度
     * 透明状态栏之后 内容会往上
     */
    fun setContentViewTopPaddingWithStatusBarHeight(context: Context, contentView:View?) {
        var result = getStatusBarHeight(context)
        contentView?.setPadding(0, result, 0, 0)
    }

    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier(
                "status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}