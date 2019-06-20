//
//  JCMessageVideoContent.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import JMessage

open class JCMessageVideoContent: NSObject, JCMessageContentType {

    public weak var delegate: JCMessageDelegate?
    open var layoutMargins: UIEdgeInsets = .zero
    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageVideoContentView.self
    }
    open var data: Data?
    open var image: UIImage?
    open var fileContent: JMSGFileContent?
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        if data == nil {
            return .init(width: 140, height: 89)
        }
        image = JCVideoManager.getFristImage(data: data!)
        let size = image?.size ?? .zero
        
        let scale = min(min(160, size.width) / size.width, min(160, size.height) / size.height)
        
        let w = size.width * scale
        let h = size.height * scale
        return .init(width: w, height: h)
    }
}
