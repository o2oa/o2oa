package net.zoneland.x.bpm.mobile.v1.zoneXBPM

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import cn.jpush.android.api.JPushInterface
import com.baidu.mapapi.SDKInitializer
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.sdk.QbSdk
import com.zlw.main.recorderlib.RecordManager
import io.realm.Realm
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.LogSingletonService


/**
 * Created by fancy on 2017/6/5.
 */

class O2App : MultiDexApplication() {


    companion object {
        lateinit var instance: O2App
    }


    /**
     * baidu
     */
    val BAIDU_APP_ID: String by lazy {
        getAppMeta("com.baidu.speech.APP_ID")
    }
    val BAIDU_APP_KEY: String by lazy {
        getAppMeta("com.baidu.speech.API_KEY")
    }
    val BAIDU_SECRET_KEY: String by lazy {
        getAppMeta("com.baidu.speech.SECRET_KEY")
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        //sdk
        O2SDKManager.instance().init(this)
        //数据库
        Realm.init(this)

        //换肤插件

        FancySkinManager.instance().withoutActivity(this)
                .addSupportAttr("icon", IconChangeColorIconSkinAttr())
                .addSupportAttr("iconCompleted", IconCompletedChangeColorIconSkinAttr())
                .addSupportAttr("color", ColorChangeColorIconSkinAttr())
                .addSupportAttr("drawableLeft", DrawableLeftRadioButtonSkinAttr())
                .addSupportAttr("tabIndicatorColor", TabIndicatorColorTabLayoutSkinAttr())
                .addSupportAttr("backgroundTint", BackgroundTintFloatingActionButtonSkinAttr())
                .addSupportAttr("o2ButtonColor", O2ProgressButtonColorSkinAttr())


        //baidu
        try {
            SDKInitializer.initialize(applicationContext)
            //bugly
            CrashReport.initCrashReport(applicationContext)

            // qb
            QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback{
                override fun onCoreInitFinished() {
                    Log.i("O2app", "qb sdk core init finish..........")
                }

                override fun onViewInitFinished(p0: Boolean) {
                    Log.i("O2app", "qb sdk init $p0 ..........")
                }
            })
            QbSdk.setDownloadWithoutWifi(true)

            //极光推送
            initJMessageAndJPush()
        } catch (e: Exception) {
        }



//        Fresco.initialize(this)
        //注册日志记录器
        LogSingletonService.instance().registerApp(this)

        //录音
        RecordManager.getInstance().init(this, false)

        Log.i("O2app", "O2app init.....................................................")
        //stetho developer tool
//        Stetho.initializeWithDefaults(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


//  获取Application下的meta值
    fun getAppMeta(metaName: String, default: String = "") : String {
        return try {
            if ("com.baidu.speech.APP_ID" == metaName) {
                val appid = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData.getInt("com.baidu.speech.APP_ID")
                ""+appid
            }else {
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData.getString(metaName, default)
            }
        }catch (e: Exception) {
            Log.e("O2app", "", e)
            default
        }
    }


    private fun initJMessageAndJPush() {
        JPushInterface.init(this)

    }



}