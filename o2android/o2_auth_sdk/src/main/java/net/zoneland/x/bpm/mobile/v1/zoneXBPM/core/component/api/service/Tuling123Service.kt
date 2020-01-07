package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.TulingPostData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.TulingResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import rx.Observable

/**
 * Created by fancyLou on 17/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


interface Tuling123Service{


    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("api")
    fun api(@Body body: TulingPostData):Observable<TulingResponse>

}