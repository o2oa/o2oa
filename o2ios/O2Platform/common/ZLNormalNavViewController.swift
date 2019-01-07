//
//  ZLNormalNavViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/18.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class ZLNormalNavViewController: UINavigationController {

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
        let offset = UIOffset(horizontal:0, vertical: 0)
        barItem.setBackButtonTitlePositionAdjustment(offset, for: .default)
        barItem.setTitleTextAttributes([NSAttributedString.Key.font:navbar_item_font,NSAttributedString.Key.foregroundColor:navbar_tint_color], for:UIControl.State())
        
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    

}
