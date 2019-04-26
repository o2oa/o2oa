//
//  O2WebViewModels.swift
//  O2Platform
//
//  Created by FancyLou on 2019/4/18.
//  Copyright © 2019 zoneland. All rights reserved.
//

import HandyJSON


class O2WebViewUploadImage: HandyJSON {
    var mwfId: String!
    var callback: String!
    var fileId: String!
    var referencetype: String!
    var reference: String!
    var scale: Int!
    
    required init() {}
}

class O2NotificationMessage<T>: HandyJSON {
    var callback: String!
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
    var type: String!
    var data:T?
    
    required init() {}
}

class O2NotificationAlertMessage {
    var message: String!
    var title: String!
    var buttonName: String!
}
