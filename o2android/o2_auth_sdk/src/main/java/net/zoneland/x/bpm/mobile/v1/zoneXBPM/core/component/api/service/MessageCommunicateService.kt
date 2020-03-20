package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageForm
import retrofit2.http.*
import rx.Observable


interface MessageCommunicateService {


    /**
     * 创建会话
     * @param info
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/im/conversation")
    fun createConversation(@Body info: IMConversationInfo): Observable<ApiResponse<IMConversationInfo>>


    /**
     * 获取会话信息
     */
    @GET("jaxrs/im/conversation/{id}")
    fun conversation(@Path("id") id: String): Observable<ApiResponse<IMConversationInfo>>


    /**
     * 获取我的会话列表
     */
    @GET("jaxrs/im/conversation/list/my")
    fun myConversationList(): Observable<ApiResponse<List<IMConversationInfo>>>

    /**
     * 阅读消息
     */
    @PUT("jaxrs/im/conversation/{id}/read")
    fun readConversation(@Path("id")id: String): Observable<ApiResponse<IdData>>

    /**
     * 发送消息
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/im/msg")
    fun sendMessage(@Body msg: IMMessage): Observable<ApiResponse<IdData>>


    /**
     * 分页查询消息列表
     *
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/im/msg/list/{page}/size/{size}")
    fun messageByPage(@Path("page")page: Int,  @Path("size") size: Int, @Body conversation: IMMessageForm) :
            Observable<ApiResponse<List<IMMessage>>>

}