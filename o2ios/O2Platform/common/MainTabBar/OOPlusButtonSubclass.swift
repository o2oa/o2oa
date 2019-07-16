//
//  OOPlusButton.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/12.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CYLTabBarController
import CocoaLumberjack
import O2OA_Auth_SDK

class OOPlusButtonSubclass: CYLPlusButton,CYLPlusButtonSubclassing {
    
    
    static func plusButton() -> Any! {
        let button = OOPlusButtonSubclass()
        let normalImage = OOCustomImageManager.default.loadImage(.index_bottom_menu_logo_blur)
        let selectedImage = OOCustomImageManager.default.loadImage(.index_bottom_menu_logo_focus)
        button.frame.size.width = 80
        button.frame.size.height = 80
        button.setImage(normalImage, for: .normal)
        button.setImage(selectedImage, for: .selected)
        button.isSelected = true
        button.titleLabel?.textAlignment = .center
        
        button.titleLabel?.font = UIFont.init(name: "PingFangSC-Regular", size: 12)
        
        button.setTitle("首页", for: .normal)
        button.setTitleColor(UIColor(hex:"#666666"), for: .normal)
        
        button.setTitle("首页", for: .selected)
        button.theme_setTitleColor(ThemeColorPicker(keyPath: "Base.base_color"), forState: .selected)
        
        //button.adjustsImageWhenHighlighted = false
        button.sizeToFit()
        return button
    }
    
    static func indexOfPlusButtonInTabBar() -> UInt {
        return 2
    }
    
    static func multiplier(ofTabBarHeight tabBarHeight: CGFloat) -> CGFloat {
        return 0.3
    }
    
    static func constantOfPlusButtonCenterYOffset(forTabBarHeight tabBarHeight: CGFloat) -> CGFloat {
        return 0
    }
    
    static func plusChildViewController() -> UIViewController! {
//        let vc = PublishViewController()
//        let nav = UINavigationController(rootViewController: vc)
//        return nav
        
        let appid = O2AuthSDK.shared.customStyle()?.indexPortal
        let indexType = O2AuthSDK.shared.customStyle()?.indexType ?? "default"
        if indexType == "portal" {
            let app = OOAppsInfoDB.shareInstance.queryData(appid!)
            let destVC = OOTabBarHelper.getVC(storyboardName: "apps", vcName: "OOMainWebVC")
            MailViewController.app = app
            (destVC as? MailViewController)?.isIndexShow = true
            let nav = ZLNavigationController(rootViewController: destVC)
            return nav
        }else{
            let destVC = OOTabBarHelper.getVC(storyboardName: "task", vcName: nil)
            let nav = ZLNavigationController(rootViewController: destVC)
            return nav
        }
    }
    
    
    
    static func shouldSelectPlusChildViewController() -> Bool {
        return true
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        // tabbar UI layout setup
        let imageViewEdgeWidth:CGFloat  = self.bounds.size.width * 0.75
        let imageViewEdgeHeight:CGFloat = imageViewEdgeWidth * 0.9
        
        let centerOfView    = self.bounds.size.width * 0.5
        let labelLineHeight = self.titleLabel!.font.lineHeight
        let verticalMargin = (self.bounds.size.height - labelLineHeight - imageViewEdgeHeight ) * 0.5
        
        let centerOfImageView = verticalMargin + imageViewEdgeHeight * 0.5
        let centerOfTitleLabel = imageViewEdgeHeight + verticalMargin * 2  + labelLineHeight * 0.5 + 10
        
        //imageView position layout
        self.imageView!.bounds = CGRect(x:0, y:0, width:imageViewEdgeWidth, height:imageViewEdgeHeight)
        self.imageView!.center = CGPoint(x:centerOfView, y:centerOfImageView)
        
        //title position layout
        self.titleLabel!.bounds = CGRect(x:0, y:0, width:self.bounds.size.width,height:labelLineHeight)
        self.titleLabel!.center = CGPoint(x:centerOfView, y:centerOfTitleLabel)
        
    }
    
    

}
