//
//  ZoneNavigationBarManager.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/10.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class ZoneNavigationBarManager: NSObject {
    
    //NavigationBar background color, default is white
    open var barColor:UIColor!
    //NavigationBar subviews color
    open var tintColor:UIColor!
    //default is nil
    open var backgroundImage:UIImage!
    // default is UIStatusBarStyleDefault
    open var statusBarStyle:UIStatusBarStyle!
    //color will changed begin this offset, default is -64
    open var zeroAlphaOffset:Float = 0.0
    //color alpha will be 1 in this offset, default is 200
    open var fullAlphaOffset:Float = 0.0
    //bar minAlpha, default is 0
    open var minAlphaValue:Float = 0.0
    //bar maxAlpha, default is 1
    open var maxAlphaValue:Float = 1.0
    //if you set this property, the tintColor will changed in fullAlphaOffset
    open var fullAlphaTintColor:UIColor!
    //if you set this property, the barStyle will changed in fullAlphaOffset
    open var fullAlphaBarStyle:UIStatusBarStyle!
    //if allchange = yes, the tintColor will changed with the barColor change, default is yes, if you only want to change barColor, set allChange = NO
    open var allChange:Bool = true
    //this will cause that if currentAlpha = 0.3,it will be 1 - 0.3 = 0.7
    open var reversal:Bool = true
    //when continues = YES, bar color will changed whenever you scroll, if you set continues = NO,it only be changed in the fullAlphaOffset
    open var continues:Bool = true
    
    
    //private const
    private static let kNavigationBarHeight:Float = 64.0
    
    private static let kDefaultFullOffset:Float = 200.0
    
    private static let kMaxAlphaValue:Float = 0.995
    
    private static let kMinAlphaValue:Float = 0.0
    
    private static let kDefaultAnimationTime:Float = 0.35
    
    //store source
    private var selfNavigationBar:UINavigationBar!
    
    private var selfNavigationController:UINavigationController!
    
    private var saveIsTranslucent:Bool = true
    
    private var saveImage:UIImage!
    
    private var saveColor:UIColor!
    
    private var saveTintColor:UIColor!
    
    private var saveTitleAttributes:[NSAttributedString.Key:Any]!
    
    private var saveBarStyle:UIStatusBarStyle!
    
    private var setFull:Bool = true
    
    private var setZero:Bool = true
    
    private var setChange:Bool = true
    
    //单例
    private class var sharedManager:ZoneNavigationBarManager {
        struct SingletonWrapper {
            static let singleton = ZoneNavigationBarManager()
        }
        return SingletonWrapper.singleton
    }
    
    private override init(){
        super.init()
        self.maxAlphaValue = ZoneNavigationBarManager.kMaxAlphaValue
        self.minAlphaValue = ZoneNavigationBarManager.kMinAlphaValue
        self.fullAlphaOffset = Float(ZoneNavigationBarManager.kDefaultFullOffset)
        self.zeroAlphaOffset = -(Float)(ZoneNavigationBarManager.kNavigationBarHeight)
        self.setZero = true
        self.setFull = true
        self.allChange = true
        self.continues = true
        //self.initBaseData(self)
        
    }
    
    private func initBaseData(_ manager:ZoneNavigationBarManager){
        manager.maxAlphaValue = ZoneNavigationBarManager.kMaxAlphaValue
        manager.minAlphaValue = ZoneNavigationBarManager.kMinAlphaValue
        manager.fullAlphaOffset = Float(ZoneNavigationBarManager.kDefaultFullOffset)
        manager.zeroAlphaOffset = -(Float)(ZoneNavigationBarManager.kNavigationBarHeight)
        manager.setZero = true
        manager.setFull = true
        manager.allChange = true
        manager.continues = true
    }
    
    open class func setBarColor(_ color:UIColor){
        sharedManager.barColor = color
    }
    
    open class func setTintColor(_ color:UIColor){
        sharedManager.tintColor = color
        sharedManager.selfNavigationBar.tintColor = color
        setTitleColorWithColor(color)
        //setTintColor(color)
        
    }
    
    open class func setBackgroudImage(_ image:UIImage){
        sharedManager.selfNavigationBar.setBackgroundImage(image, for: .default)
    }
    
    open class func setStatusBarStyle(_ style:UIStatusBarStyle){
        sharedManager.statusBarStyle = style
        UIApplication.shared.setStatusBarStyle(style, animated: true)
    }
    
    open class func setZeroAlphaOffset(_ offset:Float){
        sharedManager.zeroAlphaOffset = offset
    }
    
    open class func setFullAlphaOffset(_ offset:Float){
        sharedManager.fullAlphaOffset = offset
    }
    
    open class func setMaxAlphaValue(_ value:Float){
        let newValue = value > kMaxAlphaValue ? kMaxAlphaValue : value
        sharedManager.maxAlphaValue = newValue
    }
    
    open class func setMinAlphaValue(_ value:Float){
        let newValue = value < kMinAlphaValue ? kMinAlphaValue : value
        sharedManager.minAlphaValue = newValue
    }
    
    open class func setFullAlphaTintColor(_ color:UIColor){
        sharedManager.fullAlphaTintColor = color
    }
    
    open class func setFullAlphaBarStyle(_ style:UIStatusBarStyle){
        sharedManager.fullAlphaBarStyle = style
    }
    
    open class func setAllChange(_ allChange:Bool){
        sharedManager.allChange = allChange
    }
    
    open class func setReversal(_ reversal:Bool){
        sharedManager.reversal = reversal
    }
    
    open class func setContinus(_ continues:Bool){
        sharedManager.continues = continues
    }
    
    open class func managerWithController(_ viewController:UIViewController){
        let navigationBar = viewController.navigationController?.navigationBar
        sharedManager.saveIsTranslucent = (navigationBar?.isTranslucent)!
        sharedManager.selfNavigationController = viewController.navigationController
        sharedManager.selfNavigationBar = navigationBar
        sharedManager.selfNavigationBar.isTranslucent = true
        sharedManager.selfNavigationBar.setTitleVerticalPositionAdjustment(-100, for: .default)
        //保存原始数据
        saveOriginal(navigationBar!)
        navigationBar?.setBackgroundImage(UIImage(), for: .default)
        navigationBar?.shadowImage = UIImage()
    }
    
    
    open class func changeAlphaWithCurrentOffset(_ currentOffset:Float){
        let manager = sharedManager
        let currentAlpha = currentAlphaForOffset(currentOffset)
        if(!manager.barColor.isEqual(UIColor.clear)){
            if(!manager.continues){
                if(currentAlpha == manager.minAlphaValue){
                    setNavigationBarColorWithAlpha(manager.minAlphaValue)
                }else if(currentAlpha == manager.maxAlphaValue){
                    UIView.animate(withDuration: TimeInterval(kDefaultAnimationTime), animations: {
                        setNavigationBarColorWithAlpha(manager.maxAlphaValue)
                    })
                    manager.setChange = true
                }else{
                    if(manager.setChange){
                        UIView.animate(withDuration: TimeInterval(kDefaultAnimationTime), animations: {
                            setNavigationBarColorWithAlpha(manager.minAlphaValue)
                        })
                        manager.setChange = false
                    }
                }
            }else{
                setNavigationBarColorWithAlpha(currentAlpha)
            }
        }
        
        if(manager.allChange) {
            changeTintColorWithOffset(currentAlpha)
        }
    }
    
    
    
    open class func reStoreToSystemNavigationBar(){
        sharedManager.selfNavigationBar.setBackgroundImage(sharedManager.saveImage, for: .default)
        //sharedManager.selfNavigationBar.isTranslucent = sharedManager.saveIsTranslucent
        sharedManager.selfNavigationBar.barTintColor = sharedManager.saveColor
        sharedManager.selfNavigationBar.tintColor = sharedManager.saveTintColor
        sharedManager.selfNavigationBar.titleTextAttributes = sharedManager.saveTitleAttributes
        sharedManager.selfNavigationBar.setTitleVerticalPositionAdjustment(0, for: .default)
        //sharedManager.selfNavigationController.setValue([UINavigationBar.init()], forKey: "navigationBar")
        //sharedManager.selfNavigationController.setValue([UINavigationBar()], forKey: "navigationBar")
    }
    
    private class func saveOriginal(_ navBar:UINavigationBar){
        sharedManager.saveImage = navBar.backgroundImage(for: .default)
        sharedManager.saveColor = navBar.barTintColor
        sharedManager.saveTintColor = navBar.tintColor
        sharedManager.saveTitleAttributes = navBar.titleTextAttributes ?? nil
    }
    
    private class func currentAlphaForOffset(_ offset:Float) -> Float{
        let manager = sharedManager
        var currentAlpha = (offset - manager.zeroAlphaOffset) / (manager.fullAlphaOffset - manager.zeroAlphaOffset)
        currentAlpha = currentAlpha < manager.minAlphaValue ? manager.minAlphaValue : (currentAlpha > manager.maxAlphaValue ? manager.maxAlphaValue : currentAlpha)
        currentAlpha = manager.reversal ? manager.maxAlphaValue + manager.minAlphaValue - currentAlpha : currentAlpha
        return currentAlpha
    }
    
    private class func changeTintColorWithOffset(_ currentAlpha:Float){
        let manager  = sharedManager
        if (currentAlpha >= manager.maxAlphaValue && manager.fullAlphaTintColor != nil){
            if(manager.setFull){
                manager.setFull = false
                manager.setZero = true
            }else{
                if(manager.reversal){
                    manager.setFull = true
                }
                return
            }
            manager.selfNavigationBar.tintColor = manager.fullAlphaTintColor
            setTitleColorWithColor(manager.fullAlphaTintColor)
            setUIStatusBarStyle(manager.fullAlphaBarStyle)
        }else if(manager.tintColor != nil){
            if(manager.setZero){
                manager.setZero = false
                manager.setFull = true
            }else{
                return
            }
            manager.selfNavigationBar.tintColor = manager.tintColor
            setTitleColorWithColor(manager.tintColor)
            setUIStatusBarStyle(manager.statusBarStyle)
        }
    }
    
    private class func setTitleColorWithColor(_ color:UIColor){
        if var  textAttr  = sharedManager.selfNavigationBar!.titleTextAttributes {
            textAttr[NSAttributedString.Key.foregroundColor] = color
            self.sharedManager.selfNavigationBar.titleTextAttributes = textAttr
        }else{
            self.sharedManager.selfNavigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor:color]
        }
        
    }
    
    private class func setNavigationBarColorWithAlpha(_ alpha:Float){
        let manager = sharedManager
        setBackgroudImage(imageWithColor(manager.barColor.withAlphaComponent(CGFloat(alpha))))
    }
    
    private class func setUIStatusBarStyle(_ style:UIStatusBarStyle){
        UIApplication.shared.setStatusBarStyle(style, animated: true)
    }
    
    private class func imageWithColor(_ color:UIColor) -> UIImage{
        let rect = CGRect(x: 0, y: 0, width: 1, height: 1)
        UIGraphicsBeginImageContext(rect.size)
        let context = UIGraphicsGetCurrentContext()
        color.setFill()
        context?.fill(rect)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image!
    }
    
}
