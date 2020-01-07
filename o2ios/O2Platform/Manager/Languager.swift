//
//  Languager.swift
//  icar
//
//  Created by gongkai on 15/5/18.
//  Copyright (c) 2015年 anytracking. All rights reserved.
//
import UIKit
private let kUserLanguage = "AppleLanguages"

/**
 *  国际化工具
 */
class Languager: NSObject {
    private struct Static {
        static var staticInstance : Languager? = nil
    }
    private var _currentLanguage:String?
    
    override init() {
        super.init()
        self.initLanguages()
    }
    
    //当前语言Bundle
    internal var currentLanguageBundle: Bundle?
    
    
    // 当前语言获取与切换
    var currentLanguage:String{
        get{
            if(self._currentLanguage==nil){
                self._currentLanguage = (UserDefaults.standard.array(forKey:  kUserLanguage))?.first as? String
                //TODO 现在的语言带后缀的 不知道怎么处理 临时去掉后缀
                self._currentLanguage = self._currentLanguage?.replacingOccurrences(of: "-CN", with: "")
                self._currentLanguage = self._currentLanguage?.replacingOccurrences(of: "-US", with: "")
                if self._currentLanguage != "zh-HK" {
                    self._currentLanguage = self._currentLanguage?.replacingOccurrences(of: "-HK", with: "")
                }
                self._currentLanguage = self._currentLanguage?.replacingOccurrences(of: "-HK", with: "")
                self._currentLanguage = self._currentLanguage?.replacingOccurrences(of: "-TW", with: "")
            }
            return self._currentLanguage!
        }
        set(newLanguage){
            if(self._currentLanguage == newLanguage){
                return
            }
            if let path =  Bundle.main.path(forResource:newLanguage, ofType: "lproj"), let bundel = Bundle(path: path){
                self.currentLanguageBundle = bundel
                self._currentLanguage = newLanguage
            }else{
                //如果不支持当前语言则加载info中Localization native development region中的值的lporj

                let defaultLanguage = "zh-Hans"
                self.currentLanguageBundle =  Bundle(path: Bundle.main.path(forResource: defaultLanguage, ofType: "lproj" )!)
                self._currentLanguage = defaultLanguage
            }
            let def = UserDefaults.standard
            def.setValue([self._currentLanguage!], forKey:kUserLanguage)
            def.synchronize()
            
            Bundle.main.onLanguage()
        }
    }
    
    // 单列
    class func standardLanguager() -> Languager{
        DispatchQueue.once(token: "standardLanguager") {
            Static.staticInstance = Languager()
        }
        return Static.staticInstance!
    }
    
    //初始化
    func initLanguages(){
        var language =  (UserDefaults.standard.array(forKey:  kUserLanguage))?.first as? String
        //TODO 现在的语言带后缀的 不知道怎么处理 临时去掉后缀
        language = language?.replacingOccurrences(of: "-CN", with: "")
        language = language?.replacingOccurrences(of: "-US", with: "")
        if language != "zh-HK" {
            language = language?.replacingOccurrences(of: "-HK", with: "")
        }
        language = language?.replacingOccurrences(of: "-TW", with: "")
        if let path = Bundle.main.path(forResource: language, ofType: "lproj" ), let bundel = Bundle(path: path){
            self.currentLanguageBundle = bundel
            self._currentLanguage = language
        }else{
            //如果不支持当前语言则加载info中Localization native development region中的值的lporj,设置为当前语言
            self.currentLanguage = "zh-Hans"
            print("Languager:\(String(describing: language))不支持，切换成默认语言\(self._currentLanguage!)")
        }
    }
    
    /**
     获取当前语言的storyboard
     */
    func storyboard(name:String)-> UIStoryboard{
        return UIStoryboard(name: name, bundle: self.currentLanguageBundle)
    }
    
    /**
     获取当前语言的nib
     */
    func nib(name:String)->UINib{
        return UINib(nibName: name, bundle: self.currentLanguageBundle)
    }
    
    /**
     获取当前语言的string
     */
    func string(key:String)->String{
        if let str = self.currentLanguageBundle?.localizedString(forKey: key, value: nil, table: nil) {
            return str
        }
        return key
    }
    
    /**
     获取当前语言的image
     */
    func image(name:String)->UIImage?{
        let path = self.currentLanguageBundle?.path(forResource: name+"@2x", ofType: "png")
        return UIImage(contentsOfFile: path!)
    }
}

func localized(key:String)->String{
    return Languager.standardLanguager().string(key: key)
}

func localizedImage(key:String)->UIImage?{
    return Languager.standardLanguager().image(name: key)
}
