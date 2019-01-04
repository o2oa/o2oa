package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin

import android.view.View
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.data.BaseSkinAttr
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.O2ProgressButton

/**
 * Created by fancyLou on 2017/12/21.
 * Copyright © 2017 O2. All rights reserved.
 */


class O2ProgressButtonColorSkinAttr(attrName:String="", originResId:Int=0, resName:String=""):  BaseSkinAttr(attrName, originResId, resName) {
    override fun apply(view: View) {
        if (view is O2ProgressButton) {
            FancySkinManager.instance().getResourceManager()?.let { resource->
                view.changeColor(resource.getColor(originResId, resName))
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return O2ProgressButtonColorSkinAttr(attrName, originResId, resName)
    }
}