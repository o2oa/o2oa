//
//  JCBusinessCardContent.swift
//  JChat
//
//  Created by 邓永豪 on 2017/8/31.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCBusinessCardContent: NSObject, JCMessageContentType {

    public weak var delegate: JCMessageDelegate?
    open var layoutMargins: UIEdgeInsets = .zero
    
    open class var viewType: JCMessageContentViewType.Type {
        return JCBusinessCardContentView.self
    }
    
    open var userName: String?
    open var appKey: String?
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        return .init(width: 200, height: 87)
    }

}
