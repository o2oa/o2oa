package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import rx.Observable


/**
 * Created by fancy on 2017/6/5.
 */

interface PgyUpdateApiService {
    @Streaming
    @GET
    fun apkDownload(@Url url: String): Observable<Response<ResponseBody>>
}