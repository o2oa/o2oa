package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin

import android.graphics.drawable.BitmapDrawable
import android.view.View
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.data.BaseSkinAttr
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.ChangeColorIconWithText

/**
 * 皮肤插件
 *   ChangeColorIconWithText中的iconCompleted属性
 * Created by fancyLou on 2017/11/14.
 * Copyright © 2017 O2. All rights reserved.
 */

class IconCompletedChangeColorIconSkinAttr(attrName:String="", originResId:Int=0, resName:String=""):  BaseSkinAttr(attrName, originResId, resName) {
    override fun apply(view: View) {
        if (view is ChangeColorIconWithText) {
            FancySkinManager.instance().getResourceManager()?.let { manager ->
                val drawable = manager.getDrawable(originResId, resName)
                if (drawable is BitmapDrawable) {
                    if (drawable.bitmap!=null) {
                        view.setMIconCompletedBitmap(drawable.bitmap)
                    }
                }
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return IconCompletedChangeColorIconSkinAttr(attrName, originResId, resName)
    }
}