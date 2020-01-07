package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.HotPictureBase64Data
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.HotPictureOutData
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface HotpicAssembleControlService {

    /**
     * 查询热图列表
     * @param page
     * *
     * @param count
     * *
     * @param body  过滤条件｛  application: 应用名称：CMS|BBS等等.  infoId: 信息ID .  title: 信息标题，模糊查询.｝
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @PUT("jaxrs/user/hotpic/filter/list/page/{page}/count/{count}")
    fun findHotPictureList(@Path("page") page: Int, @Path("count") count: Int, @Body
    body: RequestBody): Observable<ApiResponse<List<HotPictureOutData>>>


    /**
     * 获取热图的base64字符串
     * @param id
     * *
     * @return
     */
    @GET("jaxrs/user/hotpic/{id}")
    fun loadPicBase64(@Path("id") id: String): Observable<ApiResponse<HotPictureBase64Data>>
}