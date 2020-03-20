//
//  UITextInput+JChat.swift
//  JChat
//
//  Created by 邓永豪 on 2017/10/18.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

protocol TextProtocol {
    func limitNonMarkedTextSize(_ maxSize: Int)
}

extension UITextView: TextProtocol {
    func limitNonMarkedTextSize(_ maxSize: Int) {
        if self.markedTextRange != nil {
            return
        }
        let text = self.text!
        if text.count > maxSize {
            let range = (text.startIndex ..< text.index(text.startIndex, offsetBy: maxSize))

            let subText = text.substring(with: range)
            self.text = subText
        }
    }
}
