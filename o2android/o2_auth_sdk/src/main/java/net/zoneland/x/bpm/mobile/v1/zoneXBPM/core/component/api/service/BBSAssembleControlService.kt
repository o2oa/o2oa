package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface BBSAssembleControlService {
    /**
     * 获取分区列表 以及 分区下所有板块

     * @return
     */
    @GET("jaxrs/mobile/view/all")
    fun forumAll(): Observable<ApiResponse<List<ForumInfoJson>>>


    /**
     * 某一个板块下 置顶帖列表

     * @param sectionId
     * *
     * @return
     */
    @GET("jaxrs/subject/top/{sectionId}")
    fun topSubjectListBySectionId(
            @Path("sectionId") sectionId: String): Observable<ApiResponse<List<SubjectInfoJson>>>


    /**
     * 获取版块信息
     * @param sectionId
     * *
     * @return
     */
    @GET("jaxrs/section/{sectionId}")
    fun querySectionById(@Path("sectionId") sectionId: String): Observable<ApiResponse<SectionInfoJson>>

    /**
     * 分页查询帖子

     * @param pageNumber
     * *
     * @param limit
     * *
     * @param body       {"sectionId":"1e532b82-f0a0-47b7-9d15-d1e10b4f915c"} 指定板块
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/subject/filter/list/page/{pageNumber}/count/{limit}")
    fun subjectListByPage(
            @Path("pageNumber") pageNumber: Int, @Path("limit") limit: Int, @Body body: RequestBody): Observable<ApiResponse<List<SubjectInfoJson>>>


    /**
     * 用户是否能发帖

     * @param sectionId
     * *
     * @return
     */
    @GET("jaxrs/permission/subjectPublishable/{sectionId}")
    fun subjectPublishableInSection(
            @Path("sectionId") sectionId: String): Observable<ApiResponse<SubjectPublishPermissionCheckJson>>

    /**
     * 用户回帖权限

     * @param subjectId
     * *
     * @return
     */
    @GET("jaxrs/permission/subject/{subjectId}")
    fun replyAbleInSubject(@Path("subjectId") subjectId: String): Observable<ApiResponse<SubjectReplyPermissionCheckJson>>


    /**
     * 上传附件

     * @param body
     * *
     * @return
     */
    @Multipart
    @POST("jaxrs/attachment/upload/subject/{subjectId}")
    fun uploadBBSSubjectAttachment(@Part body: MultipartBody.Part, @Part("site") site: RequestBody, @Path("subjectId") subjectId: String): Observable<ApiResponse<IdData>>

    /**
     * 发表帖子

     * @param body SubjectPublishFormJson
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/user/subject")
    fun publishSubject(@Body body: RequestBody): Observable<ApiResponse<IdData>>

    /**
     * 查询回复详细
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/reply/{id}")
    fun getSubjectReplyInfo(@Path("id") id: String): Observable<ApiResponse<SubjectReplyInfoJson>>


    /**
     * 回复
     * @param body ReplyFormJson
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/user/reply")
    fun publishReply(@Body body: RequestBody): Observable<ApiResponse<IdData>>


    /**
     * 主题附件列表
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/subjectattach/list/subject/{id}")
    fun getSubjectAttachList(@Path("id") id: String): Observable<ApiResponse<List<BBSSubjectAttachmentJson>>>



}