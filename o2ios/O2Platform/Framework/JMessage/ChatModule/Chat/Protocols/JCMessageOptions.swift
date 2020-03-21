//
//  JCMessageOptions.swift
//  JChat
//
//  Created by deng on 2017/3/8.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

/// 消息类型
@objc public enum JCMessageStyle: Int {
    case notice
    case bubble
}

/// 消息对齐方式
@objc public enum JCMessageAlignment: Int {
    case left
    case right
    case center
}

@objc public enum JCMessageState: Int {
    case sending
    case sendError
    case sendSucceed
    case downloadFailed
}

/// 消息选项
@objc open class JCMessageOptions: NSObject {
    
    public override init() {
        super.init()
    }
    
    public convenience init(with content: JCMessageContentType) {
        self.init()
        
        switch content {
        case is JCMessageNoticeContent:
            self.style = .notice
            self.alignment = .center
            self.showsCard = false
            self.showsAvatar = false
            self.showsBubble = true
            self.isUserInteractionEnabled = false
            
        case is JCMessageTimeLineContent:
            self.style = .notice
            self.alignment = .center
            self.showsCard = false
            self.showsAvatar = false
            self.showsBubble = false
            self.isUserInteractionEnabled = false
            
//        case is JCMessageImageContent:
//            self.showsTips = false
            
        default:
            break
        }
    }
    
    open var style: JCMessageStyle = .bubble
    open var alignment: JCMessageAlignment = .left
    
    open var isUserInteractionEnabled: Bool = true
    
    open var showsCard: Bool = false
    open var showsAvatar: Bool =  true
    open var showsBubble: Bool = true
    open var showsTips: Bool = true
    open var state: JCMessageState = .sendSucceed
    
    internal func fix(with content: JCMessageContentType)  {
    }
}

