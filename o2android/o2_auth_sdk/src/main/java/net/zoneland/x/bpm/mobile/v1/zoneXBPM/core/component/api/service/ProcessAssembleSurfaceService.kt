package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ValueData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AttachmentInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.ProcessWOIdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface ProcessAssembleSurfaceService {

    /**
     * 获取应用列表
     * @return
     */
    @GET("jaxrs/application/list")
    fun getApplicationList(): Observable<ApiResponse<List<ApplicationData>>>

    /**
     * 获取应用下的流程
     * @param appId
     * *
     * @return
     */
    @GET("jaxrs/process/list/application/{appId}")
    fun getApplicationProcess(@Path("appId") appId: String): Observable<ApiResponse<List<ProcessInfoData>>>

    /**
     * 获取应用下的流程
     * @param filter 可启动流程终端类型,可选值 client,mobile,all
     */
    @POST("jaxrs/process/list/application/{appId}/filter")
    fun getApplicationProcessFilter(@Path("appId") appId: String, @Body filter: ApplicationProcessFilter): Observable<ApiResponse<List<ProcessInfoData>>>

    /**
     * 获取应用列表 包含应用下的流程数据
     * @return
     */
    @GET("jaxrs/application/list/complex")
    fun getApplicationProcessList(): Observable<ApiResponse<List<ApplicationWithProcessData>>>


    /**
     * 获取当前流程的可用身份列表
     */
    @GET("jaxrs/process/list/available/identity/process/{processId}")
    fun availableIdentityWithProcess(@Path("processId") processId:String): Observable<ApiResponse<List<ProcessWOIdentityJson>>>

    /**
     * 保存工作
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/data/work/{workId}")
    fun saveTaskForm(@Body body: RequestBody, @Path("workId") workId: String): Observable<ApiResponse<IdData>>

    /**
     * 删除工作
     */
    @DELETE("jaxrs/work/{workId}")
    fun deleteWorkForm(@Path("workId") workId: String): Observable<ApiResponse<IdData>>

    /**
     * 获取工作对象
     * 如果返回500错误就是工作已经结束了
     */
    @GET("jaxrs/work/{workId}")
    fun getWorkInfo(@Path("workId") workId: String): Observable<ApiResponse<WorkInfoResData>>

    /**
     * 已阅列表
     * @param lastId
     * *
     * @param limit
     * *
     * @return
     */
    @GET("jaxrs/readcompleted/list/{lastId}/next/{limit}")
    fun getReadCompleteListByPage(@Path("lastId") lastId: String, @Path("limit") limit: Int): Observable<ApiResponse<List<ReadCompleteData>>>

    @GET("jaxrs/readcompleted/list/{lastId}/next/{limit}/application/{applicationId}")
    fun getReadCompleteListByPageWithApplication(@Path("lastId") lastId: String, @Path("limit") limit: Int, @Path("applicationId") applicationId: String): Observable<ApiResponse<List<ReadCompleteData>>>

    /**
     * 获取已阅的详细信息
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/readcompleted/{id}/reference")
    fun getReadCompleteInfo(@Path("id") id: String): Observable<ApiResponse<ReadCompleteInfoData>>

    /**
     * 设为已阅
     * @param id
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/read/{id}/processing")
    fun setReadComplete(@Path("id") id: String, @Body readBody: RequestBody): Observable<ApiResponse<IdData>>

    /**
     * 待阅列表
     * @param lastId
     * *
     * @param limit
     * *
     * @return
     */
    @GET("jaxrs/read/list/{lastId}/next/{limit}")
    fun getReadListByPage(@Path("lastId") lastId: String, @Path("limit") limit: Int): Observable<ApiResponse<List<ReadData>>>

    @GET("jaxrs/read/list/{lastId}/next/{limit}/application/{applicationId}")
    fun getReadListByPageWithApplication(@Path("lastId") lastId: String, @Path("limit") limit: Int, @Path("applicationId") applicationId: String): Observable<ApiResponse<List<ReadData>>>

    /**
     * 待阅详细信息
     * @param id read id
     * *
     * @return
     */
    @GET("jaxrs/read/{id}/reference")
    fun getReadInfo(@Path("id") id: String): Observable<ApiResponse<ReadInfoData>>

    /**
     * 待阅信息
     * @param readId
     * *
     * @return
     */
    @GET("jaxrs/read/{readId}")
    fun getRead(@Path("readId") readId: String): Observable<ApiResponse<ReadData>>

    /**
     * 已办列表
     * @param lastId
     * *
     * @param limit
     * *
     * @return
     */
    @GET("jaxrs/taskcompleted/list/{lastId}/next/{limit}")
    fun getTaskCompleteListByPage(@Path("lastId") lastId: String, @Path("limit") limit: Int): Observable<ApiResponse<List<TaskCompleteData>>>

    @GET("jaxrs/taskcompleted/list/{lastId}/next/{limit}/application/{applicationId}")
    fun getTaskCompleteListByPageWithApplication(@Path("lastId") lastId: String, @Path("limit") limit: Int, @Path("applicationId") applicationId: String): Observable<ApiResponse<List<TaskCompleteData>>>

    /**
     * 搜索已办
     * @param lastId
     * *
     * @param limit
     * *
     * @param body {"key":"关键字"}
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/taskcompleted/list/{lastId}/next/{limit}/filter")
    fun searchTaskCompleteListByPage(@Path("lastId") lastId: String, @Path("limit") limit: Int, @Body body: RequestBody): Observable<ApiResponse<List<TaskCompleteData>>>

    /**
     * 已办详细信息
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/taskcompleted/{id}/reference")
    fun getTaskCompleteInfo(@Path("id") id: String): Observable<ApiResponse<TaskCompleteInfoData>>



    /**
     * 已办详细信息
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/taskcompleted/{id}/reference/control")
    fun getTaskCompleteInfoWithControl(@Path("id") id: String): Observable<ApiResponse<TaskCompleteInfoDataWithControl>>

    /**
     *
     */
    @GET("jaxrs/taskcompleted/list/count/application")
    fun getTaskCompletedApplicationList(): Observable<ApiResponse<List<TaskApplicationData>>>

    /**
     * 获取应用列表
     * @return
     */
    @GET("jaxrs/task/list/count/application")
    fun getTaskApplicationList(): Observable<ApiResponse<List<TaskApplicationData>>>

    /**
     * 分页获取任务列表
     * @param lastId (0) 第一条开始 、 传入最后一个任务id就是从这条任务开始
     * *
     * @param limit 每页展现数量
     * *
     * @return
     */
    @GET("jaxrs/task/list/{lastId}/next/{limit}")
    fun getTaskListByPage(@Path("lastId") lastId: String, @Path("limit") limit: Int): Observable<ApiResponse<List<TaskData>>>

    /**
     * 应用内分页获取任务列表
     * @param lastId (0) 第一条开始 、 传入最后一个任务id就是从这条任务开始
     * *
     * @param limit 每页展现数量
     * *
     * @param applicationId 应用id
     * *
     * @return
     */
    @GET("jaxrs/task/list/{lastId}/next/{limit}/application/{applicationId}")
    fun getTaskListByPageWithApplication(@Path("lastId") lastId: String, @Path("limit") limit: Int, @Path("applicationId") applicationId: String): Observable<ApiResponse<List<TaskData>>>

    /**
     * 继续流转
     * @param taskBody {'routeName': routeName, 'opinion': opinion}
     * @param taskId 任务id
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/task/{taskId}/processing")
    fun postTask(@Body taskBody: RequestBody, @Path("taskId") taskId: String): Observable<ApiResponse<WorkPostResult>>


    /**
     * 神经网络自动处理任务
     * @param taskBody type:神经网络类型 可以为空
     * @param taskId 任务id
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/task/{taskId}/processing/neural")
    fun postTaskNeural(@Body taskBody: RequestBody, @Path("taskId") taskId: String): Observable<ApiResponse<TaskNeuralResponseData>>

    /**
     * 获取task
     * @param taskId
     * *
     * @return
     */
    @GET("jaxrs/task/{taskId}")
    fun getTask(@Path("taskId") taskId: String): Observable<ApiResponse<TaskData>>



    /**
     * 启动草稿
     * @param processId
     * *
     * @param body
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/draft/process/{processId}")
    fun startDraft(@Path("processId") processId: String, @Body body: ProcessStartBo): Observable<ApiResponse<ProcessDraftData>>

    /**
     * 启动流程
     * @param processId
     * *
     * @param body
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/work/process/{processId}")
    fun startProcess(@Path("processId") processId: String, @Body body: ProcessStartBo): Observable<ApiResponse<List<ProcessWorkData>>>


    /**
     * 启动流程
     * @param processId
     * *
     * @param body
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/work/process/{processId}")
    fun startProcessForCms(@Path("processId") processId: String, @Body body: ProcessStartCmsBo): Observable<ApiResponse<List<ProcessWorkData>>>

    /**
     * 已办撤回
     * @param workId
     * *
     * @return
     */
    @PUT("jaxrs/work/{workId}/retract")
    fun retractWork(@Path("workId") workId: String): Observable<ApiResponse<IdData>>


    /**
     * 获取附件信息
     * @param attachId
     * *
     * @param workId
     * *
     * @return
     */
    @GET("jaxrs/attachment/{attachId}/work/{workId}")
    fun getWorkAttachmentInfo(@Path("attachId") attachId: String, @Path("workId") workId: String): Observable<ApiResponse<AttachmentInfo>>


    /**
     * 获取附件信息
     * @param attachId
     * *
     * @param workCompletedId
     * *
     * @return
     */
    @GET("jaxrs/attachment/{attachId}/workcompleted/{workCompletedId}")
    fun getWorkCompletedAttachmentInfo(@Path("attachId") attachId: String, @Path("workCompletedId") workCompletedId: String): Observable<ApiResponse<AttachmentInfo>>

    /**
     * 上传附件
     * @param body
     * *
     * @param site
     * *
     * @param workId
     */
    @Multipart
    @POST("jaxrs/attachment/upload/work/{workId}")
    fun uploadAttachment(@Part body: MultipartBody.Part, @Part("site") site: RequestBody, @Path("workId") workId: String): Observable<ApiResponse<IdData>>

    /**
     * 替换附件
     * @param body
     * *
     * @param attachmentId
     * *
     * @param workId
     */
    @Multipart
    @PUT("jaxrs/attachment/update/{attachmentId}/work/{workId}")
    fun replaceAttachment(@Part body: MultipartBody.Part, @Path("attachmentId") attachmentId: String, @Path("workId") workId: String): Observable<ApiResponse<IdData>>

//    /**
//     * 附件下载
//     * @param attachId
//     * *
//     * @return
//     */
//    @GET("jaxrs/attachment/download/{attachId}/work/{workId}/stream")
//    @Headers("Content-Type: application/json; charset=utf-8")
//    fun downloadWorkAttachment(@Path("attachId") attachId: String, @Path("workId") workId: String): Call<ResponseBody>
//
//    /**
//     * 附件下载
//     * @param attachId
//     * *
//     * @return
//     */
//    @GET("jaxrs/attachment/download/{attachId}/workcompleted/{workCompletedId}/stream")
//    @Headers("Content-Type: application/json; charset=utf-8")
//    fun downloadWorkCompletedAttachment(@Path("attachId") attachId: String, @Path("workCompletedId") workCompletedId: String): Call<ResponseBody>

    /**
     * 测试附件是否可用
     * @param attachId
     * *
     * @return
     */
    @GET("jaxrs/attachment/{attachId}/available")
    fun checkAttachmentAvailable(@Path("attachId") attachId: String): Observable<ApiResponse<ValueData>>
}