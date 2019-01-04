//
//  Common.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/18.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import SwiftValidator
import SwiftyUserDefaults
import CocoaLumberjack
import O2OA_Auth_SDK

// 当前系统版本
let kCersion = (UIDevice.current.systemVersion as NSString).floatValue
// 屏幕宽度
let kScreenH = UIScreen.main.bounds.height
// 屏幕高度
let kScreenW = UIScreen.main.bounds.width


// MARK:- 自定义手机号码检校规则
public class MobileNumberRule: RegexRule {
    
    static let regex = "^\\d{11}$"
    
    convenience init(message : String = "Not a valid Mobile Number"){
        self.init(regex: MobileNumberRule.regex, message : message)
    }
}



// MARK:- 所有配置定义，使用SwiftyUserDefaults实现存储
protocol AppConfigEnable {
    
    //是否第一次使用应用，显示引导页使用
    var isFirstTime:Bool? { get set }
    // IM是否已经登录
    var openIMLoginStatus:Bool? { get set }
    // 跳转用的 从那个页面过来的
    var taskIndex: Int {get set}
    var appBackType: Int {get set}
    var notificationGranted: Bool {get set}
    var firstGranted: Bool {get set}
    var accountIsSecurity: Bool {get set}
    var firstLoad: Bool {get set}
    //服务器 移动端配置hash值 判断是否更新了
    var customStyleHash: String {get set}
}


// MARK:- 扩展定义的键
extension DefaultsKeys {
    static let isFirstTime = DefaultsKey<Bool?>("isFirstTime")
    static let openIMLoginStatus = DefaultsKey<Bool?>("openIMLoginStatus")
    static let taskIndex = DefaultsKey<Int>("taskIndex")
    static let appBackType = DefaultsKey<Int>("appBackType")
    static let notificationGranted = DefaultsKey<Bool>("notificationGranted")
    static let firstGranted = DefaultsKey<Bool>("fristGranted")
    static let accountIsSecurity = DefaultsKey<Bool>("accountIsSecurity")
    static let firstLoad = DefaultsKey<Bool>("firstLoad")
    static let customStyleHash = DefaultsKey<String>("customStyleHash")
}

// MARK:- Default App Config
public class AppConfigSettings: AppConfigEnable {
    
    var customStyleHash: String {
        get {
            return Defaults[.customStyleHash]
        }
        set {
            Defaults[.customStyleHash] = newValue
        }
    }
  
    var openIMLoginStatus: Bool?{
        get  {
            guard let openIMLoginStatus = Defaults[.openIMLoginStatus] else {
                return false
            }
            return openIMLoginStatus
        }
        set {
            Defaults[.openIMLoginStatus] = newValue
        }
    }
    
    var isFirstTime: Bool? {
        get {
            guard let firstTime = Defaults[.isFirstTime] else {
                return true
            }
            return firstTime
        }
        
        set {
            Defaults[.isFirstTime] = newValue
        }
    }
    
    var notificationGranted: Bool {
        get {
            return Defaults[.notificationGranted]
        }
        
        set {
            Defaults[.notificationGranted] = newValue
        }
    }
    var firstGranted: Bool {
        get {
            
            return Defaults[.firstGranted]
        }
        
        set {
            Defaults[.firstGranted] = newValue
        }
    }
    
    var accountIsSecurity: Bool {
        get {
            return Defaults[.accountIsSecurity]
        }
        
        set {
            Defaults[.accountIsSecurity] = newValue
        }
    }
    
    var firstLoad: Bool {
        get {
            return Defaults[.firstLoad]
        }
        
        set {
            Defaults[.firstLoad] = newValue
        }
    }
    
    var taskIndex: Int {
        get {
            return Defaults[.taskIndex]
        }
        
        set {
            Defaults[.taskIndex] = newValue
        }
    }
    
    var appBackType: Int {
        get {
            return Defaults[.appBackType]
        }
        
        set {
            Defaults[.appBackType] = newValue
        }
    }
    
    
    
    public func removeAllConfig() {
        Defaults.removeAll()
    }
    
    static let shared:AppConfigSettings = {
        return AppConfigSettings()
    }()
}

// MARK:- OOCustomImageManager

enum OOCustomImageKey:NSString {
    case launch_logo = "launch_logo"
    case login_avatar = "login_avatar"
    case index_bottom_menu_logo_blur = "index_bottom_menu_logo_blur"
    case index_bottom_menu_logo_focus = "index_bottom_menu_logo_focus"
    case people_avatar_default = "people_avatar_default"
    case process_default = "process_default"
    case setup_about_logo = "setup_about_logo"
}

class OOCustomImageManager {
    
    static let `default`:OOCustomImageManager = {
        return OOCustomImageManager()
    }()
    
    private var imageCache = NSCache<NSString,UIImage>()
    
    private init() {
        if let configInfo = O2AuthSDK.shared.customStyle() {
            configInfo.images?.forEach({ (ooImage) in
                let value = ooImage.value!
                let data = Data(base64Encoded: value)
                let image = UIImage(data: data!)
                let scaleImage = image?.scaledImageFrom3x()
                imageCache.setObject(scaleImage!, forKey: ooImage.name! as NSString)
            })
        }
    }
    
    func loadImage(_ key:OOCustomImageKey) -> UIImage? {
        if let image = imageCache.object(forKey: key.rawValue) {
            return image
        }
        return UIImage(named: "icon_zhuye_pre")
    }
    
   
    
    
    
}

