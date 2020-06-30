//
//  IMConversation.swift
//  O2Platform
//
//  Created by FancyLou on 2020/6/4.
//  Copyright © 2020 zoneland. All rights reserved.
//
import HandyJSON


class IMConversationInfo: NSObject, DataModel {
    @objc var id: String?
    @objc var title: String?
    @objc var type: String? //会话类型 单人 、 群.
    @objc var personList: [String]?
    @objc var adminPerson: String?
    @objc var note: String?

    @objc var lastMessageTime: String?
    @objc var createTime: String?
    @objc var updateTime: String?
    var unreadNumber: Int?
    var isTop: Bool?

    @objc var lastMessage: IMMessageInfo?

    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }
}

class IMConversationUpdateForm: NSObject, DataModel  {
    @objc var id: String?
    @objc var title: String?
    @objc var personList: [String]?
    @objc var adminPerson: String?
    @objc var note: String?
    
    required override init() { }

       func mapping(mapper: HelpingMapper) {

       }
}


class IMMessageRequestForm: NSObject, DataModel {

    @objc var conversationId: String?

    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }
}

class IMMessageInfo: NSObject, DataModel {
    @objc var id: String?
    @objc var conversationId: String?
    @objc var body: String?
    @objc var createPerson: String?
    @objc var createTime: String?
    @objc var updateTime: String?

    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }
}

class IMMessageBodyInfo: NSObject, DataModel {
    @objc var id: String?
    @objc var type: String?
    @objc var body: String?
    @objc var fileId: String? //文件id
    @objc var fileExtension: String? //文件扩展
    @objc var fileTempPath: String? //本地临时文件地址
    @objc var audioDuration: String? // 音频文件时长
    @objc var address: String? //type=location的时候位置信息
    @objc var addressDetail: String?
    var latitude: Double?//type=location的时候位置信息
    var longitude: Double?//type=location的时候位置信息


    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }
}

class IMUploadBackModel: NSObject, DataModel {
    public override var description: String {
        return "IMUploadBackModel"
    }
    
    @objc var id:String?
    @objc var fileExtension: String? //文件扩展
    
    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }
}

//websocket 消息对象
class WsMessage: NSObject, DataModel {
    @objc var type: String? //im_create
    @objc var body: IMMessageInfo? //这个对象只有 type=im_create的时候才是这个对象
    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }
}

//其他消息
class InstantMessage: NSObject, DataModel {
    @objc var id: String?
    @objc var title: String?
    @objc var type: String?
    @objc var body: String?
    @objc var consumerList: [String]?
    @objc var person: String?
    var consumed: Bool?
    @objc var createTime: String?
    @objc var updateTime: String?
    
    required override init() { }

    func mapping(mapper: HelpingMapper) {

    }

}


struct  O2LocationData {
    var address: String?
    var addressDetail: String?
    var latitude: Double?
    var longitude: Double?
}
