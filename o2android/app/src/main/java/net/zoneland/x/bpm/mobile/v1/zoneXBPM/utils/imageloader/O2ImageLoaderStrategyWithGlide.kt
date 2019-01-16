package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager

/**
 * Created by fancy on 2017/7/13.
 * Copyright © 2017 O2. All rights reserved.
 */

class O2ImageLoaderStrategyWithGlide : O2ImageLoaderStrategy {


    override fun showImage(v: View, url: String, options: O2ImageLoaderOptions?) {
        if (v is ImageView) {
            val glideUrl =  GlideUrl(url, LazyHeaders.Builder().addHeader("x-token") { O2SDKManager.instance().zToken }.build())
            val request = Glide.with(v.context).load(glideUrl)
            request.dontAnimate()//Glide bug 圆形头像有问题 必须使用dontAnimate
            if (options == null) {
                request.into(v)
            }else {
                if (options.placeHolder != -1) {
                    request.placeholder(options.placeHolder)
                }
                if (options.errorDrawable != -1) {
                    request.error(options.errorDrawable)
                }
                if (options.isCrossFade) {
                    request.crossFade()
                }
                if (options.isSkipCache) {
                    request.skipMemoryCache(true)
                    request.diskCacheStrategy(DiskCacheStrategy.NONE)
                }
                if (options.imageReSize != null) {
                    val size = options.imageReSize!!
                    request.override(size.reWidth, size.reHeight)
                }
                request.into(v)
            }

        }
    }


    override fun showImage(v: View, drawable: Int, options: O2ImageLoaderOptions?) {
         if (v is ImageView) {
             val request = Glide.with(v.context).load(drawable)
             if (options == null) {
                 request.into(v)
             }else {
                 if (options.placeHolder != -1) {
                     request.placeholder(options.placeHolder)
                 }
                 if (options.errorDrawable != -1) {
                     request.error(options.errorDrawable)
                 }
                 if (options.isCrossFade) {
                     request.crossFade()
                 }
                 if (options.isSkipCache) {
                     request.skipMemoryCache(true)
                     request.diskCacheStrategy(DiskCacheStrategy.NONE)
                 }
                 if (options.imageReSize != null) {
                     val size = options.imageReSize!!
                     request.override(size.reWidth, size.reHeight)
                 }
                 request.into(v)
             }
         }
    }


}