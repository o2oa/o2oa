package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin

import android.support.design.widget.TabLayout
import android.view.View
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.data.BaseSkinAttr

/**
 * Created by fancyLou on 2017/11/15.
 * Copyright © 2017 O2. All rights reserved.
 */

class TabIndicatorColorTabLayoutSkinAttr(attrName:String="", originResId:Int=0, resName:String=""):  BaseSkinAttr(attrName, originResId, resName) {
    override fun apply(view: View) {
        if (view is TabLayout) {
            FancySkinManager.instance().getResourceManager()?.let {
                val color = it.getColor(originResId, resName)
                view.setSelectedTabIndicatorColor(color)
            }
        }
    }

    override fun copy(attrName: String, originResId: Int, resName: String): BaseSkinAttr {
        return TabIndicatorColorTabLayoutSkinAttr(attrName, originResId, resName)
    }
}