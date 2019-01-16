//
//  App.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import UIKit
import ObjectMapper




class O2App:Mappable {
    var title:String?
    var appId:String?
    var storyBoard:String?
    var vcName:String?
    var segueIdentifier:String?
    var normalIcon:String?
    var selectedIcon:String?
    var customParameter:[String:AnyObject]?
    var order = 0
    var mainOrder = 0
    
    init(title:String?,appId:String?,storyBoard:String?,vcName:String?,segueIdentifier:String?,normalIcon:String?,selectedIcon:String?,customParameter:[String:AnyObject]?=nil,order:Int,mainOrder:Int){
        self.title  = title
        self.appId = appId
        self.storyBoard = storyBoard
        self.vcName = vcName
        self.segueIdentifier = segueIdentifier
        self.normalIcon = normalIcon
        self.selectedIcon = selectedIcon
        self.customParameter = customParameter
        self.order = order
        self.mainOrder = mainOrder
    }
    
    init() {
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        title <- map["title"]
        appId <- map["appId"]
        storyBoard <- map["storyBoard"]
        vcName <- map["vcName"]
        segueIdentifier <- map["segueIdentifier"]
        normalIcon <- map["normalIcon"]
        selectedIcon <- map["selectedIcon"]
        customParameter <- map["customParameter"]
        order <- map["order"]
        mainOrder <- map["mainOrder"]
    }
}

class O2App2:Mappable {
    
    var enable : Bool?
    var id : Int?
    var key : String?
    var name : String?
    
    required init?(map: Map){}
    
    func mapping(map: Map)
    {
        enable <- map["enable"]
        id <- map["id"]
        key <- map["key"]
        name <- map["name"]
        
    }
}

class O2PortalApp : Mappable{
    
    var alias : String?
    var createTime : String?
    var creatorPerson : String?
    var descriptionField : String?
    var firstPage : String?
    var id : String?
    var lastUpdatePerson : String?
    var lastUpdateTime : String?
    var name : String?
    var portalCategory : String?
    var updateTime : String?
    
    required init?(map: Map){}
    
    func mapping(map: Map)
    {
        alias <- map["alias"]
        createTime <- map["createTime"]
        creatorPerson <- map["creatorPerson"]
        descriptionField <- map["description"]
        firstPage <- map["firstPage"]
        id <- map["id"]
        lastUpdatePerson <- map["lastUpdatePerson"]
        lastUpdateTime <- map["lastUpdateTime"]
        name <- map["name"]
        portalCategory <- map["portalCategory"]
        updateTime <- map["updateTime"]
        
    }
    
}

class O2AppUtil {
    
    static var apps:[O2App] {
        get {
            let app1 = O2App(title: "云盘", appId:"yunpan", storyBoard: "cloudStorage", vcName: nil, segueIdentifier: "showFileCloudSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app2 = O2App(title: "会议管理", appId:"meeting", storyBoard: "meeting", vcName: nil, segueIdentifier: "showMeetingSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app3 = O2App(title: "考勤管理", appId:"attendance", storyBoard: "checkin", vcName: nil, segueIdentifier: "showIcSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app4 = O2App(title: "BBS", appId:"bbs", storyBoard: "bbs", vcName: nil, segueIdentifier: "showBBSSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app5 = O2App(title: "内容管理", appId:"cms", storyBoard: "information", vcName: nil, segueIdentifier: "showCMSSegue", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app6 = O2App(title: "待办", appId:"task", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app10 = O2App(title: "已办", appId:"taskcompleted", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app7 = O2App(title: "待阅", appId:"read", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app11 = O2App(title: "已阅", appId:"readcompleted", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app8 = O2App(title: "语音助手", appId: "o2ai", storyBoard: "ai", vcName: nil, segueIdentifier: "", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            let app9 = O2App(title: "日程安排", appId: "calendar", storyBoard: "calendar", vcName: nil, segueIdentifier: "", normalIcon: "", selectedIcon: "", order: 0, mainOrder: 0)
            return [app1,app2,app3,app4,app5,app6, app10, app7, app11, app8, app9]
        }
    }
    
    static var defaultMainApps = [
     O2App(title: "待办", appId:"task", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "icon_task", selectedIcon: "icon_task", order: 0, mainOrder: 0),
     O2App(title: "已办", appId:"taskcompleted", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "icon_taskcompleted", selectedIcon: "icon_taskcompleted", order: 0, mainOrder: 0),
     O2App(title: "待阅", appId:"read", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "icon_read", selectedIcon: "icon_read", order: 0, mainOrder: 0),
     O2App(title: "已阅", appId:"readcompleted", storyBoard: "task", vcName: "todoTask", segueIdentifier: nil, normalIcon: "icon_readcompleted", selectedIcon: "icon_readcompleted", order: 0, mainOrder: 0)
    ]

}
