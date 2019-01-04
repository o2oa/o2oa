//
//  JCMainTabBarController.swift
//  JChat
//
//  Created by deng on 2017/2/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCMainTabBarController: UITabBarController {

    //MARK: - life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupChildControllers()
    }

    //MARK: - private func
    private func setupChildControllers() {
        // 会话
        let conversationVC = JCConversationListViewController()
        conversationVC.title = "会话"
        let chatTabBar = UITabBarItem(title: "会话",
                                      image: UIImage.loadImage("com_icon_chat")?.withRenderingMode(.alwaysOriginal),
                                      selectedImage: UIImage.loadImage("com_icon_chat_pre")?.withRenderingMode(.alwaysOriginal))
        chatTabBar.setTitleTextAttributes([NSAttributedString.Key.foregroundColor : UIColor.black], for: .selected)
        let chatNav = JCNavigationController(rootViewController: conversationVC)
        chatNav.tabBarItem = chatTabBar
        
        // 通讯录
        let contactsVC = JCContactsViewController()
        contactsVC.title = "通讯录"
        let contactsTabBar = UITabBarItem(title: "通讯录",
                                          image: UIImage.loadImage("com_icon_contacter")?.withRenderingMode(.alwaysOriginal),
                                          selectedImage: UIImage.loadImage("com_icon_contacter_pre")?.withRenderingMode(.alwaysOriginal))
        contactsTabBar.setTitleTextAttributes([NSAttributedString.Key.foregroundColor : UIColor.black], for: .selected)
        let contactsNav = JCNavigationController(rootViewController: contactsVC)
        if UserDefaults.standard.object(forKey: kUnreadInvitationCount) != nil {
            let count = UserDefaults.standard.object(forKey: kUnreadInvitationCount) as! Int
            if count != 0 {
                if count > 99 {
                    contactsTabBar.badgeValue = "99+"
                } else {
                    contactsTabBar.badgeValue = "\(count)"
                }
            }
        }
        contactsNav.tabBarItem = contactsTabBar
        
        // 我
        let mineVC = JCMineViewController()
        mineVC.title = "我"
        let mineTabBar = UITabBarItem(title: "我",
                                      image: UIImage.loadImage("com_icon_mine")?.withRenderingMode(.alwaysOriginal),
                                      selectedImage: UIImage.loadImage("com_icon_mine_pre")?.withRenderingMode(.alwaysOriginal))
        mineTabBar.setTitleTextAttributes([NSAttributedString.Key.foregroundColor : UIColor.black], for: .selected)
        let mineNav = JCNavigationController(rootViewController: mineVC)
        mineNav.tabBarItem = mineTabBar
        
        self.viewControllers = [chatNav, contactsNav, mineNav];
    }
}
