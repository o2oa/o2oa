package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
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
     * 更新会话
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/im/conversation")
    fun updateConversation(@Body form: IMConversationUpdateForm) : Observable<ApiResponse<IMConversationInfo>>

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


    /**
     * 个人消息 排除IM消息
     *  列表
     */
    @GET("jaxrs/instant/list/currentperson/noim/count/{count}/desc")
    fun instantMessageList(@Path("count") count: Int) : Observable<ApiResponse<List<InstantMessage>>>

    /**
     * 上传文件
     * im消息文件 图片 音频 视频等
     * @param conversationId 会话id
     * @param type 消息类型 image audio 等
     */
    @Multipart
    @POST("jaxrs/im/msg/upload/{conversationId}/type/{type}")
    fun uploadFile(@Path("conversationId") conversationId: String, @Path("type") type: String, @Part body: MultipartBody.Part): Observable<ApiResponse<IMMessageFileData>>



}