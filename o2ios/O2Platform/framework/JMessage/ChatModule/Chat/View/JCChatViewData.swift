//
//  JCChatViewData.swift
//  JChat
//
//  Created by deng on 10/04/2017.
//  Copyright © 2017 HXHG. All rights reserved.
//

import UIKit

internal class JCChatViewData: NSObject, NSCopying {
    
    internal override init() {
        self.elements = []
        super.init()
    }
    internal init(elements: [JCMessageType]) {
        self.elements = elements
        super.init()
    }
    
    internal var count: Int {
        return elements.count
    }
    
    func copy(with zone: NSZone? = nil) -> Any {
        return JCChatViewData(elements: self.elements)
    }
    
    
    internal subscript(index: Int) -> JCMessageType {
        return elements[index]
    }
    
    
    internal func subarray(with subrange: Range<Int>) -> Array<JCMessageType> {
        return Array(elements[subrange])
    }
    
    internal func replaceSubrange(_ subrange: Range<Int>, with collection: Array<JCMessageType>)  {
        elements.replaceSubrange(subrange, with: collection)
    }
    
    
    internal var elements: [JCMessageType]
}
