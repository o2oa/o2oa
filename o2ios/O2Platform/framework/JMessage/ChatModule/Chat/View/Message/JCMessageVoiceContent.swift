//
//  JCMessageVoiceContent.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageVoiceContent: NSObject, JCMessageContentType {

    public weak var delegate: JCMessageDelegate?
    open var layoutMargins: UIEdgeInsets = .init(top: 5, left: 10, bottom: 5, right: 10)
    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageVoiceContentView.self
    }
    open var data: Data?
    open var duration: TimeInterval = 9999
    open var attributedText: NSAttributedString?
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        // +---------------+
        // | |||  99'59''  |
        // +---------------+
        let minute = Int(duration) / 60
        let second = Int(duration) % 60
        var string = "\(minute)'\(second)''"
        if minute == 0 {
            string = "\(second)''"
        }
        attributedText = NSAttributedString(string: string, attributes: [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 14)])
        
        return .init(width: 20 + 38 + 20, height: 26)
    }
}
