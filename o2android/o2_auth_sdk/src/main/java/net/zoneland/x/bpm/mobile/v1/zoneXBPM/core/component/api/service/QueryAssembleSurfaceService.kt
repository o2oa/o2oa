package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.TestObject
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable


interface QueryAssembleSurfaceService {

    /**
     * 执行视图
     * 返回视图结果
     */
    @PUT("jaxrs/view/{id}/execute")
    fun excuteView(@Path("id") id: String) : Observable<ApiResponse<TestObject>>
}