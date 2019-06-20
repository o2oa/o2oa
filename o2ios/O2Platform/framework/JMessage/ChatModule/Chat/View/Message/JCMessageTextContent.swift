//
//  JCMessageTextContent.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageTextContent: NSObject, JCMessageContentType {
    public weak var delegate: JCMessageDelegate?
    public override init() {
        let text = "this is a test text"
        self.text = NSAttributedString(string: text)
        super.init()
    }
    public init(text: String) {
        self.text = NSAttributedString(string: text)
        super.init()
    }
    public init(attributedText: NSAttributedString) {
        self.text = attributedText
        super.init()
    }
    
    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageTextContentView.self
    }
    open var layoutMargins: UIEdgeInsets = .init(top: 9, left: 10, bottom: 9, right: 10)
    
    open var text: NSAttributedString
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        let mattr = NSMutableAttributedString(attributedString: text)
        mattr.addAttribute(NSAttributedString.Key.font, value: UIFont.systemFont(ofSize: 16), range: NSMakeRange(0, mattr.length))

        let mattrSize = mattr.boundingRect(with: CGSize(width: 220.0, height: Double(MAXFLOAT)), options: [.usesLineFragmentOrigin,.usesFontLeading], context: nil)
        self.text = mattr
        return .init(width: max(mattrSize.width, 15), height: max(mattrSize.height, 15))
    }
}
