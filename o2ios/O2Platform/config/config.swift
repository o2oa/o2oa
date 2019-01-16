//
//  config.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation


let SCREEN_WIDTH:CGFloat = UIScreen.main.bounds.width;

let SCREEN_HEIGHT:CGFloat = UIScreen.main.bounds.height;

let iPhoneX = (SCREEN_WIDTH == 375 && SCREEN_HEIGHT == 812) ? true : false

let safeAreaTopHeight:CGFloat = (SCREEN_HEIGHT == 812.0 ? 88 : 64)

let IOS11_TOP_STATUSBAR_HEIGHT = isIPhoneX ? 44 : 20


let NAV_HEIGHT = iPhoneX ? (44 + 44) : 64

//是否连接到collect服务器 内网版本false 需要在Info.plist配置服务器信息
let O2IsConnect2Collect = true


func RGB(_ r:Float,g:Float,b:Float)->UIColor{
    //return UIColor(colorLiteralRed: r/255.0, green: g/255.0, blue: b/255.0, alpha: 1)
    //return UIColor(displayP3Red: CGFloat(r/255.0), green: CGFloat(g/255.0), blue:CGFloat(b/255.0), alpha: 1)
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
let base_color = RGB(251.0,g:71.0,b:71.0)
let base_blue_color = UIColor.init(hex: "#008be6")

let base_gray_color = RGB(255.0, g: 255.0, b: 255.0)
let base_gray_background_color = RGB(155.0,g:155.0,b:155.0)

let toolbar_background_color = RGB(247,g:247,b:247)
let toolbar_text_color = RGB(108,g: 108,b: 108)
let toolbar_text_font = UIFont(name: "PingFangSC-Regular", size: 14.0)!

let navbar_tint_color = UIColor.white
let navbar_barTint_color = base_color
let navbar_item_font = toolbar_text_font
//导航栏标题字体
let navbar_text_font = UIFont(name: "PingFangSC-Regular", size: 15.0)!


//一些常量
//友盟及蒲公英相应的KEY
let PGY_APP_ID = "84492f465f8f4fb11b8486dc50bf4a38"
let PGY_APP_ID_SZSLB = "31e1d7cd0acb4424baf6ce4d5cad4833"
let PGY_APP_ID_HZCGW = "ce74b24c98391b3e16b8b699b4d5a9c5"
let PGY_APP_ID_HLJDX = "a106e4554c23e726c251389291fd8d4c"
let UM_APP_ID = "57f88c7ce0f55a657200241d"
let UM_APP_ID_SZSLB = "59ad05ac7f2c7423980019c2"
let UM_APP_ID_HZCGW = "59e8654aaed1796396000331"
let UM_APP_ID_HLJDX = "59f5e912f43e487cbe000048"
//百度 语音
let BD_SPEECH_APP_ID = "11799270"
let BD_SPEECH_API_KEY = "FDWx2rKYgYsy8wfzmNf7GMNA"
let BD_SPEECH_SECRET_KEY = "Z6NkswYlI6NCux7wUpIxuLldI1MCqgjl"

//O2服务的厂商，0表示O2自身，1表示神舟顺利办，2表示杭州城管委，3表示黑龙江电信
var PROJECTMODE = 0

//用户是否绑定
let IS_BINDED_KEY = "BIND_SUCCESS"

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
            let index = Int(arc4random_uniform(UInt32(characters.characters.count)))
            ranStr.append(characters[characters.index(characters.startIndex, offsetBy: index)])
        }
        return ranStr
        
    }
    
    
    private init() {
        
    }
    static let sharedInstance = RandomString()
}


//显示进度指示





