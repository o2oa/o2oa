package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast

/**
 * Created by fancyLou on 20/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


open class BaseO2ViewModel(app: Application) : AndroidViewModel(app) {

    fun getApiService(url: String): ApiService? {
        return try {
            RetrofitClient.instance().api(url)
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "中心服务异常，请联系管理员！！")
            null
        }
    }

    fun getCollectService(): CollectService? {
        return try {
            RetrofitClient.instance().collectApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "O2注册中心服务异常，请联系管理员！！")
            null
        }
    }

    /**
     * 人员组织服务
     */
    fun getOrganizationAssembleControlApi(): OrganizationAssembleControlAlphaService? {
        return try {
            RetrofitClient.instance().organizationAssembleControlApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "人员组织模块服务异常，请联系管理员！！")
            null
        }
    }

    /**
     * 认证服务
     */
    fun getAssembleAuthenticationService(): OrgAssembleAuthenticationService? {
        return try {
            RetrofitClient.instance().assembleAuthenticationApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "权限认证模块服务异常，请联系管理员！！")
            null
        }
    }

    /**
     * 热图服务
     */
    fun getHotPicAssembleControlServiceApi(): HotpicAssembleControlService? {
        return try {
            RetrofitClient.instance().hotpicAssembleControlServiceApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "热点图片新闻服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 人员服务
     */
    fun getAssemblePersonalApi(): OrgAssemblePersonalService? {
        return try {
            RetrofitClient.instance().assemblePersonalApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "个人信息服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 组织服务
     */
    fun getAssembleExpressApi(): OrgAssembleExpressService? {
        return try {
            RetrofitClient.instance().assembleExpressApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "组织管理服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 流程服务
     */
    fun getProcessAssembleSurfaceServiceAPI(): ProcessAssembleSurfaceService? {
        return try {
            RetrofitClient.instance().processAssembleSurfaceServiceAPI()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "流程服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 云盘服务
     */
    fun getFileAssembleControlService(): FileAssembleControlService? {
        return try {
            RetrofitClient.instance().fileAssembleControlApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "云盘服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 会议管理服务
     */
    fun getMeetingAssembleControlService(): MeetingAssembleControlService? {
        return try {
            RetrofitClient.instance().meetingAssembleControlApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "会议管理模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 考勤管理服务
     */
    fun getAttendanceAssembleControlService(): AttendanceAssembleControlService? {
        return try {
            RetrofitClient.instance().attendanceAssembleControlApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "考勤管理模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 论坛服务
     */
    fun getBBSAssembleControlService(): BBSAssembleControlService? {
        return try {
            RetrofitClient.instance().bbsAssembleControlServiceApi()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "论坛服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 信息中心
     */
    fun getCMSAssembleControlService(): CMSAssembleControlService? {
        return try {
            RetrofitClient.instance().cmsAssembleControlService()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "信息中心服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 公共配置服务
     */
    fun getOrganizationAssembleCustomService(): OrganizationAssembleCustomService? {
        return try {
            RetrofitClient.instance().organizationAssembleCustomService()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "公共配置服务模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 门户模块
     */
    fun getPortalAssembleSurfaceService(): PortalAssembleSurfaceService? {
        return try {
            RetrofitClient.instance().portalAssembleSurfaceService()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "门户模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 日程管理
     */
    fun getCalendarAssembleService(): CalendarAssembleControlService? {
        return try {
            RetrofitClient.instance().calendarAssembleControlService()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "日程管理模块异常，请联系管理员！")
            null
        }
    }

    /**
     * 图灵 v1 服务
     */
    fun getTuling123Service(): Tuling123Service? {
        return try {
            RetrofitClient.instance().tuling123Service()
        } catch (e: Exception) {
            XLog.error("", e)
            XToast.toastLong(O2App.instance, "AI查询模块异常，请联系管理员！")
            null
        }
    }
}