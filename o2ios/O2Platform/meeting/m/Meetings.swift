//
//  Meetings.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/22.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

class Build:Mappable {
    var createTime:String?
    var updateTime:String?
    var id:String?
    var pinyin:String?
    var pinyinInitial:String?
    var name:String?
    var address:String?
    var roomList:[Room]?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        createTime <- map["createTime"]
        updateTime <- map["updateTime"]
        id <- map["id"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        name <- map["name"]
        address <- map["address"]
        roomList <- map["roomList"]
    }
}

class Room:Mappable {
    var createTime:String?
    var updateTime:String?
    var id:String?
    var pinyin:String?
    var pinyinInitial:String?
    var name:String?
    var meetingList:[Meeting]?
    var building:String?
    var floor:Int?
    var roomNumber:Int?
    var device:String?
    var capacity:Int?
    var auditor:String?
    var available:Bool?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        createTime <- map["createTime"]
        updateTime <- map["updateTime"]
        id <- map["id"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        name <- map["name"]
        meetingList <- map["meetingList"]
        building <- map["building"]
        floor <- map["floor"]
        roomNumber <- map["roomNumber"]
        device <- map["device"]
        capacity <- map["capacity"]
        auditor <- map["auditor"]
        available <- map["available"]
    }
}

class MeetingFormBean:Mappable{
    var subject:String?
    var description:String?
    var room:String?
    var startTime:String?
    var completedTime:String?
    var invitePersonList:[String] = []
    
    init(meetingForm:MeetingForm){
        self.subject = meetingForm.subject
        self.description = meetingForm.desc
        self.room = meetingForm.room
        self.invitePersonList = meetingForm.invitePersonList
        self.startTime = "\(meetingForm.meetingDate.toString(format: "yyyy-MM-dd")) \(meetingForm.startTime.toString(format: "HH:mm:ss"))"
          self.self.completedTime = "\(meetingForm.meetingDate.toString(format: "yyyy-MM-dd")) \(meetingForm.completedTime.toString(format: "HH:mm:ss"))"
//        self.startTime = Date.genDateStringFromDateAndTime(meetingForm.meetingDate, theTime: meetingForm.startTime)
//        self.completedTime = Date.genDateStringFromDateAndTime(meetingForm.meetingDate, theTime: meetingForm.completedTime)
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        subject <- map["subject"]
        description <- map["description"]
        room <- map["room"]
        startTime <- map["startTime"]
        completedTime <- map["completedTime"]
        invitePersonList <- map["invitePersonList"]
    }
    
    
    
    
}

class MeetingForm:Mappable{
    var subject:String?
    var room:String?
    var roomName:String?
    var meetingDate:Date = Date()
    var startTime:Date = Date()
    var completedTime:Date = Date()
    var invitePersonList:[String] = []
    var desc:String?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        subject <- map["subject"]
        room <- map["room"]
        roomName <- map["roomName"]
        meetingDate <- map["meetingDate"]
        startTime <- map["startTime"]
        completedTime <- map["completedTime"]
        invitePersonList <- map["invitePersonList"]
        desc <- map["desc"]
    }
}



class Meeting:Mappable {
    var status:String?
    var myApply:Bool?
    var myWaitConfirm:Bool?
    var myWaitAccept:Bool?
    var myAccept:Bool?
    var myReject:Bool?
    var attachmentList:[String]?
    var createTime:String?
    var updateTime:String?
    var id:String?
    var subject:String?
    var pinyin:String?
    var pinyinInitial:String?
    var desc:String?
    var room:String?
    var startTime:String?
    var completedTime:String?
    var invitePersonList:[String]?
    var acceptPersonList:[String]?
    var rejectPersonList:[String]?
    var confirmStatus:String?
    var manualCompleted:Bool?
    var applicant:String?
    var auditor:String?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        status <- map["status"]
        myApply <- map["myApply"]
        myWaitConfirm <- map["myWaitConfirm"]
        myWaitAccept <- map["myWaitAccept"]
        myAccept <- map["myAccept"]
        myReject <- map["myReject"]
        attachmentList <- map["attachmentList"]
        createTime <- map["createTime"]
        updateTime <- map["updateTime"]
        id <- map["id"]
        subject <- map["subject"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        desc <- map["desc"]
        room <- map["room"]
        startTime <- map["startTime"]
        completedTime <- map["completedTime"]
        invitePersonList <- map["invitePersonList"]
        acceptPersonList <- map["acceptPersonList"]
        rejectPersonList <- map["rejectPersonList"]
        confirmStatus <- map["confirmStatus"]
        manualCompleted <- map["manualCompleted"]
        applicant <- map["applicant"]
        auditor <- map["auditor"]
    }
}
