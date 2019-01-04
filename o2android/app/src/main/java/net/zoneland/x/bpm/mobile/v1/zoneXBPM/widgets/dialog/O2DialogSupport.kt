package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog

import android.content.Context
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * Created by fancy on 2017/9/14.
 * Copyright © 2017 O2. All rights reserved.
 */


object O2DialogSupport {


    /**
     * 自定义界面对话框
     */
    fun openCustomViewDialog(context: Context, title: String, customViewRes: Int,
                             listener: ((O2AlertDialogBuilder.O2Dialog) -> Unit)): O2AlertDialogBuilder.O2Dialog {
        return O2AlertDialogBuilder(context)
                .title(title)
                .customView(customViewRes)
                .positive(R.string.positive)
                .negative(R.string.cancel)
                .onPositiveListener(listener)
                .show()
    }
    fun openCustomViewDialog(context: Context, title: String, positiveText:String,
                             negativeText:String, customViewRes: Int,
                             listener:((O2AlertDialogBuilder.O2Dialog) -> Unit), negativeListener:((O2AlertDialogBuilder.O2Dialog) -> Unit)): O2AlertDialogBuilder.O2Dialog {
        return O2AlertDialogBuilder(context)
                .title(title)
                .customView(customViewRes)
                .positive(positiveText)
                .negative(negativeText)
                .onPositiveListener(listener)
                .onNegativeListener(negativeListener)
                .show()
    }

    /**
     * 确认dialog
     */
    fun openConfirmDialog(context: Context, content:String, listener: ((O2AlertDialogBuilder.O2Dialog) -> Unit),
                          icon: O2AlertIconEnum = O2AlertIconEnum.ALERT,
                          negativeListener: ((O2AlertDialogBuilder.O2Dialog) -> Unit) = { _->  }) {
        O2AlertDialogBuilder(context)
                .title(R.string.confirm)
                .icon(icon)
                .content(content)
                .positive(R.string.positive)
                .negative(R.string.cancel)
                .onPositiveListener(listener)
                .onNegativeListener(negativeListener)
                .show()
    }



    /**
     * 提示dialog
     */
    fun openAlertDialog(context: Context, content:String, listener: ((O2AlertDialogBuilder.O2Dialog) -> Unit) = {}, icon: O2AlertIconEnum = O2AlertIconEnum.ALERT) {
        O2AlertDialogBuilder(context)
                .icon(icon)
                .content(content)
                .positive(R.string.positive)
                .onPositiveListener(listener)
                .show()
    }
}