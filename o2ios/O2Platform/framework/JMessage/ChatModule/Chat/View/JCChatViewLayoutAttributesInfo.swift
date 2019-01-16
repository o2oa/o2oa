//
//  JCChatViewLayoutAttributesInfo.swift
//  JChat
//
//  Created by deng on 10/04/2017.
//  Copyright © 2017 HXHG. All rights reserved.
//

import UIKit

@objc open class JCChatViewLayoutAttributesInfo: NSObject {
    
    public init(message: JCMessageType, size: CGSize, rects: [JCChatViewLayoutItem: CGRect], boxRects: [JCChatViewLayoutItem: CGRect]) {
        _message = message
        _cacheSize = size
        _allLayoutedRects = rects
        _allLayoutedBoxRects = boxRects
        super.init()
    }
    
    open var message: JCMessageType {
        return _message
    }
    
    open func layoutedRect(with item: JCChatViewLayoutItem) -> CGRect {
        return _allLayoutedRects[item] ?? .zero
    }
    open func layoutedBoxRect(with item: JCChatViewLayoutItem) -> CGRect {
        return _allLayoutedBoxRects[item] ?? .zero
    }
    
    private var _message: JCMessageType
    private var _cacheSize: CGSize
    
    private var _allLayoutedRects: [JCChatViewLayoutItem: CGRect]
    private var _allLayoutedBoxRects: [JCChatViewLayoutItem: CGRect]
    
}
