//
//  OOTabBarHelper.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/12.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOTabBarHelper: NSObject {
    
    
    static func getVC(storyboardName:String,vcName:String?) -> UIViewController {
        let storyBoard:UIStoryboard = UIStoryboard.init(name: storyboardName, bundle: nil)
        var destVC:UIViewController!
        if vcName != nil {
            destVC = storyBoard.instantiateViewController(withIdentifier: vcName!)
        }else{
            destVC = storyBoard.instantiateInitialViewController()
        }
        return destVC
    }
    
    static func initTabBarStyle() {
        UITabBarItem.appearance().setTitleTextAttributes([
            NSAttributedString.Key.font: UIFont.init(name: "PingFangSC-Regular", size: 12) ?? UIFont.systemFont(ofSize: 12),
            NSAttributedString.Key.foregroundColor: UIColor(hex: "#666666")], for: .normal)
        
        UITabBarItem.appearance().setTitleTextAttributes([
            NSAttributedString.Key.font: UIFont.init(name: "PingFangSC-Regular", size: 12) ?? UIFont.systemFont(ofSize: 12),
            NSAttributedString.Key.foregroundColor: O2ThemeManager.color(for: "Base.base_color")!], for: .selected)
        
        UITabBar.appearance().backgroundColor = UIColor(hex: "#F7F7F7")
    }


}
