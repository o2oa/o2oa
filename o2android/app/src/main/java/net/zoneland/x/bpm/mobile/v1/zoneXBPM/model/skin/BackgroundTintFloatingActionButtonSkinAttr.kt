package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin

import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.view.View
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.data.BaseSkinAttr
import org.jetbrains.anko.doFromSdk

/**
 * Created by fancyLou on 2017/11/15.
 * Copyright © 2017 O2. All rights reserved.
 */


class BackgroundTintFloatingActionButtonSkinAttr(attrName:String="", originResId:Int=0, resName:String=""):  BaseSkinAttr(attrName, originResId, resName) {
    override fun apply(view: View) {
        if (view is FloatingActionButton) {
            FancySkinManager.instance().getResourceManager()?.let { manager ->
                val color = manager.getColorStateList(originResId, resName)
                if (color!=null) {
                    doFromSdk(Build.VERSION_CODES.LOLLIPOP) {
                        view.setBackgroundTintList(color)
                    }
                }
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return BackgroundTintFloatingActionButtonSkinAttr(attrName, originResId, resName)
    }
}