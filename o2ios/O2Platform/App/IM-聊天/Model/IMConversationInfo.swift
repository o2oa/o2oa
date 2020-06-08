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
    
    required override init(){}
    
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
    
     required override init(){}
       
       func mapping(mapper: HelpingMapper) {
           
       }
}

class IMMessageBodyInfo: NSObject, DataModel {
    @objc var id: String?
   @objc var type: String?
   @objc var body: String?
    
    
    required override init(){}
    
    func mapping(mapper: HelpingMapper) {
        
    }
}

//websocket 消息对象
class WsMessage: NSObject, DataModel {
    @objc var type: String? //im_create
    @objc var body: IMMessageInfo? //这个对象只有 type=im_create的时候才是这个对象
    required override init(){}
    
    func mapping(mapper: HelpingMapper) {
        
    }
}
