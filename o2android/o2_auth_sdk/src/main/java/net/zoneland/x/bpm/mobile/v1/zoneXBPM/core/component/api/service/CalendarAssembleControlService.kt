package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.IdData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ValueData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ValueNumberData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.calendar.*
import retrofit2.http.*
import rx.Observable

/**
 * Created by fancyLou on 14/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


interface CalendarAssembleControlService {

    /**
     * 获取我的日历列表
     */
    @GET("jaxrs/calendar/list/my")
    fun myCalendarList(): Observable<ApiResponse<MyCalendarData>>

    /**
     * 获取事件列表
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/event/list/filter")
    fun filterCalendarEventList(@Body filter: CalendarEventFilterInfo):Observable<ApiResponse<CalendarEventResponseData>>

    /**
     * 日历新增
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/calendar")
    fun saveCalendar(@Body post:CalendarPostData):  Observable<ApiResponse<IdData>>

    /**
     * 日历广场
     */
    @GET("jaxrs/calendar/list/public")
    fun getPublicCalendarList(): Observable<ApiResponse<List<CalendarPostData>>>

    /**
     * 关注
     */
    @GET("jaxrs/calendar/follow/{id}")
    fun followCalendar(@Path("id") id: String): Observable<ApiResponse<ValueData>>

    /**
     * 取消关注
     */
    @GET("jaxrs/calendar/follow/{id}/cancel")
    fun followCalendarCancel(@Path("id") id: String): Observable<ApiResponse<ValueData>>



    /**
     * 获取日历对象
     */
    @GET("jaxrs/calendar/{id}")
    fun getCalendar(@Path("id") id: String): Observable<ApiResponse<CalendarPostData>>

    /**
     * 删除日历
     */
    @DELETE("jaxrs/calendar/{id}")
    fun deleteCalendar(@Path("id") id: String): Observable<ApiResponse<IdData>>

    /**
     * 保存日程事件
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/event")
    fun saveCalendarEvent(@Body event: CalendarEventInfoData): Observable<ApiResponse<IdData>>

    /**
     * 修改
     * jaxrs/event/update/single/3af8c0f5-49ac-4a24-9583-6d453a124096
     * @return id:
     *
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/event/update/single/{id}")
    fun updateCalendarEventSingle(@Path("id") id: String ,@Body event: CalendarEventInfoData): Observable<ApiResponse<ValueNumberData>>

    /**
     * 修改重复事件  之后
     * jaxrs/event/update/after/6dafa82b-993f-4443-bb7f-75f5fd29548b
     * @return value:18
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/event/update/after/{id}")
    fun updateCalendarEventAfter(@Path("id") id: String ,@Body event: CalendarEventInfoData): Observable<ApiResponse<ValueNumberData>>

    /**
     * 修改重复事件 全部
     * jaxrs/event/update/all/b7d877b4-38b1-4e5f-bcb6-a78ae6595379
     * @return value:18
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/event/update/all/{id}")
    fun updateCalendarEventAll(@Path("id") id: String ,@Body event: CalendarEventInfoData): Observable<ApiResponse<ValueNumberData>>

    /**
     * 删除单个日程
     */
    @DELETE("jaxrs/event/single/{id}")
    fun deleteCalendarEventSingle(@Path("id") id: String): Observable<ApiResponse<ValueNumberData>>

    /**
     * 删除重复日程的之后的日程
     */
    @DELETE("jaxrs/event/after/{id}")
    fun deleteCalendarEventAfter(@Path("id") id: String): Observable<ApiResponse<ValueNumberData>>

    /**
     * 删除重复日程的全部日程
     */
    @DELETE("jaxrs/event/all/{id}")
    fun deleteCalendarEventAll(@Path("id") id: String): Observable<ApiResponse<ValueNumberData>>
}
