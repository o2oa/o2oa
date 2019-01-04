////
////  OOMeetingModels.swift
////  O2Platform
////
////  Created by 刘振兴 on 2018/4/5.
////  Copyright © 2018年 zoneland. All rights reserved.
////
//
//import Foundation
//import HandyJSON
//
//class OOMeetingBuildInfo: NSObject,DataModel {
//    
//    var address : String?
//    var createTime : String?
//    var id : String?
//    var name : String?
//    var pinyin : String?
//    var pinyinInitial : String?
//    var roomList : [OOMeetingRoomInfo]?
//    var updateTime : String?
//    
//    
//    required override init() {
//        
//    }
//}
//
//class OOMeetingRoomInfo: NSObject,DataModel {
//    var available : Bool?
//    var idle : Bool?
//    var building : String?
//    var capacity : Int?
//    var createTime : String?
//    var device : String?
//    var floor : Int?
//    var id : String?
//    var meetingList : [OOMeetingInfo]?
//    var name : String?
//    var phoneNumber : String?
//    var pinyin : String?
//    var pinyinInitial : String?
//    var roomNumber : String?
//    var updateTime : String?
//    
//    required override init() {
//        
//    }
//}
//
//class OOMeetingInfo :NSObject,DataModel{
//    
//    var acceptPersonList : [String]?
//    var applicant : String?
//    var attachmentList : [OOMeetingAttachmentList]?
//    var completedTime : String?
//    var confirmStatus : String?
//    var createTime : String?
//    var descriptionField : String?
//    var id : String?
//    var invitePersonList : [String]?
//    var manualCompleted : Bool?
//    var myAccept : Bool?
//    var myApply : Bool?
//    var myReject : Bool?
//    var myWaitAccept : Bool?
//    var myWaitConfirm : Bool?
//    var pinyin : String?
//    var pinyinInitial : String?
//    var rejectPersonList : [String]?
//    var room : String?
//    var startTime : String?
//    var status : String?
//    var subject : String?
//    var updateTime : String?
//    
//    required override init() {
//        
//    }
//}
//
//class OOMeetingAttachmentList : NSObject,DataModel{
//    
//    var createTime : String?
//    var `extension` : String?
//    var id : String?
//    var lastUpdatePerson : String?
//    var lastUpdateTime : String?
//    var length : Int?
//    var meeting : String?
//    var name : String?
//    var storage : String?
//    var summary : Bool?
//    var updateTime : String?
//    
//    required override init() {
//        
//    }
//}
//
////class MeetingForm:NSObject,HandyJSON{
////    var subject:String?
////    var room:String?
////    var roomName:String?
////    var meetingDate:Date = Date()
////    var startTime:Date = Date()
////    var completedTime:Date = Date()
////    var invitePersonList:[String] = []
////    var desc:String?
////    override required init() {
////        
////    }
////}
