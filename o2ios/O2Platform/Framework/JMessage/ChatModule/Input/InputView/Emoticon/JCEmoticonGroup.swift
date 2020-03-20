//
//  JCEmoticonGroup.swift
//  JChat
//
//  Created by deng on 2017/3/9.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

public enum JCEmoticonType: Int {
    case small = 0
    case large = 1
    
    public var isSmall: Bool {
        return self == .small
    }
    
    public var isLarge: Bool {
        return self == .large
    }
}

open class JCEmoticonGroup: NSObject {
    
    open lazy var id: String = UUID().uuidString
    
    open var title: String?
    open var thumbnail: UIImage?
    open var type: JCEmoticonType = .small
    open var emoticons: [JCEmoticon] = []

}
