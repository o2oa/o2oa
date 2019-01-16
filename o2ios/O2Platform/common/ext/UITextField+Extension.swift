//
//  UITextField+Extension.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/28.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

extension UITextField {
    // MARK: - 
    class func createTextField(_ leftView:UIView?, _ rightView:UIView?,_ textPrompt:String?) -> UITextField {
        let textField = UITextField(frame: .zero)
        textField.backgroundColor = .clear
        //textField.clearsOnBeginEditing = true
        textField.clearButtonMode = .whileEditing
        
        //leftView
        if leftView != nil {
            textField.leftView = leftView
            textField.leftViewMode = .always
        }
        
        //rightView
        if rightView != nil {
            textField.rightView = rightView
            textField.rightViewMode = .always
        }
        
        textField.defaultTextAttributes = [NSAttributedString.Key.foregroundColor:UIColor.hexInt(0x666666),NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 15)!]
        textField.attributedPlaceholder = NSAttributedString(string: textPrompt!, attributes: [NSAttributedString.Key.foregroundColor:UIColor.hextIntWithAlpha(0x888888,0.4),NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 13)!])
        
        //bottom line
        
        return textField
    }
}
