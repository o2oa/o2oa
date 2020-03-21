//
//  JCMessageImageContent.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

open class JCMessageImageContent: NSObject, JCMessageContentType {
    typealias uploadHandle = (_ percent: Float) -> ()

    public weak var delegate: JCMessageDelegate?
    var upload: uploadHandle?
    var imageSize: CGSize?
    open var image: UIImage?
    open var layoutMargins: UIEdgeInsets = .zero
    
    open class var viewType: JCMessageContentViewType.Type {
        return JCMessageImageContentView.self
    }
    
    open func sizeThatFits(_ size: CGSize) -> CGSize {
        if image == nil {
             image = UIImage.createImage(color: UIColor(netHex: 0xCDD0D1), size: imageSize ?? CGSize(width: 160, height: 160))
        }
        let size = imageSize ?? (image?.size)!
        let scale = min(min(160, size.width) / size.width, min(160, size.height) / size.height)
        let w = size.width * scale
        let h = size.height * scale
        return .init(width: w, height: h)
    }
}
