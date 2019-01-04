//
//  ZoneHUD.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/16.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit
import JGProgressHUD

class ZoneHUD: NSObject {
    
    private static var hud:JGProgressHUD {
        struct HUDWrapper {
            static let myHUD = JGProgressHUD(style: .light)
        }
        HUDWrapper.myHUD.interactionType = .blockAllTouches
        HUDWrapper.myHUD.animation = JGProgressHUDFadeZoomAnimation()
        HUDWrapper.myHUD.backgroundColor = UIColor.init(white: 0.0, alpha: 0.2)
        return HUDWrapper.myHUD
    }
    
    public class func showNormalHUD(_ parentView:UIView,_ text:String = "loading...") {
        ZoneHUD.hud.indicatorView = JGProgressHUDIndicatorView()
        ZoneHUD.hud.textLabel.text = text
        ZoneHUD.hud.show(in: parentView, animated: true)
    }
    
    public class func showSuccessHUD(successText text:String = "success",_ afterDelay:Double = 0.5){
        hud.textLabel.text = text
        hud.layoutChangeAnimationDuration = 0.3
        hud.indicatorView = JGProgressHUDSuccessIndicatorView()
        Timer.after(afterDelay) {
            //perationQueue.main.addOperation {
                hud.dismiss()
            //}
        }
    }
    
    public class func showErrorHUD(errorText text:String = "error",_ afterDelay:Double = 0.5){
        hud.textLabel.text = text
        hud.layoutChangeAnimationDuration = 0.3
        hud.indicatorView = JGProgressHUDErrorIndicatorView()
        Timer.after(afterDelay) {
            //DispatchQueue.main.async {
                ZoneHUD.hud.dismiss()
            //}
        }
    }
    
    public class func dismissNormalHUD(){
        hud.dismiss()
    }
}
