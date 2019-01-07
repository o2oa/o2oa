//
//  JMSGOptionalContent+.swift
//  JChat
//
//  Created by 邓永豪 on 2017/10/8.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

extension ExJMessage where Base: JMSGOptionalContent {

    /**
     default optional content
     */
    static var `default`: JMSGOptionalContent {
        let optionalContent = JMSGOptionalContent()
        #if READ_VERSION
            optionalContent.needReadReceipt = true
        #else
            optionalContent.needReadReceipt = false
        #endif
        return optionalContent
    }

}
