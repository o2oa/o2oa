//
//  JCMessageNoticeContent.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageNoticeContent: NSObject, JCMessageContentType {
    public weak var delegate: JCMessageDelegate?
    
    open var layoutMargins: UIEdgeInsets = .zero
    
    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageNoticeContentView.self
    }
    
    public init(text: String) {
        self.text = text
        super.init()
    }
    
    open var text: String
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        let attributes = [
            NSAttributedString.Key.font: UIFont.systemFont(ofSize: 12),
            NSAttributedString.Key.foregroundColor: UIColor.white,
        ]
        let attr = NSMutableAttributedString(string: text, attributes: attributes)
        let mattrSize = attr.boundingRect(with: CGSize(width: 250.0, height: Double(MAXFLOAT)), options: [.usesLineFragmentOrigin,.usesFontLeading], context: nil)
        let size = CGSize(width: mattrSize.size.width + 11, height: mattrSize.size.height + 4)
        return size
    }

    public static let unsupport: JCMessageNoticeContent = JCMessageNoticeContent(text: "The message does not support")
}
