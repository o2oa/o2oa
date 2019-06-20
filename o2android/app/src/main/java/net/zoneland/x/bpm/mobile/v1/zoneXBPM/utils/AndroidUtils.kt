package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DownloadManager.Request.NETWORK_MOBILE
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
import android.net.ConnectivityManager
import android.net.NetworkInfo




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
     * 网络连接类型
     *  @return 0：没有网络 1：wifi 2：2G 3：3G 4：4G 5：流量
     */
    fun getAPNType(context: Context): String {
        var netType = "没有网络"
        val connMgr = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo ?: return netType
        val nType = networkInfo.type
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = "wifi"// wifi
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            val nSubType = networkInfo.subtype
            /*
            GPRS : 2G(2.5) General Packet Radia Service 114kbps
            EDGE : 2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
            UMTS : 3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
            CDMA : 2G 电信 Code Division Multiple Access 码分多址
            EVDO_0 : 3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
            EVDO_A : 3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
            1xRTT : 2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
            HSDPA : 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
            HSUPA : 3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
            HSPA : 3G (分HSDPA,HSUPA) High Speed Packet Access
            IDEN : 2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
            EVDO_B : 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
            LTE : 4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
            EHRPD : 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
            HSPAP : 3G HSPAP 比 HSDPA 快些
            */
            return when(nSubType) {
                // 2G网络
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN ->
                    "2G"
                // 3G网络
                TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP ->
                    "3G"
                TelephonyManager.NETWORK_TYPE_LTE ->
                    "4G"
                else ->
                    "手机流量"
            }

        }
        return netType
    }

    /**
     * 运营商
     */
    fun getCarrier(context: Context): String {
        var carrierName = ""
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            val imsi = telephonyManager.subscriberId
            XLog.debug("运营商代码" + imsi!!)
            return if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                    carrierName = "中国移动"
                } else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
                    carrierName = "中国联通"
                } else if (imsi.startsWith("46003")) {
                    carrierName = "中国电信"
                }
                carrierName
            } else {
                "none"
            }
        }else {
            return "none"
        }
    }

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