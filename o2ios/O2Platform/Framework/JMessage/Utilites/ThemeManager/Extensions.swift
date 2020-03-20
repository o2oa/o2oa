//
//  Extensions.swift
//  ThemeDemo
//
//  Created by 邓永豪 on 2017/8/24.
//  Copyright © 2017年 dengyonghao. All rights reserved.
//

import UIKit

extension UIImage {
    
    static func loadImage(_ imageName: String) -> UIImage? {
        return ThemeManager.instance.loadImage(imageName)
    }
    
    // 如果明确资源不受 theme 变化而变化，使用这个接口会更快
    static func loadDefaultImage(_ imageName: String) -> UIImage? {
        return ThemeManager.instance.loadImage(imageName, .default)
    }
    
}

extension UIColor {

    convenience init(red: Int, green: Int, blue: Int) {
        assert(red >= 0 && red <= 255, "Invalid red component")
        assert(green >= 0 && green <= 255, "Invalid green component")
        assert(blue >= 0 && blue <= 255, "Invalid blue component")
        
        self.init(red: CGFloat(red) / 255.0, green: CGFloat(green) / 255.0, blue: CGFloat(blue) / 255.0, alpha: 1.0)
    }
    
    convenience init(_ colorName: String) {
        let  netHex = ThemeManager.instance.themeColor(colorName)
        self.init(red:(netHex >> 16) & 0xff, green:(netHex >> 8) & 0xff, blue:netHex & 0xff)
    }
    
}
