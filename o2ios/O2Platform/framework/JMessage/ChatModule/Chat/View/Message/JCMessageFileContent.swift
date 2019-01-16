//
//  JCMessageFileContent.swift
//  JChat
//
//  Created by deng on 2017/7/20.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCMessageFileContent: NSObject, JCMessageContentType {

    public weak var delegate: JCMessageDelegate?
    open var layoutMargins: UIEdgeInsets = .zero
    
    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageFileContentView.self
    }
    
    open var data: Data?
    open var fileName: String?
    open var fileType: String?
    open var fileSize: String?
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        return .init(width: 200, height: 95)
    }
}
