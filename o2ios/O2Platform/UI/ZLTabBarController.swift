//
//  ZLTabBarController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/16.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class ZLTabBarController:UITabBarController {
    
    private var currentIndex:Int = -1
    


    override func viewDidLoad() {
        super.viewDidLoad()
        tabBar.isTranslucent = false //设置标签不透明
        tabBar.barTintColor = toolbar_background_color
        let item = UITabBarItem.appearance()
        item.setTitleTextAttributes([NSAttributedString.Key.foregroundColor:toolbar_text_color,NSAttributedString.Key.font:toolbar_text_font], for:UIControl.State())
        item.setTitleTextAttributes([NSAttributedString.Key.foregroundColor:O2ThemeManager.color(for: "Base.base_color")!,NSAttributedString.Key.font:toolbar_text_font], for: .selected)
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    
    
    override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
    }
    
    
}
