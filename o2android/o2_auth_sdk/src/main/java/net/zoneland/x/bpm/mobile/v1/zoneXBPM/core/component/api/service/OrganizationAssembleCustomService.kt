package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import retrofit2.http.GET
import rx.Observable

/**
 * Created by 73419 on 2017/12/18 0018.
 */
interface OrganizationAssembleCustomService {

    /**
     * 获取会议配置
     */
    @GET("jaxrs/definition/meetingConfig")
    fun getMeetingConfig() : Observable<ApiResponse<String>>
}