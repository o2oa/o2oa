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
    
    /// EZSwiftExtensions: Convenience constructor for UIButton.
    public convenience init(x: CGFloat, y: CGFloat, w: CGFloat, h: CGFloat, target: AnyObject, action: Selector) {
        self.init(frame: CGRect(x: x, y: y, width: w, height: h))
        addTarget(target, action: action, for: UIControl.Event.touchUpInside)
    }
    
    /// EZSwiftExtensions: Set a background color for the button.
    public func setBackgroundColor(_ color: UIColor, forState: UIControl.State) {
        UIGraphicsBeginImageContext(CGSize(width: 1, height: 1))
        UIGraphicsGetCurrentContext()?.setFillColor(color.cgColor)
        UIGraphicsGetCurrentContext()?.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
        let colorImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        self.setBackgroundImage(colorImage, for: forState)
    }
}
