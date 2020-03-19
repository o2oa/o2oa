//
//  JMSGConversation+.swift
//  JChat
//
//  Created by 邓永豪 on 2017/10/1.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

let kConversationSticky = "kConversationSticky"

extension ExJMessage where Base: JMSGConversation {

    /**
     conversation target type is group
     */
    var isGroup: Bool {
        return base.conversationType == .group
    }

    var stickyTime: Int {
        guard let extras = base.getExtras() else {
            return 0
        }
        guard let value = extras[kConversationSticky] as? String else {
            return 0
        }
        if !value.isEmpty {
            return Int(value) ?? 0
        }
        return 0
    }

    var isSticky: Bool {
        get {
            guard let extras = base.getExtras() else {
                return false
            }
            guard let value = extras[kConversationSticky] as? String else {
                return false
            }
            if !value.isEmpty {
                return true
            }
            return false
        }
        set {
            if newValue {
                let date = Date(timeIntervalSinceNow: 0)
                let time = String(Int(date.timeIntervalSince1970))
                base.setExtraValue(time, forKey: kConversationSticky)
            } else {
                base.setExtraValue(nil, forKey: kConversationSticky)
            }
        }
    }

}

extension Array where Element == JMSGConversation {
    var unreadCount: Int {
        var count = 0
        for item in self {
            if let group = item.target as? JMSGGroup {
                // TODO: isNoDisturb 这个接口存在性能问题，如果大量离线会卡死
                if group.isNoDisturb {
                    continue
                }
            }
            if let user = item.target as? JMSGUser {
                if user.isNoDisturb {
                    continue
                }
            }
            count += item.unreadCount?.intValue ?? 0
        }
        return count
    }
}

