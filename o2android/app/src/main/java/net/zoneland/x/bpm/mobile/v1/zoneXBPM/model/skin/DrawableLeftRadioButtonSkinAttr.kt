package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin

import android.view.View
import android.widget.RadioButton
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.data.BaseSkinAttr
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancyLou on 2017/11/15.
 * Copyright © 2017 O2. All rights reserved.
 */

class DrawableLeftRadioButtonSkinAttr(attrName:String="", originResId:Int=0, resName:String=""):  BaseSkinAttr(attrName, originResId, resName) {
    override fun apply(view: View) {
        if (view is RadioButton) {
            FancySkinManager.instance().getResourceManager()?.let {
                XLog.info("originResId:$originResId, resName:$resName")
                val drawable = it.getDrawable(originResId, resName)
                if (drawable!=null) {
                    XLog.info("setCompoundDrawablesWithIntrinsicBounds。。。。。。。。。。。。。")
                    view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                }
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return DrawableLeftRadioButtonSkinAttr(attrName, originResId, resName)
    }
}