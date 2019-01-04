package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bigkoo.convenientbanner.holder.Holder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.HotPictureOutData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions

/**
 * Created by fancy on 2017/6/19.
 * Copyright © 2017 O2. All rights reserved.
 */

class ConvenientBannerImageHolderView : Holder<HotPictureOutData> {


    var imageView: ImageView? = null

    override fun UpdateUI(context: Context?, position: Int, data: HotPictureOutData?) {
        val url = APIAddressHelper.instance().getHotPictureUrl(data?.picId ?:"")
        O2ImageLoaderManager.instance().showImage(imageView!!, url, O2ImageLoaderOptions(placeHolder = R.mipmap.banner_default))
    }

    override fun createView(context: Context?): View? {
        if (imageView == null) {
            imageView = ImageView(context)
        }
        imageView?.scaleType = ImageView.ScaleType.FIT_XY
        imageView?.setImageResource(R.mipmap.banner_default)
        return imageView
    }
}