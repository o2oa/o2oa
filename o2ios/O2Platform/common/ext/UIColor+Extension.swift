//
//  UIColor+Extension.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/18.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import UIKit

extension UIColor {
    
    class func rgb(_ r: Int, _ g: Int, _ b: Int, _ alpha: CGFloat) -> UIColor {
        return UIColor.init(red: CGFloat(r)/255.0, green: CGFloat(g)/255.0, blue: CGFloat(b)/255.0, alpha: alpha / 1.0)
    }
    
    class func hexRGB(_ rgbValue: Int, _ alpha: CGFloat) -> UIColor {
        return UIColor.init(red: ((CGFloat)((rgbValue & 0xFF0000) >> 16))/255.0, green: ((CGFloat)((rgbValue & 0xFF00) >> 8))/255.0, blue: ((CGFloat)(rgbValue & 0xFF))/255.0, alpha: alpha / 1.0)
    }
    
    /**
     *  16进制 转 RGBA
     */
    class func UIColorFromRGBA(rgb:Int, alpha:CGFloat) ->UIColor {
        
        return UIColor(red: ((CGFloat)((rgb & 0xFF0000) >> 16)) / 255.0,
                       green: ((CGFloat)((rgb & 0xFF00) >> 8)) / 255.0,
                       blue: ((CGFloat)(rgb & 0xFF)) / 255.0,
                       alpha: alpha)
    }
    
    /**
     *  16进制 转 RGB
     */
    class func UIColorFromRGB(rgb:Int) ->UIColor {
        
        return UIColor(red: ((CGFloat)((rgb & 0xFF0000) >> 16)) / 255.0,
                       green: ((CGFloat)((rgb & 0xFF00) >> 8)) / 255.0,
                       blue: ((CGFloat)(rgb & 0xFF)) / 255.0,
                       alpha: 1.0)
    }
    
    // MARK: -  RGBA
    class func RGBA(r:CGFloat,g:CGFloat,b:CGFloat,a:CGFloat) -> UIColor {
        return UIColor(red: r/255.0, green: g/255.0, blue: b/255.0, alpha: a)
    }
    
    // MARK: - HexInt Covert UIColor
    class func hexInt(_ hexValue:Int) -> UIColor {
      return hexInt(hexValue, 1.0)
    }
    
    // MARK: - HexInt alpha Covert UIColor
    class func hextIntWithAlpha(_ hexValue:Int,_ alpha:CGFloat) -> UIColor {
        return hexInt(hexValue,alpha)
    }
    
    // MARK: - 私有方法实现
    fileprivate class func hexInt(_ hexValue:Int,_ alpha:CGFloat)  -> UIColor {
        return UIColor(red: ((CGFloat)((hexValue & 0xFF0000) >> 16)) / 255.0,
                       green: ((CGFloat)((hexValue & 0xFF00) >> 8)) / 255.0,
                       blue: ((CGFloat)(hexValue & 0xFF)) / 255.0,
                       alpha: alpha)
    }
}
