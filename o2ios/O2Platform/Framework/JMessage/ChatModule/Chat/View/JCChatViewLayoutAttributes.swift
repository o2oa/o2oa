//
//  JCChatViewLayoutAttributes.swift
//  JChat
//
//  Created by deng on 2017/3/1.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

@objc public enum JCChatViewLayoutItem: Int {
    case all
    case card
    case avatar
    case bubble
    case content
    case tips
}

@objc open class JCChatViewLayoutAttributes: UICollectionViewLayoutAttributes {
    
    public override init() {
        super.init()
    }
    
    open override func copy(with zone: NSZone? = nil) -> Any {
        let new = super.copy(with: zone)
        if let new = new as? JCChatViewLayoutAttributes {
            new.info = info
        }
        return new
    }
    
    open var message: JCMessageType? {
        return info?.message
    }
    
    open var info: JCChatViewLayoutAttributesInfo?
}

