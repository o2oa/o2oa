//
//  OOTabBarHelper.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/12.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CYLTabBarController

class OOTabBarHelper: NSObject {
    
    static func viewControllers() -> [ZLNavigationController] {
        
        //消息
        let conversationVC = JCConversationListViewController()
        conversationVC.title  = "消息"
        let messages = ZLNavigationController(rootViewController: conversationVC)
        
        //通讯录
        let addressVC = OOTabBarHelper.getVC(storyboardName: "contacts", vcName: nil)
        let address = ZLNavigationController(rootViewController: addressVC)
        
        //应用
        let appsVC = OOTabBarHelper.getVC(storyboardName: "apps", vcName: nil)
        let apps = ZLNavigationController(rootViewController: appsVC)
        
        //设置
        let settingsVC = OOTabBarHelper.getVC(storyboardName: "setting", vcName: nil)
        let settings =   ZLNavigationController(rootViewController: settingsVC)
        let viewControllers = [messages, address, apps, settings]
        return viewControllers
    }
    
    
    static func tabBarItemsAttributesForController() ->  [[String : String]] {
        
        let tabBarItemOne = [CYLTabBarItemTitle:"消息",
                             CYLTabBarItemImage:"message_normal",
                             CYLTabBarItemSelectedImage:"message_selected"]
        
        let tabBarItemTwo = [CYLTabBarItemTitle:"通讯录",
                             CYLTabBarItemImage:"address_normal",
                             CYLTabBarItemSelectedImage:"address_selected"]
        
        let tabBarItemThree = [CYLTabBarItemTitle:"应用",
                               CYLTabBarItemImage:"apps_normal",
                               CYLTabBarItemSelectedImage:"apps_selected"]
        
        let tabBarItemFour = [CYLTabBarItemTitle:"设置",
                              CYLTabBarItemImage:"setting_normal",
                              CYLTabBarItemSelectedImage:"setting_selected"]
        
        let tabBarItemsAttributes = [tabBarItemOne,tabBarItemTwo,tabBarItemThree,tabBarItemFour]
        return tabBarItemsAttributes
    }
    
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
            NSAttributedString.Key.foregroundColor: base_color], for: .selected)
        
        UITabBar.appearance().backgroundColor = UIColor(hex: "#F7F7F7")
    }


}
