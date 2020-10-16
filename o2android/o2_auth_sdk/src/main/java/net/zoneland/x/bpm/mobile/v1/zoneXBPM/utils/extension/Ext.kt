package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension

import android.app.Activity
import android.content.SharedPreferences
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.high_order_func.KTXDrawerListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.high_order_func._OnPageChangeListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.high_order_func._OnSubscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.security.SecurityEditor
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.security.SecuritySharedPreference
import rx.Observable

/**
 * Created by fancy on 2017/5/2.
 */

/**
 * 长度转成文件大小名称
 */
fun Long.friendlyFileLength(): String {
    if (this < 1024) {
        return this.toString() + " B"
    } else {
        val kb: Long = this / 1024
        if (kb < 1024) {
            return kb.toString() + " KB"
        } else {
            val mb = kb / 1024
            if (mb < 1024) {
                return mb.toString() + " MB"
            } else {
                val gb = mb / 1024
                return gb.toString() + " GB"
            }
        }
    }
}

//inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
//    val editor = edit()
//    editor.func()
//    editor.apply()
//}

inline fun SecuritySharedPreference.edit(func: SecurityEditor.() -> Unit) {
    val editor  = edit()
    editor.func()
    editor.apply()
}

inline fun ViewPager.addOnPageChangeListener(func: _OnPageChangeListener.() -> Unit) {
    val listener = _OnPageChangeListener()
    listener.func()
    addOnPageChangeListener(listener)
}

inline fun DrawerLayout.addDrawerListener(func: KTXDrawerListener.() -> Unit) {
    val listener = KTXDrawerListener()
    listener.func()
    addDrawerListener(listener)
}

/**
 * 统一处理订阅
 */
inline fun <T> Observable<T>.o2Subscribe(func: _OnSubscribe<T>.() -> Unit) {
    val o2Subscribe = _OnSubscribe<T>()
    o2Subscribe.func()
    subscribe(o2Subscribe)
}


fun AppCompatActivity.replaceFragmentSafely(fragment: Fragment, tag: String, @IdRes containerViewId: Int,
                                            allowState: Boolean = false,
                                            isAddBackStack: Boolean = false,
                                            @AnimRes enterAnimation: Int = 0,
                                            @AnimRes exitAnimation: Int = 0,
                                            @AnimRes enterPopAnimation: Int = 0,
                                            @AnimRes exitPopAnimation: Int = 0) {
    val ft = supportFragmentManager.beginTransaction()
    ft.setCustomAnimations(enterAnimation, exitAnimation, enterPopAnimation, exitPopAnimation)
    if (isAddBackStack) {
        ft.addToBackStack(tag)
    }
    ft.replace(containerViewId, fragment, tag)
    if (!supportFragmentManager.isStateSaved) {// isStateSaved is from Android Support Library version 26.0.0
        ft.commit()
    } else if (allowState) {
        ft.commitAllowingStateLoss()
    }
}

fun <T : Fragment> AppCompatActivity.addFragmentSafely(fragment: T, tag: String, allowState: Boolean = false,
                                                                                                @IdRes containerViewId: Int,
                                                                                                @AnimRes enterAnimation: Int = 0,
                                                                                                @AnimRes exitAnimation: Int = 0,
                                                                                                @AnimRes enterPopAnimation: Int = 0,
                                                                                                @AnimRes exitPopAnimation: Int = 0): T {
    if (!exitsFragmentByTag(tag)) {
        val ft = supportFragmentManager.beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, enterPopAnimation, exitPopAnimation)
                .add(containerViewId, fragment, tag)
        if (!supportFragmentManager.isStateSaved) {
            ft.commit()
        } else if (allowState) {
            ft.commitAllowingStateLoss()
        }
        return fragment
    }

    return findFragmentByTag(tag) as T

}

fun AppCompatActivity.exitsFragmentByTag(tag: String): Boolean {
    return supportFragmentManager.findFragmentByTag(tag) != null
}

fun AppCompatActivity.findFragmentByTag(tag: String): Fragment? {
    return supportFragmentManager.findFragmentByTag(tag)
}

fun Activity.screenWidth(): Int {
    return resources.displayMetrics.widthPixels
}
fun Activity.screenHeight():Int {
    return resources.displayMetrics.heightPixels
}

