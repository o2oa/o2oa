package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

/**
 * Created by fancyLou on 2019-04-29.
 * Copyright © 2019 O2. All rights reserved.
 */


 class O2NotificationMessage<T> (
        var callback: String?,
        /**
        alert
        confirm
        prompt
        vibrate
        toast
        actionSheet
        showLoading
        hideLoading
         **/
        var type: String? ,
        var data:T?

)

 class O2NotificationAlertMessage(var message: String?,
                                      var title: String?,
                                      var buttonName: String?)
 class O2NotificationConfirm( var message: String?,
                                  var title: String?,
                                  var buttonLabels: List<String>?)
 class O2NotificationActionSheet(var title: String?,
                                     var cancelButton: String?,
                                     var otherButtons: List<String>?)
 class O2NotificationToast(var duration: Int?,
                               var message: String?)
 class O2NotificationLoading ( var text: String?)
