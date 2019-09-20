//
//  MeetingModels.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/17.
//  Copyright © 2018年 zone. All rights reserved.
//

import Foundation
import HandyJSON

class OOMeetingConfigProcess: NSObject, DataModel {
    @objc var name: String?
    @objc var id: String?
    @objc var application: String?
    @objc var applicationName: String?
    @objc var alias: String?
    
    required override init() {
        
    }
}

class OOMeetingConfigInfo: NSObject, DataModel {
    var process: OOMeetingConfigProcess?
    var mobileCreateEnable:  Bool?
    var weekBegin: Int?
    
    required override init() {
        
    }
}

class OOMeetingProcessIdentity: NSObject, DataModel {
    @objc var name: String?
    @objc var unique: String?
    @objc var desc:String?
    @objc var distinguishedName: String?
    @objc var person: String?
    @objc var unit: String?
    @objc var unitName: String?
    var unitLevel: Int?
    @objc var unitLevelName: String?
    
    required override init() {
        
    }
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
}

class OOMeetingBuildInfo: NSObject,DataModel {
    
    @objc var address : String?
    @objc var createTime : String?
    @objc var id : String?
    @objc var name : String?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var roomList : [OOMeetingRoomInfo]?
    @objc var updateTime : String?
    
    required override init() {
        
    }
}

class OOMeetingRoomInfo: NSObject,DataModel {
    var available : Bool?
    var idle : Bool?
    @objc var building : String?
    var capacity : Int?
    @objc var createTime : String?
    @objc var device : String?
    var floor : Int?
    @objc var id : String?
    @objc var meetingList : [OOMeetingInfo]?
    @objc var name : String?
    @objc var phoneNumber : String?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var roomNumber : String?
    @objc var updateTime : String?
    
    required override init() {
        
    }
}

class OOMeetingInfo :NSObject,DataModel{
    
    @objc var acceptPersonList : [String]?
    @objc var applicant : String?
    @objc var attachmentList : [OOMeetingAttachmentList]?
    @objc var completedTime : String?
    @objc var confirmStatus : String?
    @objc var createTime : String?
    @objc var descriptionField : String?
    @objc var id : String?
    @objc var invitePersonList : [String]?
    var manualCompleted : Bool?
    var myAccept : Bool?
    var myApply : Bool?
    var myReject : Bool?
    var myWaitAccept : Bool?
    var myWaitConfirm : Bool?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var rejectPersonList : [String]?
    @objc var room : String?
    @objc var startTime : String?
    @objc var status : String?
    @objc var subject : String?
    @objc var updateTime : String?
    
    required override init() {
        
    }
}

class OOMeetingAttachmentList : NSObject,DataModel{
    
    @objc var createTime : String?
    @objc var `extension` : String?
    @objc var id : String?
    @objc var lastUpdatePerson : String?
    @objc var lastUpdateTime : String?
    var length : Int?
    @objc var meeting : String?
    @objc var name : String?
    @objc var storage : String?
    var summary : Bool?
    @objc var updateTime : String?
    
    required override init() {
        
    }
}

class MeetingForm:NSObject,HandyJSON{
    @objc var subject:String?
    @objc var room:String?
    @objc var roomName:String?
    var meetingDate:Date = Date()
    var startTime:Date = Date()
    var completedTime:Date = Date()
    @objc var invitePersonList:[String] = []
    @objc var desc:String?
    override required init() {
        
    }
}


