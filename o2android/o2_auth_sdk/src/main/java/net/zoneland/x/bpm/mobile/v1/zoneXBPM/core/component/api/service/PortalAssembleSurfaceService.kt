package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.portal.PortalData
import retrofit2.http.GET
import rx.Observable

/**
 * Created by fancyLou on 2018/3/16.
 * Copyright © 2018 O2. All rights reserved.
 */


interface PortalAssembleSurfaceService {


    @GET("jaxrs/portal/list")
    fun portalList(): Observable<ApiResponse<List<PortalData>>>
}