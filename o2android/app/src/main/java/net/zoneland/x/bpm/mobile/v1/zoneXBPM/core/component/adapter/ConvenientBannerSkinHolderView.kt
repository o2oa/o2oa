package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bigkoo.convenientbanner.holder.Holder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R

/**
 * Created by fancy on 2017/6/19.
 * Copyright © 2017 O2. All rights reserved.
 */

class ConvenientBannerSkinHolderView : Holder<Int> {


    var itemView: View? = null

    override fun UpdateUI(context: Context?, position: Int, data: Int?) {
        if (data!=null) {
            val image = itemView?.findViewById<ImageView>(R.id.image_item_skin_show_preview)
            image?.setImageResource(data)
        }
    }

    override fun createView(context: Context?): View? {
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_skin_show_preview, null)
        }
        return itemView
    }
}