//
//  CCUIColorExtension.swift
//  JChat
//
//  Created by deng on 2017/2/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

extension UIColor {
    
    static var random: UIColor {
        let maxValue: UInt32 = 24
        return UIColor(red: CGFloat(arc4random() % maxValue) / CGFloat(maxValue),
                       green: CGFloat(arc4random() % maxValue) / CGFloat(maxValue) ,
                       blue: CGFloat(arc4random() % maxValue) / CGFloat(maxValue) ,
                       alpha: 1)
    }
    
//    convenience init(red: Int, green: Int, blue: Int) {
//        assert(red >= 0 && red <= 255, "Invalid red component")
//        assert(green >= 0 && green <= 255, "Invalid green component")
//        assert(blue >= 0 && blue <= 255, "Invalid blue component")
//
//        self.init(red: CGFloat(red) / 255.0, green: CGFloat(green) / 255.0, blue: CGFloat(blue) / 255.0, alpha: 1.0)
//    }

    convenience init(netHex:Int) {
        self.init(red:(netHex >> 16) & 0xff, green:(netHex >> 8) & 0xff, blue:netHex & 0xff)
    }    
}
