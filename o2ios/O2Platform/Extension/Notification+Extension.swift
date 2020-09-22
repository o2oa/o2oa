//
//  Notification+Extension.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/18.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

enum OONotification:String {
    //登录相关
    case login
    case logout
    case bindCompleted
    
    // 考勤管理相关
    case location
    case newWorkPlace
    case staticsTotal
    
    //重载门户webview
    case reloadPortal
    
    //websocket使用
    case websocket
    
    //日程管理Main中使用
    case calendarIds
    
    
    
    var stringValue:String {
        return "OOK" + rawValue
    }
    
    var notificationName:Notification.Name {
        return Notification.Name(stringValue)
    }
}

extension NotificationCenter {
    static func post(customeNotification name: OONotification, object: Any? = nil) {
        NotificationCenter.default.post(name: name.notificationName, object: object)
    }
}
