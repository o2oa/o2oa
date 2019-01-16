package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.google.gson.reflect.TypeToken
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.APIDistributeTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ApplicationData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.Base64ImageUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.cache.DiskLruCacheHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.cache.LruCacheHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.ninerectangle.NineRectangle
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Future

/**
 * Created by fancy on 2017/6/13.
 * Copyright © 2017 O2. All rights reserved.
 */


class PictureLoaderService(val context: Context) {
    companion object {
        val MEMORY_CACHE_SIZE_LIMIT = (Runtime.getRuntime().maxMemory() / 3).toInt()
        val LOCAL_CACHE_SIZE_LIMIT = 100 * 1024 * 1024
    }

    val taskMap = HashMap<String, Future<Unit>>()

    init {
        LruCacheHelper.openCache(MEMORY_CACHE_SIZE_LIMIT)
        var appVersion = 1
        try {
            appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch(e: Exception) {
        }
        DiskLruCacheHelper.openCache(context, appVersion, LOCAL_CACHE_SIZE_LIMIT)
    }

    fun close() {
        taskMap.map { it.value.cancel(true) }
        DiskLruCacheHelper.closeCache()
        LruCacheHelper.closeCache()
    }

    fun loadProcessAppIcon(convertView:View?, appId:String?) {
        if (convertView==null || TextUtils.isEmpty(appId)){
            XLog.error("参数不全，无法获取应用图标！！！！！！！！！！")
            return
        }
        try {
            var succeeded = loadImageFromMemory(convertView, appId!!)
            if (succeeded) return
            val hasCache = DiskLruCacheHelper.hasCache(appId)
            if (hasCache) {
                loadImageFromDisk(convertView, appId)
            }else {
                loadApplicationIconFromNetwork(convertView, appId)
            }
        }catch (e:Exception){
            XLog.error( "loadProcessAppIcon error", e)
        }
    }



    fun loadGroupAvatar(parent: View?, tag: String, persons: List<String>) {
        if (parent ==null || TextUtils.isEmpty(tag) || persons.isEmpty()) {
            XLog.error("参数不全，无法生成群组头像！！！！！！！！！！")
            return
        }
        XLog.debug("group: $tag, persons: $persons")
        try {
            //内存读取
            var succeeded = loadImageFromMemory(parent, tag)
            if (succeeded) return
            val hasCache = DiskLruCacheHelper.hasCache(tag)
            if (hasCache) {
                //磁盘读取
                loadImageFromDisk(parent, tag)
            } else {
                if (persons.size < 1) {
                    XLog.error("组合头像的成员数量不正确")
                } else {
                    //没有缓存
                    generateNineRectAvatar(parent, tag, persons)
                }
            }
        } catch (e: Exception) {
            XLog.error("获取头像缓存失败", e)
        }
    }


    private fun loadImageFromMemory(parent: View, tag: String): Boolean {
        var bitmap = LruCacheHelper.load(tag)
        if (bitmap != null) {
            if (parent is ImageView) {
                parent.setImageBitmap(bitmap)
            } else {
                parent.post {
                    var view = parent.findViewWithTag<ImageView>(tag)
                    if (view != null) {
                        view.setImageBitmap(bitmap)
                    }
                }
            }
            return true
        }

        return false
    }


    private fun loadImageFromDisk(parent: View, tag: String) {
        taskMap.put(tag, doAsync {
            var bitmap: Bitmap? = null
            try {
                bitmap = DiskLruCacheHelper.load(tag)
                if (bitmap != null) {
                    putImageIntoMemoryCache(tag, bitmap)
                }
            } catch (e: Exception) {
            }
            uiThread {
                if (bitmap != null) {
                    if (parent is ImageView) {
                        parent.setImageBitmap(bitmap)
                    } else {
                        parent.post {
                            var view = parent.findViewWithTag<ImageView>(tag)
                            if (view != null ) {
                                view.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
                if (taskMap.containsKey(tag)) {
                    taskMap.remove(tag)
                }
            }
        })

    }


    private fun loadApplicationIconFromNetwork(convertView: View, appId: String) {
        taskMap.put(appId, doAsync {
            var bitmap:Bitmap? =null
            try {
                val input = httpLoadApplicationIcon(appId)
                if (input!=null) {
                    bitmap = BitmapFactory.decodeStream(input)
                    if (bitmap!=null) {
                        putImageIntoMemoryCache(appId, bitmap)
                        putImageIntoDiskCache(appId, bitmap)
                    }
                }
            }catch (e: Exception) {
                XLog.error("生成群组头像异常", e)
            }

            uiThread {
                if (bitmap!=null){
                    if (convertView is ImageView) {
                        convertView.setImageBitmap(bitmap)
                    } else {
                        convertView.post {
                            var view = convertView.findViewWithTag<ImageView>(appId)
                            if (view != null) {
                                view.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
                if (taskMap.containsKey(appId)){
                    taskMap.remove(appId)
                }
            }
        })
    }

    private fun httpLoadApplicationIcon(appId: String): InputStream? {
        val iconUrl = APIAddressHelper.instance().getAPIDistribute(APIDistributeTypeEnum.x_processplatform_assemble_surface)
                .plus("jaxrs/application/$appId/icon")
        try {
            XLog.debug("iconurl:$iconUrl")
            val url = URL(iconUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Accept-Charset", "UTF-8")
            connection.setRequestProperty("contentType", "UTF-8")
            connection.requestMethod = "GET"
            connection.readTimeout = 5000// 设置超时的时间
            connection.connectTimeout = 5000// 设置链接超时的时间
            val newCookie = "x-token:" + O2SDKManager.instance().zToken
            connection.setRequestProperty("Cookie", newCookie)
            connection.setRequestProperty("x-token", O2SDKManager.instance().zToken)
            // 获取响应的状态码 404 200 505 302
            val code = connection.responseCode
            if (code ==200) {
                var currentLine:String? = null
                var allLine = ""
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                if (reader==null){
                    XLog.debug("reader is null")
                }
                do {
                    currentLine  = reader.readLine()
                    if (!TextUtils.isEmpty(currentLine)) {
                        allLine = allLine.plus(currentLine).plus("\r\n")
                    }
                }while (currentLine != null)

                val response:ApiResponse<ApplicationData> = O2SDKManager.instance().gson.fromJson(allLine, object : TypeToken<ApiResponse<ApplicationData>>(){}.type)
                val icon = response.data.icon
                if (TextUtils.isEmpty(icon)) {
                    XLog.error( "ICON字段为空，不需要更新应用ICON！")
                    return null
                }
                return  Base64ImageUtil.generateBase642Inputstream(icon)
            }else {
                XLog.error( "error response code :"+code)
            }
        }catch (e:Exception) {
            XLog.error( "", e)
        }
        return null
    }

    private fun generateNineRectAvatar(parent: View, tag: String, persons: List<String>) {
        taskMap.put(tag, doAsync {
            var nineRectBitmap:Bitmap? = null
            try {
                val list = ArrayList<String>()
                if (persons.size > 9) {
                    list.addAll(persons.subList(0, 9))
                } else {
                    list.addAll(persons)
                }

                val bitList = ArrayList<Bitmap>()
                list.map {
                    val inputStream:InputStream? = httpLoadAvatar(it)
                    val avatarBitmap = BitmapFactory.decodeStream(inputStream) ?: BitmapFactory.decodeResource(context.resources, R.mipmap.contact_icon_avatar)
                    bitList.add(avatarBitmap)
                }
                nineRectBitmap = NineRectangle(context.dip(48f).toFloat()).getNineRectCombineBitmap(bitList)
                if (nineRectBitmap!=null) {
                    // 保存到缓存。
                    putImageIntoMemoryCache(tag, nineRectBitmap)
                    putImageIntoDiskCache(tag, nineRectBitmap)
                }

            } catch (e: Exception) {
                XLog.error("生成群组头像异常", e)
            }

            uiThread {
                if (nineRectBitmap != null) {
                    if (parent is ImageView) {
                        parent.setImageBitmap(nineRectBitmap)
                    } else {
                        parent.post {
                            var view = parent.findViewWithTag<ImageView>(tag)
                            if (view != null) {
                                view.setImageBitmap(nineRectBitmap)
                            }
                        }
                    }
                }
                if (taskMap.containsKey(tag)) {
                    taskMap.remove(tag)
                }
            }
        })

    }

    private fun httpLoadAvatar(name: String): InputStream? {
        var inputstream:InputStream? = null
        try {
            XLog.debug("load avatar : $name")
            val response = RetrofitClient.instance().assembleExpressApi().loadPersonAvatar(name).execute()
            inputstream = response.body()?.byteStream()
        }catch (e: Exception){XLog.error("获取头像失败", e)}
        return inputstream
    }

    private fun putImageIntoDiskCache(tag: String, bitmap: Bitmap?) {
        DiskLruCacheHelper.dump(bitmap, tag)
    }


    private fun putImageIntoMemoryCache(tag: String, bitmap: Bitmap) {
        LruCacheHelper.dump(tag, bitmap)
    }

}