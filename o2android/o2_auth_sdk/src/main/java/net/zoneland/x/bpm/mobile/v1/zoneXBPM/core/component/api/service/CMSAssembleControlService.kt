package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSApplicationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentAttachmentJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface CMSAssembleControlService {


    /**
     * 查询有权限发布的应用和分类
     */
    @GET("jaxrs/appinfo/get/user/publish/{appId}")
    fun canPublishCategories(@Path("appId") appId: String): Observable<ApiResponse<CMSApplicationInfoJson>>


    /**
     * 查询草稿
     * @param filter {"categoryIdList":["36783507-3109-4701-a1bd-487e12340af5"],"creatorList":["楼国栋@louguodong@P"],"documentType":"全部"}
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/document/draft/list/(0)/next/1")
    fun findDocumentDraftListWithCategory(@Body filter: RequestBody): Observable<ApiResponse<List<CMSDocumentInfoJson>>>


    /**
     * 保存文档
     * @param body 启动的时候先创建一个草稿： {
                "isNewDocument":true,
                "title":"测试文档qqqq",
                "creatorIdentity":"楼国栋@eaf80580-8ec7-43fc-b555-8863b47efa5f@I",
                "appId":"fbc9c933-3dc4-4087-9b95-1ada4e30e1c9",
                "categoryId":"cd29c6bc-e6f2-4987-bf39-d74db3b666e4",
                "docStatus":"draft",
                "categoryName":"qqqq",
                "categoryAlias":"temp-qqqq"}
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/document")
    fun documentPost(@Body body: RequestBody): Observable<ApiResponse<IdData>>

    /**
     * 内容管理应用列表
     * @return
     */
    @GET("jaxrs/appinfo/list/user/view")
    fun applicationList(): Observable<ApiResponse<List<CMSApplicationInfoJson>>>

    /**
     * 根据应用id查询 该应用下的分类列表
     */
    @GET("jaxrs/categoryinfo/list/publish/app/{appId}")
    fun findCategorysByAppId(@Path("appId") appId: String): Observable<ApiResponse<List<CMSCategoryInfoJson>>>


    /**
     * 获取document列表
     * @param filter   appIdList（专栏id）  categoryIdList(分类id)
     * *
     * @param lastId
     * *
     * @param count
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/document/filter/list/{lastId}/next/{count}")
    fun filterDocumentList(@Body filter: RequestBody, @Path("lastId") lastId: String, @Path("count") count: Int): Observable<ApiResponse<List<CMSDocumentInfoJson>>>


    /**
     * 获取附件对象
     * @param attachId 附件id
     * @param documentId 文档id
     */
    @GET("jaxrs/fileinfo/{attachId}/document/{documentId}")
    fun getDocumentAttachment(@Path("attachId") attachId: String, @Path("documentId") documentId: String): Observable<ApiResponse<CMSDocumentAttachmentJson>>

    /**
     * 获取文章的附件列表
     * @param docId
     * *
     * @return
     */
    @GET("jaxrs/fileinfo/list/document/{docId}")
    fun getDocumentAttachList(@Path("docId") docId: String): Observable<ApiResponse<List<CMSDocumentAttachmentJson>>>


    /**
     * 附件上传
     * @param body
     * *
     * @param site
     * *
     * @param docId 文档id
     */
    @Multipart
    @POST("jaxrs/fileinfo/upload/document/{docId}")
    fun uploadAttachment(@Part body: MultipartBody.Part, @Part("site") site: RequestBody, @Path("docId")docId : String): Observable<ApiResponse<IdData>>

    /**
     * 替换附件
     * @param body
     * *
     * @param attachmentId
     * *
     * @param docId
     */
    @Multipart
    @POST("jaxrs/fileinfo/update/document/{docId}/attachment/{attachmentId}")
    fun replaceAttachment(@Part body: MultipartBody.Part, @Path("attachmentId") attachmentId: String, @Path("docId") docId: String): Observable<ApiResponse<IdData>>


}