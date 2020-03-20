//
//  OONewAttanceNavController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/14.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OONewAttanceNavController: ZLNavigationController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let barItem = UIBarButtonItem.appearance(whenContainedInInstancesOf: [OONewAttanceNavController.self])
        let offset = UIOffset(horizontal: 0, vertical: 0)
        barItem.setBackButtonTitlePositionAdjustment(offset, for: .default)
        barItem.setTitleTextAttributes([NSAttributedString.Key.font:navbar_item_font,NSAttributedString.Key.foregroundColor:navbar_tint_color], for:UIControl.State())
        
    }
    
    @objc func closeWindow() {
        
    }

}
