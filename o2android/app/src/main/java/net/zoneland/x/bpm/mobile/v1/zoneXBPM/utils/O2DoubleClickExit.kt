package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent



/**
 * 连续点击两次退出
 * 不kill 进程
 * Created by fancy on 2017/5/24.
 */
class O2DoubleClickExit(val activity: Activity) {

    var isOnKeyBacking: Boolean = false

    var mHandler:Handler = Handler(Looper.getMainLooper())

    val mBackToast = XToast.getXToast(activity)!!

    val onBackTimeRunnable: Runnable = Runnable{
        isOnKeyBacking = false
        mBackToast.cancel()
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return false
        }
        if (isOnKeyBacking) {
            mHandler.removeCallbacks(onBackTimeRunnable)
            mBackToast.cancel()
            //appExit()
            activity.finish()
            return true
        }else {
            isOnKeyBacking = true
            mBackToast.show()
            mHandler.postDelayed(onBackTimeRunnable, 2000)
            return true
        }
    }

    fun appExit() {
        isOnKeyBacking = false
        val i = Intent(Intent.ACTION_MAIN)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.addCategory(Intent.CATEGORY_HOME)
        activity.startActivity(i)
    }
}