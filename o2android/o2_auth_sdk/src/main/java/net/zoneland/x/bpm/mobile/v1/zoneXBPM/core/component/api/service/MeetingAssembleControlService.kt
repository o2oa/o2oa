package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.BuildingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.RoomInfoJson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface MeetingAssembleControlService {

    /**
     * 获取所有大楼
     * 包含大楼内会议室的列表
     * @return
     */
    @GET("jaxrs/building/list")
    fun listBuildings(): Observable<ApiResponse<List<BuildingInfoJson>>>

    /**
     * 根据start 和 completed 查询所有的会议室情况 是否空闲
     * @param start yyyy-MM-dd HH:mm
     * *
     * @param completed yyyy-MM-dd HH:mm
     * *
     * @return
     */
    @GET("jaxrs/building/list/start/{start}/completed/{completed}")
    fun listBuildingsByTime(@Path("start") start: String, @Path("completed") completed: String): Observable<ApiResponse<List<BuildingInfoJson>>>

    /**
     * 列示我参与的指定月份的会议，或者被邀请，或者是申请人.
     * 按月
     * @param year
     * *
     * @param month
     * *
     * @return
     */
    @GET("jaxrs/meeting/list/year/{year}/month/{month}")
    fun findMyMeetingByMonth(@Path("year") year: String, @Path("month") month: String): Observable<ApiResponse<List<MeetingInfoJson>>>

    /**
     * 管理员权限 全部会议
     * 列示我参与的指定月份的会议，或者被邀请，或者是申请人.
     * 按月
     * @param year
     * *
     * @param month
     * *
     * @return
     */
    @GET("jaxrs/meeting/list/year/{year}/month/{month}/all")
    fun findMyMeetingByMonthAll(@Path("year") year: String, @Path("month") month: String): Observable<ApiResponse<List<MeetingInfoJson>>>
    /**
     * 列示我参与的指定日期的会议，或者被邀请，或者是申请人.
     * 按日
     * @param year
     * *
     * @param month
     * *
     * @param day
     * *
     * @return
     */
    @GET("jaxrs/meeting/list/year/{year}/month/{month}/day/{day}")
    fun findMyMeetingByDay(@Path("year") year: String, @Path("month") month: String, @Path("day") day: String): Observable<ApiResponse<List<MeetingInfoJson>>>
    /**
     * 管理员权限 全部会议
     * 列示我参与的指定日期的会议，或者被邀请，或者是申请人.
     * 按日
     * @param year
     * *
     * @param month
     * *
     * @param day
     * *
     * @return
     */
    @GET("jaxrs/meeting/list/year/{year}/month/{month}/day/{day}/all")
    fun findMyMeetingByDayAll(@Path("year") year: String, @Path("month") month: String, @Path("day") day: String): Observable<ApiResponse<List<MeetingInfoJson>>>

    /**
     * 等待我确认是否参加的会议.
     * @return
     */
    @GET("jaxrs/meeting/list/wait/accept")
    fun myWaitAcceptMeetingList(): Observable<ApiResponse<List<MeetingInfoJson>>>

    /**
     * 接受会议邀请
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/meeting/{id}/accept")
    fun acceptMeeting(@Path("id") id: String): Observable<ApiResponse<IdData>>

    /**
     * 拒绝会议邀请
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/meeting/{id}/reject")
    fun rejectMeeting(@Path("id") id: String): Observable<ApiResponse<IdData>>


    @GET("jaxrs/room/{id}")
    fun getRoomById(@Path("id") id: String): Observable<ApiResponse<RoomInfoJson>>

    /**
     * 申请会议
     * @param taskBody
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/meeting")
    fun saveMeeting(@Body taskBody: RequestBody): Observable<ApiResponse<IdData>>

    /**
     * 保存会议材料
     * @param id
     * *
     * @return
     */
    @Multipart
    @POST("jaxrs/attachment/meeting/{meetingId}/upload/{summary}")
    fun saveMeetingFile(@Part body: MultipartBody.Part,@Path("meetingId") id: String,
                        @Path("summary") summary: Boolean = false): Observable<ApiResponse<IdData>>

    /**
     * 删除会议材料
     * @param id
     * *
     * @return
     */
    @DELETE("jaxrs/attachment/{id}")
    fun deleteMeetingFile(@Path("id") id: String): Observable<ApiResponse<IdData>>


    /**
     * 更新会议
     * @param taskBody
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/meeting/{id}")
    fun updateMeeting(@Body taskBody: RequestBody, @Path("id") id: String): Observable<ApiResponse<IdData>>

    /**
     * 取消会议
     * @param id
     * *
     * @return
     */
    @DELETE("jaxrs/meeting/{id}")
    fun deleteMeeting(@Path("id") id: String): Observable<ApiResponse<IdData>>


    /**
     * 获取会议信息
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/meeting/{id}")
    fun getMeetingById(@Path("id") id: String): Observable<ApiResponse<MeetingInfoJson>>

    /**
     * 获取全部会议室列表
     */

    @GET("jaxrs/room/list")
    fun getAllMeetingRoomList(): Observable<ApiResponse<List<RoomInfoJson>>>

    /**
     * 我发起的未开始的会议.
     * @return
     */
    @GET("jaxrs/meeting/list/applied/wait")
    fun myOriginatorMeetingList(): Observable<ApiResponse<List<MeetingInfoJson>>>

    /**
     *根据大楼ID获取大楼信息
     */
    @GET("jaxrs/building/{id}")
    fun getBuildingDetail(@Path("id") id : String): Observable<ApiResponse<BuildingInfoJson>>
}