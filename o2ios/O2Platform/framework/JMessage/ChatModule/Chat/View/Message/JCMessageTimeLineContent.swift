//
//  JCMessageTimeLineContent.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageTimeLineContent: NSObject, JCMessageContentType {

    public weak var delegate: JCMessageDelegate?
    open var layoutMargins: UIEdgeInsets = .zero

    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageTimeLineContentView.self
    }
    
    public init(date: Date) {
        self.date = date
        super.init()
    }

    internal var before: JCMessageType?
    internal var after: JCMessageType?
    
    open var date: Date
    open var text: String {
        return JCMessageTimeLineContent.dd(after?.date ?? date)
    }

    // 这个是比较耗时的操作，所以这里设置为全局只创建一次
    static let defaultFormat = DateFormatter.dateFormat(fromTemplate: "hh:mm", options: 0, locale: nil) ?? "hh:mm"
    
    static func dd(_ date: Date) -> String {
        
        // yy-MM-dd hh:mm
        // MM-dd hh:mm
        // 星期一 hh:mm - 7 * 24小时内
        // 昨天 hh:mm - 2 * 24小时内
        // 今天 hh:mm - 24小时内
        
        let s1 = TimeInterval(date.timeIntervalSince1970)
        let s2 = TimeInterval(time(nil))
        
        let dz = TimeInterval(TimeZone.current.secondsFromGMT())
        
        let formatter = DateFormatter()
        // 每次都创建会非常耗时
        let format1 = JCMessageTimeLineContent.defaultFormat
        
        let days1 = Int64(s1 + dz) / (24 * 60 * 60)
        let days2 = Int64(s2 + dz) / (24 * 60 * 60)
        
        switch days1 - days2 {
        case +0:
            // Today
            formatter.dateFormat = "\(format1)"
        case +1:
            // Tomorrow
            formatter.dateFormat = "'明天' \(format1)"
        case +2 ... +7:
            // 2 - 7 day later
            formatter.dateFormat = "EEEE \(format1)"
        case -1:
            formatter.dateFormat = "'昨天' \(format1)"
        case -2:
            formatter.dateFormat = "'前天' \(format1)"
        case -7 ... -2:
            // 2 - 7 day ago
            formatter.dateFormat = "EEEE \(format1)"
        default:
            // Distant
            if date.isThisYear() {
                formatter.dateFormat = "MM-dd \(format1)"
            } else {
                formatter.dateFormat = "yy-MM-dd \(format1)"
            }
        }
        return formatter.string(from: date)
    }
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        
        let attr = NSMutableAttributedString(string: text, attributes: [
            NSAttributedString.Key.font: UIFont.systemFont(ofSize: 12),
            NSAttributedString.Key.foregroundColor: UIColor.white,
            ])
        
        return CGSize(width: attr.size().width + 11, height: 18)
    }
}
