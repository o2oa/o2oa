package net.zoneland.x.bpm.mobile.v1.zoneXBPM

import android.content.Context
import android.content.pm.PackageManager
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import android.util.Log
import android.util.LongSparseArray
import cn.jpush.android.api.JPushInterface
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo
import cn.jpush.im.api.BasicCallback
import com.baidu.mapapi.SDKInitializer
import com.facebook.drawee.backends.pipeline.Fresco
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.sdk.QbSdk
import io.realm.Realm
import jiguang.chat.application.JGApplication
import jiguang.chat.entity.NotificationClickEventReceiver
import jiguang.chat.pickerimage.utils.ScreenUtil
import jiguang.chat.pickerimage.utils.StorageUtil
import jiguang.chat.utils.SharePreferenceManager
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.skin.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.LogSingletonService
import java.lang.Exception


/**
 * Created by fancy on 2017/6/5.
 */

class O2App : MultiDexApplication() {


    companion object {
        lateinit var instance: O2App
    }

    /**
     * 极光IM SDK相关
     */
    val JM_IM_USER_PASSWORD: String by lazy {
        getAppMeta("JM_IM_USER_PASSWORD")
    }
    val JM_IM_APP_KEY: String by lazy {
        getAppMeta("JPUSH_APPKEY")
    }
    var isAtMe =  LongSparseArray<Boolean>()
    var isAtall = LongSparseArray<Boolean>()
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
        //J
        ScreenUtil.init(this)
        StorageUtil.init(this, null)
        Fresco.initialize(this)
        SharePreferenceManager.init(this, JGApplication.JCHAT_CONFIGS)

        //注册Notification点击的接收器
        NotificationClickEventReceiver(this)
        //注册日志记录器
        LogSingletonService.instance().registerApp(this)

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
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData.getString(metaName, default)
        }catch (e: Exception) {
            Log.e("O2app", "", e)
            default
        }
    }





    //MARK: - 极光IM 相关的函数

    private fun initJMessageAndJPush() {
        JPushInterface.init(this)
        //极光IM
        JMessageClient.init(applicationContext, true)
        JMessageClient.setDebugMode(false)
    }

    /**
     * 当前登录用户信息
     */
    fun _JMMyUserInfo(): UserInfo? =
            JMessageClient.getMyInfo()

    /**
     * 是否登录成功
     */
    fun _JMIsLogin(): Boolean = _JMMyUserInfo() != null


    fun _JMLoginInner(){
        _JMLogin { isSuccess ->
            Log.i("O2app", "登录IM服务器result：$isSuccess *****************************************")
        }
    }

    /**
     * 登录IM服务器
     */
    fun _JMLogin(back: (isSuccess: Boolean)->Unit) {
        if (TextUtils.isEmpty(O2SDKManager.instance().cId)) {
            Log.i("O2app", "用户未登录。。。。。。。。。。不能连接到IM服务器")
            return
        }

        JMessageClient.login(O2SDKManager.instance().cId, JM_IM_USER_PASSWORD, object : BasicCallback() {
            override fun gotResult(responseCode: Int, message: String?) {
                if (responseCode == 0) {
                    //登录成功
                    Log.i("O2app", "登录极光IM服务器成功！！！！！！！！！！！！")
                    back(true)
                } else {
                    Log.i("O2app", "login JM IM fail, code: $responseCode, message: $message")
                    //如果没有注册过 就注册一个
                    when (responseCode) {
                        800004, 800005, 800006, 801003, 898005 -> {
                            _JMRegister(back)
                        }
                        else -> {
                            back(false)
                        }
                    }
                }
            }
        })
    }

    fun _JMLogout() = JMessageClient.logout()

    /**
     * 注册用户
     */
    fun _JMRegister(back: (isSuccess: Boolean)->Unit) {
        val userInfo = RegisterOptionalUserInfo()
        userInfo.nickname = O2SDKManager.instance().cName
        val avatarHttpUrl = APIAddressHelper.instance().getPersonAvatarUrlWithoutPermission(O2SDKManager.instance().cId)
        userInfo.avatar = avatarHttpUrl
        JMessageClient.register(O2SDKManager.instance().cId, JM_IM_USER_PASSWORD, userInfo, object : BasicCallback() {
            override fun gotResult(responseCode: Int, message: String?) {
                if (responseCode == 0) {
                    //注册成功
                    Log.i("O2app", "注册极光IM服务器成功！！！！！！！！！！！！开始重新登录。。。。。。。。")
                    _JMLogin(back)
                } else {
                    Log.i("O2app", "register JM IM fail, code: $responseCode, message: $message")
                    back(false)
                }
            }
        })
    }


}