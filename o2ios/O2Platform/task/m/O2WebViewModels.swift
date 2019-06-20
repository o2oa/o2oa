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


class O2WebViewBaseMessage<T : HandyJSON>: HandyJSON {
    var callback: String!
    var type: String!
    var data:T?
    
    required init() {}
}


class O2NotificationMessage<T : HandyJSON>: HandyJSON {
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

class O2NotificationAlertMessage: HandyJSON {
    var message: String!
    var title: String!
    var buttonName: String!
    
    required init() {}
}
class O2NotificationConfirm: HandyJSON {
    var message: String!
    var title: String!
    var buttonLabels: [String]!
    
    required init() {}
}
class O2NotificationActionSheet: HandyJSON {
    var title: String!
    var cancelButton: String!
    var otherButtons: [String]!
    
    required init() {}
}
class O2NotificationToast: HandyJSON {
    var duration: Int!
    var message: String!
    
    required init() {}
}
class O2NotificationLoading: HandyJSON {
    var text: String!
    
    required init() {}
}

class O2UtilPicker: HandyJSON {
    var value: String!
    var startDate: String!
    var endDate: String!
    
    required init() {}
}

class O2UtilNavigation: HandyJSON {
    var title: String!
    required init() {}
}

struct O2UtilPhoneInfo: HandyJSON {
    var screenWidth: String?
    var screenHeight: String?
    var brand:String?
    var model: String?
    var version: String?
    var netInfo: String?
    var operatorType: String?
}
