package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import rx.functions.Action1

/**
 * Created by fancy on 2017/6/7.
 */

class ResponseHandler<T>( val onHandler: (T) -> Unit) : Action1<ApiResponse<T>>  {

    override fun call(t: ApiResponse<T>) {
        onHandler(t.data)
    }
}
