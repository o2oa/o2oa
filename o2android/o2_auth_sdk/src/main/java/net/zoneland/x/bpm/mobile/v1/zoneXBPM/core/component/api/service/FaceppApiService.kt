package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.face.FaceSearchResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import rx.Observable

/**
 * Created by fancyLou on 2018/10/11.
 * Copyright © 2018 O2. All rights reserved.
 */


interface FaceppApiService {

    /**
     * http://dev.o2oa.io:8888/x_faceset_control/search/dev_o2oa_io
     */
    @Multipart
    @POST("search/{faceset}")
    fun searchFace(@Part body: MultipartBody.Part,  @Path("faceset") faceset: String): Observable<FaceSearchResponse>

}