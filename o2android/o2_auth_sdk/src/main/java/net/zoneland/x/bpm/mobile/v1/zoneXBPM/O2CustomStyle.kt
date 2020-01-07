package net.zoneland.x.bpm.mobile.v1.zoneXBPM

import android.content.Context
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil
import java.io.File
import com.bumptech.glide.Glide
import android.os.Looper



/**
 * 服务器自定义图片
 * Created by fancyLou on 16/04/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


object O2CustomStyle {

    const val CUSTOM_STYLE_JSON_KEY = "customStyleJsonKey"
    //MARK - 自定义信息 更新hash
    const val CUSTOM_STYLE_UPDATE_HASH_KEY = "customStyleUpdateHashKey"

    //MARK - 移动端首页展现是默认的native 还是配置的portal门户页面
    const val INDEX_TYPE_DEFAULT = "default"
    const val INDEX_TYPE_PORTAL = "portal"

    //MARK - 移动端首页展现类型的key
    const val INDEX_TYPE_PREF_KEY = "customStyleIndexTypeKey"
    const val INDEX_ID_PREF_KEY = "customStyleIndexIdKey"

    const val extension_png = ".png"

    //MARK - 缓存图片key
    const val IMAGE_KEY_LAUNCH_LOGO = "launch_logo" //启动页logo图  关于页面用的也是这个图 195px    65dp
    //首页底部菜单home按钮  114px   38dp
    const val IMAGE_KEY_INDEX_BOTTOM_MENU_LOGO_FOCUS = "index_bottom_menu_logo_focus"
    const val IMAGE_KEY_INDEX_BOTTOM_MENU_LOGO_BLUR = "index_bottom_menu_logo_blur"

    const val IMAGE_KEY_LOGIN_AVATAR = "login_avatar" //登录页默认头像  225px  75dp

    const val IMAGE_KEY_PEOPLE_AVATAR_DEFAULT = "people_avatar_default" //人员默认头像  120px  40dp

    const val IMAGE_KEY_PROCESS_DEFAULT = "process_default"  //流程默认图标   90px  30dp

    const val IMAGE_KEY_SETUP_ABOUT_LOGO = "setup_about_logo" //设置页 关于按钮 logo    66px  22dp


    /**
     * 启动 logo图地址
     */
    fun launchLogoImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_LAUNCH_LOGO + extension_png
        } else {
            null
        }
    }

    /**
     * 首页底部Home focus 图地址
     */
    fun indexMenuLogoFocusImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_INDEX_BOTTOM_MENU_LOGO_FOCUS + extension_png
        } else {
            null
        }
    }

    /**
     * 首页底部Home blur 图地址
     */
    fun indexMenuLogoBlurImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_INDEX_BOTTOM_MENU_LOGO_BLUR + extension_png
        } else {
            null
        }
    }

    /**
     * 登录页头像 图地址
     */
    fun loginAvatarImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_LOGIN_AVATAR + extension_png
        } else {
            null
        }
    }

    /**
     * 人员头像默认 图地址
     */
    fun peopleAvatarImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_PEOPLE_AVATAR_DEFAULT + extension_png
        } else {
            null
        }
    }

    /**
     * 流程默认 图地址
     */
    fun processDefaultImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_PROCESS_DEFAULT + extension_png
        } else {
            null
        }
    }

    /**
     * 设置页关于logo 图地址
     */
    fun setupAboutImagePath(context: Context?): String? {
        return if (context != null) {
            FileUtil.appExternalImageDir(context)?.absolutePath + File.separator + IMAGE_KEY_SETUP_ABOUT_LOGO + extension_png
        } else {
            null
        }
    }


    // 切换更新服务器资源后需要清除缓存。。。。。。。。。

    /**
     * 清除图片磁盘缓存
     */
    fun clearImageDiskCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable {
                    Glide.get(context).clearDiskCache()
                }).start()
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 清除图片内存缓存
     */
    fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}