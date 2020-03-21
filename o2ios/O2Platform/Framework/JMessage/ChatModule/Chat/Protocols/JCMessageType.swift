//
//  JCMessageType.swift
//  JChat
//
//  Created by deng on 10/04/2017.
//  Copyright © 2017 HXHG. All rights reserved.
//

import Foundation
import JMessage

@objc public enum MessageTargetType: Int {
    case single = 0
    case group
}

@objc public protocol JCMessageType: class {
    
    var name: String { get }
    var identifier: UUID { get }
    var msgId: String { get }
    var date: Date { get }
    var sender: JMSGUser? { get }
    var senderAvator: UIImage? { get }
    var receiver: JMSGUser? { get }
    var content: JCMessageContentType { get }
    var options: JCMessageOptions { get }
    var updateSizeIfNeeded: Bool { get }
    var unreadCount: Int { get }
    var targetType: MessageTargetType { get }
}

@objc public protocol JCMessageDelegate: NSObjectProtocol {
    @objc optional func message(message: JCMessageType, videoData data: Data?)
    @objc optional func message(message: JCMessageType, voiceData data: Data?, duration: Double)
    @objc optional func message(message: JCMessageType, fileData data: Data?, fileName: String?, fileType: String?)
    @objc optional func message(message: JCMessageType, location address: String?, lat: Double, lon: Double)
    @objc optional func message(message: JCMessageType, image: UIImage?)
    // user 对象是为了提高效率，如果 user 已经加载出来了，就直接使用，不需要重新去获取一次
    @objc optional func message(message: JCMessageType, user: JMSGUser?, businessCardName: String, businessCardAppKey: String)
    @objc optional func clickTips(message: JCMessageType)
    @objc optional func tapAvatarView(message: JCMessageType)
    @objc optional func longTapAvatarView(message: JCMessageType)
    @objc optional func tapUnreadTips(message: JCMessageType)
}
