package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base

import android.content.Context
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast


/**
 * Created by fancy on 2017/6/5.
 */

open class BasePresenterImpl<V: BaseView> : BasePresenter<V> {

    protected var mView:V? = null

    override fun attachView(view: V) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    fun getApiService(context: Context?, url:String): ApiService? {
        return try {
            RetrofitClient.instance().api(url)
        } catch (e: Exception) {
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "中心服务异常，请联系管理员！！")
            }
            null
        }
    }

    fun getCollectService(context: Context?): CollectService? {
        return try {
            RetrofitClient.instance().collectApi()
        }catch (e: Exception) {
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "O2注册中心服务异常，请联系管理员！！")
            }
            null
        }
    }

    /**
     * 人员组织服务
     */
    fun getOrganizationAssembleControlApi(context: Context?): OrganizationAssembleControlAlphaService? {
        return try {
            RetrofitClient.instance().organizationAssembleControlApi()
        }catch (e: Exception) {
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "人员组织模块服务异常，请联系管理员！！")
            }
            null
        }
    }

    /**
     * 认证服务
     */
    fun getAssembleAuthenticationService(context: Context?): OrgAssembleAuthenticationService? {
        return try {
            RetrofitClient.instance().assembleAuthenticationApi()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "权限认证模块服务异常，请联系管理员！！")
            }
            null
        }
    }

    /**
     * 热图服务
     */
    fun getHotPicAssembleControlServiceApi(context: Context?): HotpicAssembleControlService? {
        return try {
            RetrofitClient.instance().hotpicAssembleControlServiceApi()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "热点图片新闻服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 人员服务
     */
    fun getAssemblePersonalApi(context: Context?):OrgAssemblePersonalService? {
        return try {
            RetrofitClient.instance().assemblePersonalApi()
        }catch (e: Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "个人信息服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 组织服务
     */
    fun getAssembleExpressApi(context: Context?):OrgAssembleExpressService? {
        return try {
            RetrofitClient.instance().assembleExpressApi()
        }catch (e: Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "组织管理服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 流程服务
     */
    fun getProcessAssembleSurfaceServiceAPI(context: Context?):ProcessAssembleSurfaceService? {
        return try {
            RetrofitClient.instance().processAssembleSurfaceServiceAPI()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "流程服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 云盘服务
     */
    fun getFileAssembleControlService(context: Context?): FileAssembleControlService? {
        return try {
            RetrofitClient.instance().fileAssembleControlApi()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "云盘服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 会议管理服务
     */
    fun getMeetingAssembleControlService(context: Context?):MeetingAssembleControlService?{
        return try {
            RetrofitClient.instance().meetingAssembleControlApi()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "会议管理模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 考勤管理服务
     */
    fun getAttendanceAssembleControlService(context: Context?): AttendanceAssembleControlService? {
        return try {
            RetrofitClient.instance().attendanceAssembleControlApi()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "考勤管理模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 论坛服务
     */
    fun getBBSAssembleControlService(context: Context?): BBSAssembleControlService? {
        return try {
            RetrofitClient.instance().bbsAssembleControlServiceApi()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "论坛服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 信息中心
     */
    fun getCMSAssembleControlService(context: Context?): CMSAssembleControlService? {
        return try {
            RetrofitClient.instance().cmsAssembleControlService()
        }catch (e:Exception){
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "信息中心服务模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 公共配置服务
     */
    fun getOrganizationAssembleCustomService(context: Context?): OrganizationAssembleCustomService? {
        return try {
            RetrofitClient.instance().organizationAssembleCustomService()
        }catch (e:Exception){
            XLog.error("", e)
//            if (context!=null){
//                XToast.toastLong(context, "公共配置服务模块异常，请联系管理员！")
//            }
            null
        }
    }

    /**
     * 门户模块
     */
    fun getPortalAssembleSurfaceService(context: Context?): PortalAssembleSurfaceService? {
        return try {
            RetrofitClient.instance().portalAssembleSurfaceService()
        }catch (e: Exception) {
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "门户模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 日程管理
     */
    fun getCalendarAssembleService(context: Context?): CalendarAssembleControlService? {
        return try {
            RetrofitClient.instance().calendarAssembleControlService()
        }catch (e:Exception) {
            XLog.error("", e)
            if (context!=null){
                XToast.toastLong(context, "日程管理模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 图灵 v1 服务
     */
    fun getTuling123Service(context: Context?): Tuling123Service? {
        return try {
            RetrofitClient.instance().tuling123Service()
        }catch (e: Exception){
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "AI查询模块异常，请联系管理员！")
            }
            null
        }
    }

    /**
     * 人脸识别服务
     */
    fun getFaceppService(baseUrl:String, context: Context?): FaceppApiService? {
        return try {
            RetrofitClient.instance().faceppApiService(baseUrl)
        }catch (e: Exception) {
            XLog.error("", e)
            if (context!=null) {
                XToast.toastLong(context, "人脸识别模块异常，请联系管理员！")
            }
            null
        }
    }
}