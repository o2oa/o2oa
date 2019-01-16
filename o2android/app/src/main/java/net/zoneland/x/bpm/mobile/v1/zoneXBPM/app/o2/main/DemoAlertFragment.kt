package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_demo_alert.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import org.jetbrains.anko.dip

/**
 * Created by fancyLou on 2018/7/19.
 * Copyright © 2018 O2. All rights reserved.
 */


class DemoAlertFragment: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.demoAlertDialogStyle) //NO_FRAME就是dialog无边框，0指的是默认系统Theme
    }

    override fun onStart() {
        super.onStart()
        //显示大小
        val window = dialog.window
//        val metrics = resources.displayMetrics
//        val width = (metrics.widthPixels * 0.98).toInt() //DialogSearch的宽
        window!!.setLayout(context.dip(315), context.dip(485))
        window.setGravity(Gravity.CENTER)
        window.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_demo_alert, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_demo_alert_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}