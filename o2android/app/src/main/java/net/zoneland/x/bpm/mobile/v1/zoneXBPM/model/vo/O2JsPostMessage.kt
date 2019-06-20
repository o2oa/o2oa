package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancyLou on 2019-05-06.
 * Copyright © 2019 O2. All rights reserved.
 */



class O2JsPostMessage<T> (
        var callback: String?,
        var type: String? ,
        var data:T?

)

class O2UtilDatePickerMessage(var value: String?,
                       var startDate: String?,
                       var endDate: String?)

class O2UtilNavigationMessage(var title: String?)