package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader

/**
 * Created by fancy on 2017/7/13.
 * Copyright © 2017 O2. All rights reserved.
 */

data class O2ImageLoaderOptions(
        var placeHolder: Int = -1,
        var errorDrawable: Int = -1,
        var isCrossFade:Boolean = false,
        var isSkipCache:Boolean = false,
        var imageReSize: O2ImageReSize? = null
)

data class O2ImageReSize(
        var reWidth: Int = 0,
        var reHeight: Int = 0
)