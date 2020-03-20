//
//  JMSGMessage+.swift
//  JChat
//
//  Created by 邓永豪 on 2017/10/1.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

let kLargeEmoticon = "kLargeEmoticon"
let kShortVideo = "video"
let kBusinessCard = "businessCard"
let kBusinessCardName = "userName"
let kBusinessCardAppKey = "appKey"
let kFileType = "fileType"
let kFileSize = "fileSize"

extension ExJMessage where Base: JMSGMessage {

    var state: JCMessageState {
        switch base.status {
        case .sendFailed, .sendUploadFailed:
            return .sendError
        case .sending:
            return .sending
        default:
            return .sendSucceed
        }
    }

    var isFile: Bool {
        guard let extras = _messageExtras(.file) else {
            return false
        }
        if extras.keys.contains(where: { (key) -> Bool in
            if let key = key as? String {
                return key == kFileType || key == kFileSize
            }
            return false
        }) {
            return true
        }
        return false
    }

    var fileType: String? {
        if !self.isFile {
            return nil
        }
        guard let extras = base.content?.extras else {
            return nil
        }
        return extras[kFileType] as? String
    }

    var fileSize: String? {
        if !self.isFile {
            return nil
        }
        guard let extras = base.content?.extras else {
            return nil
        }
        if let size = extras[kFileSize] as? Int {
            if size > 1024 * 1024 {
                return String(format: "%.1fM", Double(size) / 1024.0 / 1024.0)
            }
            if size > 1024 {
                return "\(size / 1024)K"
            }
            return "\(size)B"
        }
        return nil
    }

    var businessCardName: String? {
        if !self.isBusinessCard {
            return nil
        }
        guard let extras = base.content?.extras else {
            return nil
        }
        return extras[kBusinessCardName] as? String
    }

    var businessCardAppKey: String? {
        if !self.isBusinessCard {
            return nil
        }
        guard let extras = base.content?.extras else {
            return nil
        }
        return extras[kBusinessCardAppKey] as? String
    }


    var isBusinessCard: Bool {
        get {
            guard let extras = _messageExtras(.text) else {
                return false
            }
            if extras.keys.contains(where: { (key) -> Bool in
                if let key = key as? String {
                    return key == kBusinessCard
                }
                return false
            }) {
                return true
            }
            return false
        }
        set {
            if let content = base.content as? JMSGTextContent {
                content.addStringExtra(kBusinessCard, forKey: kBusinessCard)
            }
        }
    }

    var isShortVideo: Bool {
        get {
            guard let extras = _messageExtras(.file) else {
                return false
            }
            if extras.keys.contains(where: { (key) -> Bool in
                if let key = key as? String {
                    return key == kShortVideo
                }
                return false
            }) {
                return true
            }
            return false
        }
        set {
            if let content = base.content as? JMSGFileContent {
                content.addStringExtra("mov", forKey: kShortVideo)
            }
        }
    }

    var isLargeEmoticon: Bool {
        get {
            guard let extras = _messageExtras(.image) else {
                return false
            }
            if extras.keys.contains(where: { (key) -> Bool in
                if let key = key as? String {
                    // android 的扩展字段：jiguang
                    return key == kLargeEmoticon || key == "jiguang"
                }
                return false
            }) {
                return true
            }
            return false
        }
        set {
            if let content = base.content as? JMSGImageContent {
                content.addStringExtra(kLargeEmoticon, forKey: kLargeEmoticon)
            }
        }
    }

    static func createBusinessCardMessage(_ conversation: JMSGConversation, _ userName: String, _ appKey: String) -> JMSGMessage {
        let message: JMSGMessage!
        let content = JMSGTextContent(text: "推荐了一张名片")
        content.addStringExtra(userName, forKey: "userName")
        content.addStringExtra(appKey, forKey: "appKey")
        if conversation.ex.isGroup  {
            let group = conversation.target as! JMSGGroup
            message = JMSGMessage.createGroupMessage(with: content, groupId: group.gid)
        } else {
            let user = conversation.target as! JMSGUser
            message = JMSGMessage.createSingleMessage(with: content, username: user.username)
        }
        message.ex.isBusinessCard = true
        return message
    }

    static func createBusinessCardMessage(gid: String, userName: String, appKey: String) -> JMSGMessage {
        let message: JMSGMessage!
        let content = JMSGTextContent(text: "推荐了一张名片")
        content.addStringExtra(userName, forKey: "userName")
        content.addStringExtra(appKey, forKey: "appKey")
        message = JMSGMessage.createGroupMessage(with: content, groupId: gid)
        message.ex.isBusinessCard = true
        return message
    }

    /**
     create a @ message
     */
    static func createMessage(_ conversation: JMSGConversation, _ content: JMSGAbstractContent, _ reminds: [JCRemind]?) -> JMSGMessage {
        let message: JMSGMessage!
        if conversation.ex.isGroup && reminds != nil {
            let group = conversation.target as! JMSGGroup

            if reminds!.count > 0 {
                var users: [JMSGUser] = []
                var isAtAll = false
                for remind in reminds! {
                    guard let user = remind.user else {
                        isAtAll = true
                        break
                    }
                    users.append(user)
                }
                if isAtAll {
                    message = JMSGMessage.createGroupAtAllMessage(with: content, groupId: group.gid)
                } else {
                    message = JMSGMessage.createGroupMessage(with: content, groupId: group.gid, at_list: users)
                }
            } else {
                message = JMSGMessage.createGroupMessage(with: content, groupId: group.gid)
            }
        } else {
            message = JMSGMessage.createSingleMessage(with: content, username: JMSGUser.myInfo().username)
        }
        return message
    }

    // MARK: - private method

    /**
     get current message extras
     */
    private func _messageExtras(_ contentType: JMSGContentType) -> [AnyHashable : Any]? {
        let content: JMSGAbstractContent?
        switch contentType {
        case .text:
            content = base.content as? JMSGTextContent
        case .image:
            content = base.content as? JMSGImageContent
        case .file:
            content = base.content as? JMSGFileContent
        default:
            return nil
        }
        guard let messsageContent = content else {
            return nil
        }
        guard let extras = messsageContent.extras else {
            return nil
        }
        return extras
    }

}
