////
////  OOFormModels.swift
////  o2app
////
////  Created by 刘振兴 on 2018/1/25.
////  Copyright © 2018年 zone. All rights reserved.
////
//
//import UIKit
//import HandyJSON
//
//enum OOFormComponentType {
//    case textItem
//    case dateItem
//    case dateIntervalItem
//    case segueItem
//}
//
//enum OOFormItemStatus {
//    case read
//    case edit
//}
//
//open class OOFormBaseModel:NSObject{
//    var titleName:String?
//    var key:String?
//    var callbackValue:Any?
//    var componentType:OOFormComponentType?
//    var itemStatus:OOFormItemStatus?
//    public override init() {
//        super.init()
//    }
//    
//    convenience init(titleName name:String,key:String,componentType:OOFormComponentType,itemStatus:OOFormItemStatus) {
//        self.init()
//        self.titleName = name
//        self.key = key
//        self.componentType = componentType
//        self.itemStatus = itemStatus
//    }
//}
//
//
//class OOFormDateIntervalModel:OOFormBaseModel{
//    var value1:Any?
//    var value2:Any?
//    override init() {
//        
//    }
//}
//
//class OOFormSegueItemModel:OOFormBaseModel {
//    var segueIdentifier:String?
//    var destVCClass:AnyClass?
//    override init() {
//        
//    }
//}
//
//
//// MARK:- 选择调用代理
//protocol OOCommonBackResultDelegate {
//    func backResult(_ vcIdentifiter:String,_ result:Any?)
//}
//
//
//class OOMeetingFormBean:HandyJSON {
//    required init() {
//    }
//    
//    var subject:String?
//    var description:String?
//    var room:String?
//    var startTime:String?
//    var completedTime:String?
//    var invitePersonList:[String] = []
//    
//    public init(meetingForm:OOMeetingForm){
//        self.subject = meetingForm.subject
//        self.description = meetingForm.desc
//        self.room = meetingForm.room
//        self.invitePersonList = meetingForm.invitePersonList
//        self.startTime = "\(meetingForm.meetingDate.toString(format: "yyyy-MM-dd")) \(meetingForm.startTime.toString(format: "HH:mm:ss"))"
//        self.completedTime = "\(meetingForm.meetingDate.toString(format: "yyyy-MM-dd")) \(meetingForm.completedTime.toString(format: "HH:mm:ss"))"
//    }
//    
//    public func checkFormValues() -> Bool {
//        if subject == nil {
//            return false
//        }
//        if room == nil {
//            return false
//        }
//        if startTime == nil {
//            return false
//        }
//        if completedTime == nil {
//            return false
//        }
//        if invitePersonList.isEmpty {
//            return false
//        }
//        return true
//    }
//    
//    
//}
//
//class OOMeetingForm:HandyJSON {
//    var subject:String?
//    var room:String?
//    var roomName:String?
//    var meetingDate:Date = Date()
//    var startTime:Date = Date()
//    var completedTime:Date = Date()
//    var invitePersonList:[String] = []
//    var desc:String?
//    required init(){
//        
//    }
//
//}
//
//
//
