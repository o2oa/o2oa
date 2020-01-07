package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APIDistributeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.CustomStyleData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.CustomStyleUpdateData
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface ApiService {

    /**
     * 获取服务器分发地址
     * @param source 访问的host 也就是外网地址
     * *
     * @return
     */
    @GET("jaxrs/distribute/webserver/assemble/source/{source}")
    fun getWebserverDistributeWithSource(@Path("source") source: String): Observable<ApiResponse<APIDistributeData>>

    /**
     * 服务端 应用自定义图片样式获取
     */
    @GET("jaxrs/appstyle/current/style")
    fun getCustomStyle(): Observable<ApiResponse<CustomStyleData>>

    /**
     * 服务端 应用自定义图片样式更新时间
     */
    @GET("jaxrs/appstyle/current/update")
    fun getCustomStyleUpdateDate():  Observable<ApiResponse<CustomStyleUpdateData>>
}