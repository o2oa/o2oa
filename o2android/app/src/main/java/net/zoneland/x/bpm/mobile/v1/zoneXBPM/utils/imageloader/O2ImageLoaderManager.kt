package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader

import android.view.View

/**
 * Created by fancy on 2017/7/13.
 * Copyright © 2017 O2. All rights reserved.
 */

class O2ImageLoaderManager private constructor() {

    companion object {
        private var INSTANCE: O2ImageLoaderManager? =null

        fun instance() : O2ImageLoaderManager {
            if (INSTANCE == null) {
                INSTANCE = O2ImageLoaderManager()
            }
            return INSTANCE!!
        }
    }

    var imageLoader: O2ImageLoaderStrategy = O2ImageLoaderStrategyWithGlide()

    fun showImage(v: View, url: String, options: O2ImageLoaderOptions? = null) {
        imageLoader.showImage(v, url, options)
    }

    fun showImage(v: View, drawable: Int, options: O2ImageLoaderOptions? = null) {
        imageLoader.showImage(v, drawable, options)
    }

}