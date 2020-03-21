//
//  JCRemind.swift
//  JChat
//
//  Created by deng on 2017/7/19.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
class JCRemind: NSObject {
    
    var user: JMSGUser?
    var startIndex: Int
    var endIndex: Int
    var length: Int
    var isAtAll: Bool
    
    init(_ user: JMSGUser?, _ startIndex: Int, _ endIndex: Int, _ length: Int, _ isAtAll: Bool) {
        self.user = user
        self.startIndex = startIndex
        self.endIndex = endIndex
        self.length = length
        self.isAtAll = isAtAll
    }
}
