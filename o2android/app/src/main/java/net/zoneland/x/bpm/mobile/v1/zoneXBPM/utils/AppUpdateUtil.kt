package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationManagerCompat
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.DownloadAPKFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.DownloadAPKService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.PgyUpdateBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertIconEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.lang.ref.WeakReference

/**
 * Created by fancyLou on 22/03/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class AppUpdateUtil(activity: Activity) {


    private val weakReference: WeakReference<Activity> = WeakReference(activity)

    private var versionName = ""
    private var downloadUrl = ""

    fun checkAppUpdate(noUpdateIsNotify: Boolean = false, callbackContinue:((flag: Boolean)->Unit)? = null) {
        PgyUpdateManager.register(weakReference.get(), object : UpdateManagerListener() {
            override fun onUpdateAvailable(p0: String?) {
                XLog.debug("onUpdateAvailable $p0")
                val bean = O2SDKManager.instance().gson.fromJson(p0, PgyUpdateBean::class.java)
                val activity = weakReference.get()
                versionName = bean.data.versionName
                downloadUrl = bean.data.downloadURL
                XLog.info("versionName:$versionName, downloadUrl:$downloadUrl")
                if (bean != null && activity != null) {
                    val currentversionName = AndroidUtils.getAppVersionName(activity)
                    if (currentversionName != versionName) {
                        O2DialogSupport.openConfirmDialog(activity, bean.data.releaseNote, listener = { _ ->
                            XLog.info("notification is true..........")
                            callbackContinue?.invoke(true)
//                            toDownloadService(activity)
                        }, icon = O2AlertIconEnum.UPDATE, negativeListener = {_->
                            callbackContinue?.invoke(false)
                        })

                    } else {
                        callbackContinue?.invoke(false)
                        XLog.info("versionName is same , do not show dialog! versionName:$versionName ")
                    }
                }else {
                    callbackContinue?.invoke(false)
                }

            }

            override fun onNoUpdateAvailable() {
                XLog.info("没有发现新版本！")
                if (noUpdateIsNotify) {
                    val activity = weakReference.get()
                    if (activity != null) {
                        XToast.toastShort(activity, "没有发现新版本！")
                    }
                }
                callbackContinue?.invoke(false)
            }
        })
    }

    /**
     * 是否开启通知
     */
    private fun isNotificationEnabled(context: Context): Boolean {
        return try {
            val manager = NotificationManagerCompat.from(context)
            manager.areNotificationsEnabled()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun alertNotificationProcessBar(activity: Activity) {
        O2DialogSupport.openConfirmDialog(activity, activity.getString(R.string.permission_notification),
                listener = { _ ->
                    //去设置通知权限
                    AndroidUtils.gotoSettingApplication(activity)
                },
                negativeListener = { _ ->
                    //跳过 不开启通知显示进度条
                    toDownloadService(activity)
                })

    }

    private fun toDownloadService(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !activity.packageManager.canRequestPackageInstalls()) {// 8.0需要判断安装未知来源的权限
            O2DialogSupport.openAlertDialog(activity, "非常抱歉，'安装未知应用' 权限未开通， 马上去设置", { _->
                AndroidUtils.gotoSettingInstalls(activity)
            })
        } else {
            downloadServiceStart(activity)
//            callbackContinue?.invoke()
        }


    }

    fun downloadServiceStart(activity: Activity) {
        val intent = Intent(activity, DownloadAPKService::class.java)
        intent.action = activity.packageName + DownloadAPKService.DOWNLOAD_SERVICE_ACTION
        intent.putExtra(DownloadAPKService.VERSIN_NAME_EXTRA_NAME, versionName)
        intent.putExtra(DownloadAPKService.DOWNLOAD_URL_EXTRA_NAME, downloadUrl)
        activity.startService(intent)

    }
}