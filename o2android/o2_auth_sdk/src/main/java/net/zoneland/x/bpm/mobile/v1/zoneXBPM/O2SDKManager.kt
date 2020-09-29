package net.zoneland.x.bpm.mobile.v1.zoneXBPM

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2.SECURITY_IS_UPDATE
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.LaunchState
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.exception.NoBindException
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.exception.NoLoginException
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal.PortalData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SharedPreferencesHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.security.SecuritySharedPreference
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception
import java.lang.RuntimeException

/**
 * Created by fancyLou on 2018/11/22.
 * Copyright © 2018 O2. All rights reserved.
 */


class O2SDKManager private constructor()  {

    val TAG = "O2SDKManager"

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: O2SDKManager? = null

        fun instance(): O2SDKManager {
            if (INSTANCE == null) {
                synchronized(O2SDKManager::class) {
                    if (INSTANCE == null) {
                        INSTANCE = O2SDKManager()
                    }
                }
            }
            return INSTANCE!!
        }

    }

    private val CURRENT_PERSON_ID_KEY = "CURRENT_PERSON_ID_KEY"//用户 id
    private val CURRENT_PERSON_DISTINGUISHED_KEY = "CURRENT_PERSON_DISTINGUISHED_KEY"//用户 唯一标识
    private val CURRENT_PERSON_UPDATETIME_KEY = "CURRENT_PERSON_UPDATETIME_KEY"//用户最后更新时间
    private val CURRENT_PERSON_GENDERTYPE_KEY = "CURRENT_PERSON_GENDERTYPE_KEY"//性别
    private val CURRENT_PERSON_PINYIN_KEY = "CURRENT_PERSON_PINYIN_KEY"//拼音
    private val CURRENT_PERSON_PINYININITIAL_KEY = "CURRENT_PERSON_PINYININITIAL_KEY"//拼音简写
    private val CURRENT_PERSON_NAME_KEY = "CURRENT_PERSON_NAME_KEY"//姓名
    private val CURRENT_PERSON_EMPLOYEE_KEY = "CURRENT_PERSON_EMPLOYEE_KEY"//员工号
    private val CURRENT_PERSON_UNIQUE_KEY = "CURRENT_PERSON_UNIQUE_KEY"//
    private val CURRENT_PERSON_CONTROLLERLIST_KEY = "CURRENT_PERSON_CONTROLLERLIST_KEY"//
    private val CURRENT_PERSON_MAIL_KEY = "CURRENT_PERSON_MAIL_KEY"//邮箱地址
    private val CURRENT_PERSON_QQ_KEY = "CURRENT_PERSON_QQ_KEY"//我的qq
    private val CURRENT_PERSON_WEIXIN_KEY = "CURRENT_PERSON_WEIXIN_KEY"//微信号
    private val CURRENT_PERSON_MOBILE_KEY = "CURRENT_PERSON_MOBILE_KEY"//手机号
    private val CURRENT_PERSON_DEVICELIST_KEY = "CURRENT_PERSON_DEVICELIST_KEY"//
    private val CURRENT_PERSON_SIGNATURE_KEY = "CURRENT_PERSON_SIGNATURE_KEY"//
    private val CURRENT_PERSON_TOKEN_KEY = "CURRENT_PERSON_TOKEN_KEY"//登录的token
    private val CURRENT_PERSON_ROLELIST_KEY = "CURRENT_PERSON_ROLELIST_KEY"//角色

    /***********************当前登录的用户信息 */
    var cId: String = ""//用户唯一标识
    var distinguishedName: String = "" //用户唯一标识
    var cUnique: String = "" //用户唯一标识
    var cUpdateTime: String = ""
    var cGenderType: String = ""
    var cPinyin: String = ""
    var cPinyinInitial: String = ""
    var cName: String = ""
    var cEmployee: String = ""
    var cControllerList: String = ""
    var cMail: String = ""
    var cQq: String = ""
    var cWeixin: String = ""
    var cMobile: String = ""
    var cDeviceList: String = ""
    var cSignature: String = ""
    var cRoleList: String = ""//角色
    //扩展信息
    var zToken: String = ""//用户登录的token



    private lateinit var context: Context
    private lateinit var spHelper: SharedPreferencesHelper
    val gson: Gson by lazy { GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create() }

    /**
     * Application onCreate中初始化 context = ApplicationContext
     */
    fun init(context: Context) {
        //初始化RetrofitClient
        this.context = context
        spHelper = SharedPreferencesHelper(context)
        //检查老的sp 是否要更新
        val isUpdate = prefs().getBoolean(SECURITY_IS_UPDATE, false)
        if (!isUpdate) {
            Log.i(TAG, "过渡老的sp文件！")
            prefs().handleTransition() //执行过渡程序把老的sp文件读取覆盖一下
            prefs().edit().putBoolean(SECURITY_IS_UPDATE, true).apply()
        }

        RetrofitClient.instance().init(context)
        cId = prefs().getString(CURRENT_PERSON_ID_KEY, "") ?: ""
        distinguishedName = prefs().getString(CURRENT_PERSON_DISTINGUISHED_KEY, "") ?: ""
        cUpdateTime = prefs().getString(CURRENT_PERSON_UPDATETIME_KEY, "") ?: ""
        cGenderType = prefs().getString(CURRENT_PERSON_GENDERTYPE_KEY, "") ?: ""
        cPinyin = prefs().getString(CURRENT_PERSON_PINYIN_KEY, "") ?: ""
        cPinyinInitial = prefs().getString(CURRENT_PERSON_PINYININITIAL_KEY, "") ?: ""
        cName = prefs().getString(CURRENT_PERSON_NAME_KEY, "") ?: ""
        cEmployee = prefs().getString(CURRENT_PERSON_EMPLOYEE_KEY, "") ?: ""
        cUnique = prefs().getString(CURRENT_PERSON_UNIQUE_KEY, "") ?: ""
        cControllerList = prefs().getString(CURRENT_PERSON_CONTROLLERLIST_KEY, "") ?: ""
        cMail = prefs().getString(CURRENT_PERSON_MAIL_KEY, "") ?: ""
        cQq = prefs().getString(CURRENT_PERSON_QQ_KEY, "") ?: ""
        cWeixin = prefs().getString(CURRENT_PERSON_WEIXIN_KEY, "") ?: ""
        cMobile = prefs().getString(CURRENT_PERSON_MOBILE_KEY, "") ?: ""
        cDeviceList = prefs().getString(CURRENT_PERSON_DEVICELIST_KEY, "") ?: ""
        cSignature = prefs().getString(CURRENT_PERSON_SIGNATURE_KEY, "") ?: ""
        cRoleList = prefs().getString(CURRENT_PERSON_ROLELIST_KEY, "") ?: ""
        //扩展信息
        zToken = prefs().getString(CURRENT_PERSON_TOKEN_KEY, "") ?: ""//TOKEN


    }

//    fun prefs(): SharedPreferences = spHelper.prefs()

    fun prefs(): SecuritySharedPreference = spHelper.securityPrefs()

    /**
     * 启动  整个启动过程，检查绑定 连接中心服务器 下载配置 登录
     */
    fun launch(deviceToken: String, showState:(state: LaunchState)->Unit) {

        if (TextUtils.isEmpty(deviceToken)) {
            Log.e(TAG,"没有deviceToken！")
            showState(LaunchState.NoBindError)
            return
        }
        val phone = prefs().getString(O2.PRE_BIND_PHONE_KEY, "") ?: ""
        val unit = prefs().getString(O2.PRE_BIND_UNIT_KEY, "") ?: ""
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(unit)) {
            Log.e(TAG,"没有绑定手机号码。。。。")
            showState(LaunchState.NoBindError)
            return
        }
        try {
            val client = RetrofitClient.instance()
            showState(LaunchState.ConnectO2Collect)
            client.collectApi().checkBindDeviceNew(deviceToken, phone, unit, O2.DEVICE_TYPE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext {collectUnitRes->
                            saveCollectInfo(collectUnitRes.data, showState)
                        }
                        onError { e, _ ->
                            Log.e(TAG, "检查绑定异常", e)
                            showState(LaunchState.NoBindError)
                        }
                    }
        }catch (e: RuntimeException) {
            Log.e(TAG, "catch到的异常", e)
            showState(LaunchState.UnknownError)
        }

    }

    /**
     * 启动 内网使用版本
     */
    fun launchInner(serverJson: String, showState:(state: LaunchState)->Unit) {
        if (TextUtils.isEmpty(serverJson)) {
            showState(LaunchState.UnknownError)
            return
        }
        try {
            Observable.just(true)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        val server = gson.fromJson<CollectUnitData>(serverJson, CollectUnitData::class.java)
                        Observable.just(server)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .o2Subscribe {
                        onNext { unit->
                            saveCollectInfo(unit, showState)
                        }
                        onError { e, _ ->
                            Log.e(TAG, "未知异常", e)
                            showState(LaunchState.UnknownError)
                        }
                    }
        }catch (e: Exception) {
            Log.e(TAG, "catch到的异常", e)
            showState(LaunchState.UnknownError)
        }

    }

    /**
     * 绑定信息存储
     */
    fun bindUnit(unit: CollectUnitData, phone: String, deviceToken: String) {
        val url = APIAddressHelper.instance().getCenterUrl(unit.centerHost, unit.centerContext, unit.centerPort)
        prefs().edit {
            putString(O2.PRE_CENTER_URL_KEY, url)
            putString(O2.PRE_CENTER_HTTP_PROTOCOL_KEY, unit.httpProtocol)
            putString(O2.PRE_CENTER_HOST_KEY, unit.centerHost)
            putString(O2.PRE_CENTER_CONTEXT_KEY, unit.centerContext)
            putInt(O2.PRE_CENTER_PORT_KEY, unit.centerPort)
            putString(O2.PRE_BIND_UNIT_ID_KEY, unit.id)
            putString(O2.PRE_BIND_UNIT_KEY, unit.name)
            putString(O2.PRE_BIND_PHONE_KEY, phone)
            putString(O2.PRE_BIND_PHONE_TOKEN_KEY, deviceToken)
        }
    }

    /**
     * 清除绑定信息
     */
    fun clearBindUnit() {
        prefs().edit {
            putString(O2.PRE_CENTER_URL_KEY, "")
            putString(O2.PRE_CENTER_HOST_KEY, "")
            putString(O2.PRE_CENTER_HTTP_PROTOCOL_KEY, "")
            putString(O2.PRE_CENTER_CONTEXT_KEY, "")
            putInt(O2.PRE_CENTER_PORT_KEY, 0)
            putString(O2.PRE_BIND_UNIT_ID_KEY, "")
            putString(O2.PRE_BIND_UNIT_KEY, "")
            putString(O2.PRE_BIND_PHONE_KEY, "")
            putString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")
        }
    }


    private fun saveCollectInfo(unit: CollectUnitData, showState:(state: LaunchState)->Unit) {
        Log.d(TAG, "unit: ${unit.centerHost}, port: ${unit.centerPort} , id: ${unit.id}")
        //更新http协议
        RetrofitClient.instance().setO2ServerHttpProtocol(unit.httpProtocol)
        APIAddressHelper.instance().setHttpProtocol(unit.httpProtocol)
        val host = unit.centerHost
        val newUrl = APIAddressHelper.instance().getCenterUrl(unit.centerHost, unit.centerContext, unit.centerPort)
        O2SDKManager.instance().prefs().edit {
            putString(O2.PRE_BIND_UNIT_ID_KEY, unit.id)
            putString(O2.PRE_CENTER_URL_KEY, newUrl)
            putString(O2.PRE_CENTER_HTTP_PROTOCOL_KEY, unit.httpProtocol)
            putString(O2.PRE_CENTER_HOST_KEY, unit.centerHost)
            putString(O2.PRE_CENTER_CONTEXT_KEY, unit.centerContext)
            putInt(O2.PRE_CENTER_PORT_KEY, unit.centerPort)
            putString(O2.PRE_BIND_UNIT_KEY, unit.name)
        }
        Log.d(TAG, "保存 服务器信息成功！！！！newUrl：$newUrl")
        Log.d(TAG, "httpProtocol:${unit.httpProtocol}")
        Log.d(TAG, "host:$host")

        /////////////////////////// 开始业务逻辑  ////////////////////////////////////

        Log.d(TAG, "开始连接center......$newUrl")
        showState(LaunchState.ConnectO2Server)
        val client = RetrofitClient.instance()
        val api = client.api(newUrl)
        api.getWebserverDistributeWithSource(host)
                .subscribeOn(Schedulers.io())
                .flatMap { response->
                    Log.d(TAG, "开始检查配置.....")
                    showState(LaunchState.CheckMobileConfig)
                    APIAddressHelper.instance().setDistributeData(response.data)
                    api.getCustomStyleUpdateDate()
                }
                .flatMap { response->
                    val hash = prefs().getString(O2CustomStyle.CUSTOM_STYLE_UPDATE_HASH_KEY, "")
                            ?: ""
                    val result = response.data.value
                    Log.d(TAG, "检查配置newHash：$result ， oldHash：$hash")
                    if (hash == result) {
                        Observable.just(false)
                    } else {
                        prefs().edit {
                            putString(O2CustomStyle.CUSTOM_STYLE_UPDATE_HASH_KEY, result)
                        }
                        Observable.just(true)
                    }
                }.flatMap { flag->
                    if (flag) {
                        Log.d(TAG, "开始下载配置.....")
                        showState(LaunchState.DownloadMobileConfig)
                        var excep = false
                        api.getCustomStyle()
                                .subscribeOn(Schedulers.immediate())
                                .o2Subscribe {
                                    onNext {res->
                                        val style = res.data
                                        // 去除不需要显示的门户
                                        val portalList = style.portalList
                                        val newlist: ArrayList<PortalData> = ArrayList()
                                        if (!portalList.isEmpty()) {
                                            for (portal in portalList) {
                                                if (portal.mobileClient) {
                                                    newlist.add(portal)
                                                }
                                            }
                                            style.portalList = newlist
                                        }

                                        val styleJson = gson.toJson(style)
                                        prefs().edit {
                                            putString(O2CustomStyle.CUSTOM_STYLE_JSON_KEY, styleJson)
                                        }
                                    }
                                    onError { e, _ ->
                                        Log.e(TAG, "下载配置文件出错", e)
                                        excep = true
                                    }
                                }
                        if (excep) {
                            Observable.error<Boolean>(RuntimeException("下载配置文件出错"))
                        }else {
                            Observable.just(true)
                        }
                    }else {
                        Observable.just(true)
                    }
                }.flatMap { flag ->
                    Log.d(TAG, "开始登录......$flag")
                    showState(LaunchState.AutoLogin)
                    if (TextUtils.isEmpty(zToken)) {
                        Observable.error<ApiResponse<AuthenticationInfoJson>>(NoLoginException("没有登录！"))
                    }else {
                        client.assembleAuthenticationApi().who(zToken)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { who->
                        val authentication = who.data
                        if (authentication.name != O2.TOKEN_TYPE_ANONYMOUS) {
                            if (TextUtils.isEmpty(authentication.token)) {
                                Log.d(TAG, "开始登录过期了......")
                                logoutCleanCurrentPerson()
                                showState(LaunchState.NoLoginError)
                            } else {
                                setCurrentPersonData(authentication)
                                showState(LaunchState.Success)
                            }
                        }else{
                            Log.d(TAG, "开始登录过期了......")
                            logoutCleanCurrentPerson()
                            showState(LaunchState.NoLoginError)
                        }
                    }
                    onError { e, _ ->
                        Log.e(TAG, "", e)
                        showState(LaunchState.NoLoginError)
                    }
                }
    }


    /**
     * 是否系统管理员
     */
    fun isAdministrator(): Boolean {
        val roleList = this.cRoleList.split(",").map {
            if (it.contains("@")) {
                it.substring(0, it.indexOf("@")).toLowerCase()
            } else {
                it.toLowerCase()
            }
        }
        fun isAdminRole(): Boolean = roleList.any { it == "manager" }
        return this.cName == "xadmin" || isAdminRole()
    }

    /**
     * 是否会议管理员
     */
    fun isMeetingAdministrator(): Boolean {
        if (isAdministrator()) return true
        val roleList = this.cRoleList.split(",").map {
            if (it.contains("@")) {
                it.substring(0, it.indexOf("@")).toLowerCase()
            } else {
                it.toLowerCase()
            }
        }
        return roleList.any { it == "meetingmanager" }
    }

    /**
     * 登录 加载用户信息
     */
    fun setCurrentPersonData(data: AuthenticationInfoJson) {
        storagecId(data.id)
        storageDistinguishedName(data.distinguishedName)
        storagecUpdateTime(data.updateTime)
        storagezToken(data.token)
        storagecGenderType(data.genderType)
        storagecPinyin(data.pinyin)
        storagecPinyinInitial(data.pinyinInitial)
        storagecName(data.name)
        storagecEmployee(data.employee)
        storagecUnique(data.unique)
        storagecControllerList(data.controllerList.joinToString(","))
        storagecMail(data.mail)
        storagecQq(data.qq)
        storagecWeixin(data.weixin)
        storagecMobile(data.mobile)
        storagecDeviceList(data.deviceList.joinToString(","))
        storagecSignature(data.signature)
        storagecRoleList(data.roleList.joinToString(","))
    }

    /**
     * 登出 清空用户数据
     */
    fun logoutCleanCurrentPerson() {
        storagecId("")
        storageDistinguishedName("")
        storagecUnique("")
        storagecUpdateTime("")
        storagezToken("")
        storagecGenderType("")
        storagecPinyin("")
        storagecPinyinInitial("")
        storagecName("")
        storagecEmployee("")
        storagecControllerList("")
        storagecMail("")
        storagecQq("")
        storagecWeixin("")
        storagecMobile("")
        storagecDeviceList("")
//        storagecIcon("")
        storagecSignature("")
        storagecRoleList("")
    }


    fun storagecRoleList(cRoleList: String) {
        if (this.cRoleList == cRoleList) {
            return
        }
        this.cRoleList = cRoleList
        prefs().edit().putString(CURRENT_PERSON_ROLELIST_KEY, cRoleList).apply()
    }

    fun storagezToken(zToken: String) {
        if (this.zToken == zToken) {
            return
        }
        this.zToken = zToken
        prefs().edit().putString(CURRENT_PERSON_TOKEN_KEY, zToken).apply()
    }

    fun storagecId(cId: String) {
        if (this.cId == cId) {
            return
        }
        this.cId = cId
        prefs().edit().putString(CURRENT_PERSON_ID_KEY, cId).apply()
    }

    fun storageDistinguishedName(distinguishedName: String) {
        if (this.distinguishedName == distinguishedName) {
            return
        }
        this.distinguishedName = distinguishedName
        prefs().edit().putString(CURRENT_PERSON_DISTINGUISHED_KEY, distinguishedName).apply()
    }

    fun storagecUpdateTime(cUpdateTime: String) {
        if (this.cUpdateTime == cUpdateTime) {
            return
        }
        this.cUpdateTime = cUpdateTime
        prefs().edit().putString(CURRENT_PERSON_UPDATETIME_KEY, cUpdateTime).apply()
    }

    fun storagecGenderType(cGenderType: String) {
        if (this.cGenderType == cGenderType) {
            return
        }
        this.cGenderType = cGenderType
        prefs().edit().putString(CURRENT_PERSON_GENDERTYPE_KEY, cGenderType).apply()
    }

    fun storagecPinyin(cPinyin: String) {
        if (this.cPinyin == cPinyin) {
            return
        }
        this.cPinyin = cPinyin
        prefs().edit().putString(CURRENT_PERSON_PINYIN_KEY, cPinyin).apply()
    }

    fun storagecPinyinInitial(cPinyinInitial: String) {
        if (this.cPinyinInitial == cPinyinInitial) {
            return
        }
        this.cPinyinInitial = cPinyinInitial
        prefs().edit().putString(CURRENT_PERSON_PINYININITIAL_KEY, cPinyinInitial).apply()
    }

    fun storagecName(cName: String) {
        if (this.cName == cName) {
            return
        }
        this.cName = cName
        prefs().edit().putString(CURRENT_PERSON_NAME_KEY, cName).apply()
    }

    fun storagecEmployee(cEmployee: String) {
        if (this.cEmployee == cEmployee) {
            return
        }
        this.cEmployee = cEmployee
        prefs().edit().putString(CURRENT_PERSON_EMPLOYEE_KEY, cEmployee).apply()
    }

    fun storagecUnique(cUnique: String) {
        if (this.cUnique == cUnique) {
            return
        }
        this.cUnique = cUnique
        prefs().edit().putString(CURRENT_PERSON_UNIQUE_KEY, cUnique).apply()
    }

    fun storagecControllerList(cControllerList: String) {
        if (this.cControllerList == cControllerList) {
            return
        }
        this.cControllerList = cControllerList
        prefs().edit().putString(CURRENT_PERSON_CONTROLLERLIST_KEY, cControllerList).apply()
    }

    fun storagecMail(cMail: String) {
        if (this.cMail == cMail) {
            return
        }
        this.cMail = cMail
        prefs().edit().putString(CURRENT_PERSON_MAIL_KEY, cMail).apply()
    }

    fun storagecQq(cQq: String) {
        if (this.cQq == cQq) {
            return
        }
        this.cQq = cQq
        prefs().edit().putString(CURRENT_PERSON_QQ_KEY, cQq).apply()
    }

    fun storagecWeixin(cWeixin: String) {
        if (this.cWeixin == cWeixin) {
            return
        }
        this.cWeixin = cWeixin
        prefs().edit().putString(CURRENT_PERSON_WEIXIN_KEY, cWeixin).apply()
    }

    fun storagecMobile(cMobile: String) {
        if (this.cMobile == cMobile) {
            return
        }
        this.cMobile = cMobile
        prefs().edit().putString(CURRENT_PERSON_MOBILE_KEY, cMobile).apply()
    }

    fun storagecDeviceList(cDeviceList: String) {
        if (this.cDeviceList == cDeviceList) {
            return
        }
        this.cDeviceList = cDeviceList
        prefs().edit().putString(CURRENT_PERSON_DEVICELIST_KEY, cDeviceList).apply()
    }

    fun storagecSignature(cSignature: String) {
        if (this.cSignature == cSignature) {
            return
        }
        this.cSignature = cSignature
        prefs().edit().putString(CURRENT_PERSON_SIGNATURE_KEY, cSignature).apply()
    }

}