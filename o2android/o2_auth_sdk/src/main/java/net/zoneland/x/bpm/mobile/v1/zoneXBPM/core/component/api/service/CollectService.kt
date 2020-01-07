package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APILogData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ValueData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectCodeData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectDeviceData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.CollectUnitData
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface CollectService {

    /**
     * 给手机号码发送短信验证码
     * @param data
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/code")
    fun getCode(@Body data: CollectCodeData): Observable<ApiResponse<CollectCodeData>>

    /**
     * 根据手机号码和短信验证码获取单位列表
     * @param phone
     * *
     * @param code
     * *
     * @return
     */
    @GET("jaxrs/unit/list/account/{phone}/code/{code}")
    fun getUnitList(@Path("phone") phone: String, @Path("code") code: String): Observable<ApiResponse<List<CollectUnitData>>>

    /**
     * 绑定用户和设备
     * @param body
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/device/account/bind")
    fun bindDevice(@Body body: CollectDeviceData): Observable<ApiResponse<IdData>>

    /**
     * 解绑设备
     * @param token
     * *
     * @return
     */
    @DELETE("jaxrs/device/name/{token}/unbind")
    fun unBindDevice(@Path("token") token: String): Observable<ApiResponse<IdData>>


    /**
     * Unit下所有的Device
     * @param unit 单位id
     * @param account 账号 可以用手机号码
     * @param token 当前手机的token，就是device name
     */
    @GET("jaxrs/device/list/unit/{unit}/account/{account}/device/{token}")
    fun getBindDeviceList(@Path("unit") unit: String, @Path("account") account: String, @Path("token") token: String): Observable<ApiResponse<List<CollectDeviceData>>>

    /**
     * 检查绑定是否正确
     * @param name
     * *
     * @param phone
     * *
     * @param unitName
     *
     * @return
     */
    @GET("jaxrs/unit/find/{unitName}/{phone}/{name}")
    fun checkBindDevice(@Path("name") name: String, @Path("phone") phone: String, @Path("unitName") unitName: String): Observable<ApiResponse<CollectUnitData>>

    /**
     * 检查绑定是否正确
     * @param name
     * *
     * @param phone
     * *
     * @param unitName
     *
     * @param deviceType
     * *
     * @return
     */
    @GET("jaxrs/unit/find/{unitName}/{phone}/{name}/{deviceType}")
    fun checkBindDeviceNew(@Path("name") name: String, @Path("phone") phone: String, @Path("unitName") unitName: String, @Path("deviceType") deviceType:String): Observable<ApiResponse<CollectUnitData>>


    /**
     * 收集日志
     */
    @PUT("jaxrs/collect/applog/receive")
    fun collectLog(@Body log: APILogData):Observable<ApiResponse<ValueData>>

}