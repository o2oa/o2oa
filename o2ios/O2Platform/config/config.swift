//
//  config.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation


//是否连接到collect服务器 直连版本false 需要在Info.plist配置服务器信息
let O2IsConnect2Collect = true

//一些常量
//友盟及蒲公英相应的KEY
let PGY_APP_ID = "84492f465f8f4fb11b8486dc50bf4a38"
//bugly
let BUGLY_ID = "0bc87f457b"
//极光
let JPUSH_APP_KEY = "9aca7cc20fe0cc987cd913ca"
let BAIDU_MAP_KEY = "cL9Y2GLhUxgqmKTIcyxv28Y2S7O2DfYj"
let JPUSH_channel = "Publish channel"



let SCREEN_WIDTH:CGFloat = UIScreen.main.bounds.width;

let SCREEN_HEIGHT:CGFloat = UIScreen.main.bounds.height;

//x, xs : 812  xr, xs max : 896
let iPhoneX = (UIScreen.main.bounds.height == 812 || UIScreen.main.bounds.height == 896) ? true : false

let safeAreaTopHeight:CGFloat = (iPhoneX ? 88 : 64)

//顶部状态栏高度
let IOS11_TOP_STATUSBAR_HEIGHT = iPhoneX ? 44 : 20
//底部安全高度
let IPHONEX_BOTTOM_SAFE_HEIGHT: CGFloat = 34.0





func RGB(_ r:Float,g:Float,b:Float)->UIColor{
    return UIColor(red: CGFloat(r/255.0), green: CGFloat(g/255.0), blue:CGFloat(b/255.0), alpha: 1)
}

//返回字指定名称和大小的字体
func ZFont(_ name:String,_ size:CGFloat) -> UIFont {
    return UIFont(name: name, size: size)!
}

//打印字体列表
func printFonts() {
    let fontFamilyNames = UIFont.familyNames
    for familyName in fontFamilyNames {
        print("------------------------------")
        print("Font Family Name = [\(familyName)]")
        let names = UIFont.fontNames(forFamilyName: familyName)
        print("Font Names = [\(names)]")
    }
}



//设置页面字体
let setting_item_textFont = UIFont(name: "PingFangTC-Regular", size: 14.0)
let setting_value_textFont = UIFont(name: "PingFangSC-Light", size: 12.0)
let setting_value_textColor = RGB(85, g: 85, b: 85)
let setting_content_textFont = UIFont(name: "PingFangSC-Light", size: 12.0)
let setting_content_textColor = RGB(155, g: 155, b: 155)




//基本颜色
var base_color: UIColor {
    get {
        return O2ThemeManager.color(for: "Base.base_color")!
    }
}
let base_blue_color = UIColor.init(hex: "#008be6")

let base_gray_color = RGB(255.0, g: 255.0, b: 255.0)
let base_gray_background_color = RGB(155.0,g:155.0,b:155.0)

let toolbar_background_color = RGB(247,g:247,b:247)
let toolbar_text_color = RGB(108,g: 108,b: 108)
let toolbar_text_font = UIFont(name: "PingFangSC-Regular", size: 14.0)!

let navbar_tint_color = UIColor.white
var navbar_barTint_color: UIColor {
    get {
        return O2ThemeManager.color(for: "Base.base_color")!
    }
}
let navbar_item_font = toolbar_text_font
//导航栏标题字体
let navbar_text_font = UIFont(name: "PingFangSC-Regular", size: 15.0)!





var PROJECTMODE = 0


//随机字符串
class RandomString {
    let characters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    
    /**
     生成随机字符串,
     
     - parameter length: 生成的字符串的长度
     
     - returns: 随机生成的字符串
     */
    func getRandomStringOfLength(length: Int) -> String {
        var ranStr = ""
        for _ in 0..<length {
            let index = Int(arc4random_uniform(UInt32(characters.count)))
            ranStr.append(characters[characters.index(characters.startIndex, offsetBy: index)])
        }
        return ranStr
        
    }
    
    
    private init() {
        
    }
    static let sharedInstance = RandomString()
}


//显示进度指示





