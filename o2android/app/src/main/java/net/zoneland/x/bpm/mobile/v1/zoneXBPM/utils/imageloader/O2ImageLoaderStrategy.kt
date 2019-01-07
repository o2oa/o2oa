package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader

import android.view.View

/**
 * Created by fancy on 2017/7/13.
 * Copyright © 2017 O2. All rights reserved.
 */

interface O2ImageLoaderStrategy {

    fun showImage(v: View, url:String, options: O2ImageLoaderOptions? = null)
    fun showImage(v: View, drawable: Int, options: O2ImageLoaderOptions? = null)

}