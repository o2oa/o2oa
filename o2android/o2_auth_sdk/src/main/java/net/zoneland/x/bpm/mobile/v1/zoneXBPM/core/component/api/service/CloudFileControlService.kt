package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface CloudFileControlService {


    /**
     * 顶层文件列表
     */
    @GET("jaxrs/attachment2/list/top")
    fun listFileTop() : Observable<ApiResponse<List<FileJson>>>

    /**
     * 文件夹下的文件列表
     */
    @GET("jaxrs/attachment2/list/folder/{folderId}")
    fun listFileByFolderId(@Path("folderId") folderId: String): Observable<ApiResponse<List<FileJson>>>


    /**
     * 分页查询文件列表
     * @param typeBody 分类
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/attachment2/list/type/{page}/size/{count}")
    fun listFileByPage(@Path("page") page: Int, @Path("count") count: Int,
                       @Body typeBody: CloudDiskPageForm): Observable<ApiResponse<List<FileJson>>>

    /**
     * 顶层文件夹列表
     */
    @GET("jaxrs/folder2/list/top")
    fun listFolderTop(): Observable<ApiResponse<List<FolderJson>>>

    /**
     * 文件夹下的文件夹列表
     */
    @GET("jaxrs/folder2/list/{folderId}")
    fun listFolderByFolderId(@Path("folderId") folderId: String): Observable<ApiResponse<List<FolderJson>>>


    /**
     * 创建文件夹
     *
     * @param json name ，superior上级id (为空就是顶级)
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/folder2")
    fun createFolder(@Body json: Map<String, String>): Observable<ApiResponse<IdData>>


    /**
     * 上传文件
     * @param body
     *
     * @param folderId 顶级目录用：O2.FIRST_PAGE_TAG
     */
    @Multipart
    @POST("jaxrs/attachment2/upload/folder/{folderId}")
    fun uploadFile2Folder(@Part body: MultipartBody.Part, @Path("folderId") folderId: String): Observable<ApiResponse<IdData>>


    /**
     * 更新文件信息
     * @param item
     * *
     * @param id
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/attachment2/{id}")
    fun updateFile(@Body item: FileJson, @Path("id") id: String): Observable<ApiResponse<IdData>>

    /**
     * 删除文件
     * @param id
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @DELETE("jaxrs/attachment2/{id}")
    fun deleteFile(@Path("id") id: String): Observable<ApiResponse<IdData>>

    /**
     * 获取文件
     */
    @GET("jaxrs/attachment2/{id}")
    fun getFile(@Path("id") id: String): Observable<ApiResponse<FileJson>>


    /**
     * 重命名文件夹
     * @param folderId
     * *
     * @param folder
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/folder2/{folderId}")
    fun updateFolder(@Path("folderId") folderId: String, @Body folder: FolderJson): Observable<ApiResponse<IdData>>

    /**
     * 删除文件夹
     * @param folderId
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @DELETE("jaxrs/folder2/{folderId}")
    fun deleteFolder(@Path("folderId") folderId: String): Observable<ApiResponse<IdData>>


    /**
     * 分享
     */
    @POST("jaxrs/share")
    fun share(@Body form: CloudDiskShareForm): Observable<ApiResponse<IdData>>
}