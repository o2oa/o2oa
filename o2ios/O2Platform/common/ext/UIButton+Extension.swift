//
//  UIButton+Extension.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/25.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

extension UIButton {
    class func btn(bgColor: UIColor, disabledColor: UIColor, title: String, titleColor: UIColor) -> UIButton {
        
        let btn = UIButton(type: .custom)
        btn.frame = .zero
        let attribeTitle = NSAttributedString(string: title, attributes: [NSAttributedString.Key.foregroundColor:titleColor,NSAttributedString.Key.font:UIFont.init(name: "PingFangSC-Regular", size: 18)!])
        btn.setAttributedTitle(attribeTitle, for: .normal)
        btn.setAttributedTitle(attribeTitle, for: .disabled)
        btn.backgroundColor = bgColor
        btn.layer.cornerRadius = 3.0
        btn.layer.masksToBounds = true
        
        return btn
    }
    
}
