package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
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

interface FileAssembleControlService {



    /**
     * 上传文件
     * @param body
     * *
     * @param folderName
     */
    @Multipart
    @POST("jaxrs/attachment/upload/folder/{folderId}")
    fun uploadFile2Folder(@Part body: MultipartBody.Part, @Path("folderId") folderId: String): Observable<ApiResponse<IdData>>

    /**
     * 上传文件
     * @param body
     */
    @Multipart
    @POST("jaxrs/attachment/upload/folder/{folderId}")
    fun uploadFile2Root(@Part body: MultipartBody.Part,
                        @Path("folderId") folderId: String = O2.FIRST_PAGE_TAG): Observable<ApiResponse<IdData>>

    /**
     * 上传文件到特定的业务模块区域
     * @param referencetype 业务模块代号
     * @param reference 业务关联id
     * @param scale 缩放大小 比如800 ：宽高比不变 大的那条边会压缩到小于等于800
     */
    @Multipart
    @PUT("jaxrs/file/upload/referencetype/{referencetype}/reference/{reference}/scale/{scale}")
    fun uploadFile2ReferenceZone(@Part body: MultipartBody.Part,
                                 @Path("referencetype") referencetype: String,
                                 @Path("reference") reference: String,
                                 @Path("scale") scale: Int ): Observable<ApiResponse<IdData>>

    /**
     * 更新文件信息
     * @param item
     * *
     * @param id
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/attachment/{id}")
    fun updateFile(@Body item: FileJson, @Path("id") id: String): Observable<ApiResponse<IdData>>

    /**
     * 顶级文件列表
     * @return
     */
    @GET("jaxrs/complex/top")
    fun getFileTopList(): Observable<ApiResponse<YunpanJson>>

    /**
     * 获取某个文件夹下的文件列表

     * @param folderName
     * *
     * @return
     */
    @GET("jaxrs/complex/folder/{folderName}")
    fun getFileListByFolder(@Path("folderName") folderName: String): Observable<ApiResponse<YunpanJson>>


    /**
     * 创建文件夹
     * json key:superior value:上级id (为空就是顶级)
     * @param json
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/folder")
    fun createFolder(@Body json: Map<String, String>): Observable<ApiResponse<IdData>>


    /**
     * 重命名文件夹
     * @param folderId
     * *
     * @param folder
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/folder/{folderId}")
    fun reNameFolder(@Path("folderId") folderId: String, @Body folder: FolderJson): Observable<ApiResponse<IdData>>

    /**
     * 删除文件夹
     * @param folderName
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @DELETE("jaxrs/folder/{folderName}")
    fun deleteFolder(@Path("folderName") folderName: String): Observable<ApiResponse<IdData>>

    /**
     * 重命名文件
     * @param fileId
     * *
     * @param file
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/attachment/{fileId}")
    fun reNameFile(@Path("fileId") fileId: String, @Body file: FileJson): Observable<ApiResponse<IdData>>

    /**
     * 删除文件
     * @param fileName
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @DELETE("jaxrs/attachment/{fileName}")
    fun deleteFile(@Path("fileName") fileName: String): Observable<ApiResponse<IdData>>

    /**
     * 接收列表顶级目录
     * @return
     */
    @GET("jaxrs/editor/list")
    fun getReceiveTopList(): Observable<ApiResponse<List<CooperationFolderJson>>>

    /**
     * 接收列表
     * @param folder
     * *
     * @return
     */
    @GET("jaxrs/attachment/list/editor/{folder}")
    fun getReceiveFileList(@Path("folder") folder: String): Observable<ApiResponse<List<CooperationFileJson>>>

    /**
     * 分享列表顶级目录
     * @return
     */
    @GET("jaxrs/share/list")
    fun getShareTopList(): Observable<ApiResponse<List<CooperationFolderJson>>>


    /**
     * 分享列表
     * @param folder
     * *
     * @return
     */
    @GET("jaxrs/attachment/list/share/{folder}")
    fun getShareFileList(@Path("folder") folder: String): Observable<ApiResponse<List<CooperationFileJson>>>
}