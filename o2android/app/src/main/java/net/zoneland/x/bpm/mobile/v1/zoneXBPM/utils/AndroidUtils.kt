package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.text.format.Formatter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.BuildConfig
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


/**
 * Created by fancyLou on 2017/12/8.
 * Copyright © 2017 O2. All rights reserved.
 */

object AndroidUtils {

    /**
     * 品牌 HONOR
     */
    fun getDeviceBrand(): String {
        return Build.BRAND
    }

    /**
     * 制造商 HUAWEI
     */
    fun getDeviceManufacturer(): String {
        return Build.MANUFACTURER
    }

    /**
     * 手机型号 KNT-AL20
     */
    fun getDeviceModelNumber(): String {
        return Build.MODEL
    }

    /**
     * 手机系统版本
     * @return Android 7.0, SDK 24
     */
    fun getDeviceOsVersion(): String =
            "Android "+Build.VERSION.RELEASE +", SDK "+Build.VERSION.SDK_INT


    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    fun getIMEI(ctx: Context): String? {
        return if (checkPermission(ctx, Manifest.permission.READ_PHONE_STATE)) {
            val tm = ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.imei
            } else {
                tm.deviceId
            }
        } else {
            null
        }
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * 手机内存
     */
    fun getDeviceMemory(context: Context): String {
        if (!checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)){
            return ""
        }
        val str1 = "/proc/meminfo"// 系统内存信息文件
        val str2: String
        var initialMemory: Long = 0
        try {
            val localFileReader = FileReader(str1)
            val localBufferedReader = BufferedReader(
                    localFileReader, 8192)
            str2 = localBufferedReader.readLine()// 读取meminfo第一行，系统总内存大小
            val number = StringUtil.getNumbers(str2)
            if (!TextUtils.isEmpty(number)){
                initialMemory = (number.toLong() * 1024)// 获得系统总内存，单位是KB，乘以1024转换为Byte
            }
            localBufferedReader.close()
            localFileReader.close()
        } catch (e: Exception) {
            XLog.error("read Memory file error", e)
        }
        return Formatter.formatFileSize(context, initialMemory)// Byte转换为KB或者MB，内存大小规格化
    }


    /**
     * cpu 指令集
     */
    fun getDeviceCpuABI():String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS.joinToString()
        } else {
            Build.CPU_ABI
        }
    }

    /**
     * 获取当前应用程序的版本
     */
    fun getAppVersionName(context: Context): String {
        var version = "0"
        try {
            version = context.packageManager.getPackageInfo(
                    context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(this.javaClass.simpleName + "the application not found")
        }

        return version
    }

    /**
     * 实现文本复制功能
     * @param content
     */
    fun copyTextToClipboard(content: String, context: Context) {
        // 得到剪贴板管理器
        val cmb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Text", content.trim { it <= ' ' })
        cmb.primaryClip = clip
    }

    /**
     * 启动应用
     */
    fun runApp(activity: Activity, filePath: String) {
        XLog.debug("runApp $filePath")
        val localIntent = Intent("android.intent.action.VIEW")
        val localUri = Uri.fromFile(File(filePath))
        localIntent.setDataAndType(localUri, "application/vnd.android.package-archive")
        activity.startActivity(localIntent)
    }

    /**
     * 用系统默认app打开文件
     * @param activity
     * @param file
     */
    fun openFileWithDefaultApp(activity: Activity, file: File) {
        try {
            val intent = Intent()
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.action = Intent.ACTION_VIEW
            //获取文件file的MIME类型
            val type = FileUtil.getMIMEType(file)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", file)
                intent.setDataAndType(contentUri, type)
            } else {
                intent.setDataAndType(Uri.fromFile(file), type)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            /*//设置intent的data和Type属性。
            intent.setDataAndType(Uri.fromFile(file), type);*/
            //跳转
            activity.startActivity(intent)
        } catch (e: Exception) {
            XLog.error("打开文件异常", e)
            XToast.toastShort(activity, "无法打开文件！")
        }
    }

    /**
     * 调用系统默认浏览器
     * @param activity
     * @param url
     */
    fun runDefaultBrowser(activity: Activity, url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }

    /**
     * 应用详细设置界面
     * @param activity
     */
    fun gotoSettingApplication(activity: Activity) {
        val packageURI = Uri.parse("package:" + activity.packageName)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
        activity.startActivity(intent)
    }


    /**
     * 应用设置未知来源安装 apk的权限
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun gotoSettingInstalls(activity: Activity) {
        val uri = Uri.parse("package:" + activity.packageName)
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,uri)
        activity.startActivity(intent)
    }

    /**
     * 检查是否有运行权限
     */
    fun checkPermission(context: Context, permission: String): Boolean =
            ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}