package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.custom

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.DialogFragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_custom_style_install.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancyLou on 16/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class CustomStyleFragment : DialogFragment(), CustomStyleFragmentContract.View {

    companion object {
        const val TAG = "CustomStyleFragment"
    }


    var mPresenter: CustomStyleFragmentPresenter = CustomStyleFragmentPresenter()
    val handler: Handler by lazy {
        Handler(Looper.getMainLooper()) { msg: Message? ->
            val process = msg?.arg1
            XLog.info("receive message process:${msg?.arg1}")
            progressBar_custom_style_install?.progress = process ?: 0
            if (process == 100) {
                closeSelf()
            }
            true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.customStyleDialogStyle) //NO_FRAME就是dialog无边框，0指的是默认系统Theme
        mPresenter.attachView(this)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog.window
//        val metrics = resources.displayMetrics
//        val width = (metrics.widthPixels * 0.98).toInt() //DialogSearch的宽
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window.setGravity(Gravity.TOP)
        window.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_custom_style_install, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.installCustomStyle(handler)
    }

    override fun installFinish() {
        O2CustomStyle.clearImageDiskCache(context)
        O2CustomStyle.clearImageMemoryCache(context)
        val message = handler.obtainMessage()
        message.arg1 = 100
        handler.sendMessage(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (activity is DialogInterface.OnDismissListener) {
            (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
        super.onDismiss(dialog)
    }

    private fun closeSelf() {
        dismissAllowingStateLoss()
    }

}