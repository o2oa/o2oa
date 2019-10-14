//
//  ZLNavigationController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/16.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class ZLNavigationController: UINavigationController {
    

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationBar.isTranslucent = false
        self.navigationBar.barTintColor = navbar_barTint_color
        self.navigationBar.tintColor = navbar_tint_color
        self.navigationBar.titleTextAttributes = [NSAttributedString.Key.font:navbar_text_font,NSAttributedString.Key.foregroundColor:navbar_tint_color]
        
        self.toolbar.barTintColor = navbar_barTint_color
        self.toolbar.tintColor = navbar_tint_color
        self.toolbar.barStyle = .default
        //        //隐藏返回按钮文字
        let barItem = UIBarButtonItem.appearance()
        let offset = UIOffset(horizontal: -200, vertical: 0)
        barItem.setBackButtonTitlePositionAdjustment(offset, for: .default)
        barItem.setTitleTextAttributes([
            NSAttributedString.Key.font:navbar_item_font,
            NSAttributedString.Key.foregroundColor:navbar_tint_color
            ], for:UIControl.State())
        
    }
    
    override func pushViewController(_ viewController: UIViewController, animated: Bool) {
        if viewControllers.count > 0 {
            viewController.hidesBottomBarWhenPushed = true
        }
        super.pushViewController(viewController, animated: animated)
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
//    override func childViewControllerForStatusBarStyle() -> UIViewController? {
//        return self.topViewController
//    }
  
}
