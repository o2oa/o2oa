package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ValueData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonPwdForm
import okhttp3.MultipartBody
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface OrgAssemblePersonalService {

    /**
     * 当前登录用户详细信息
     * @return
     */
    @GET("jaxrs/person")
    fun getCurrentPersonInfo(): Observable<ApiResponse<PersonJson>>

    /**
     * 修改当前登录用户信息
     * @param person
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/person")
    fun modifyCurrentPerson(@Body person: PersonJson): Observable<ApiResponse<IdData>>

    /**
     * 修改用户头像
     */
    @Multipart
    @PUT("jaxrs/person/icon")
    fun modifyCurrentPersonIcon(@Part body: MultipartBody.Part): Observable<ApiResponse<ValueData>>


    /**
     * 获取会议配置
     */
    @GET("jaxrs/definition/meetingConfig")
    fun getMeetingConfig() : Observable<ApiResponse<String>>


    /**
     * 更新当前用户密码
     */
    @PUT("jaxrs/person/password")
    fun modifyCurrentPersonPassword(@Body body: PersonPwdForm): Observable<ApiResponse<ValueData>>
}