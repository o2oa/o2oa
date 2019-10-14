//
//  O2MainController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/1/25.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import CocoaLumberjack
import O2OA_Auth_SDK

class O2MainController: UITabBarController, UITabBarControllerDelegate {
    
    static var tabBarVC:O2MainController!
    
    static func genernateVC() -> O2MainController {
//        guard let vc = tabBarVC else {
//            tabBarVC = O2MainController()
//            return tabBarVC
//        }
//        return vc
        return O2MainController()
    }
    
    private var currentIndex:Int = 0
    // demo服务器弹出公告
    private var demoAlertView = O2DemoAlertView()
    private let viewModel:OOLoginViewModel = {
        return OOLoginViewModel()
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.delegate = self
        _initControllers()
        selectedIndex = 2
        currentIndex = 2
        _loginIM()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        // 判断是否 第一次安装 是否是连接的demo服务器
        if let unit = O2AuthSDK.shared.bindUnit() {
            if "demo.o2oa.net" == unit.centerHost || "demo.o2oa.io" == unit.centerHost || "demo.o2server.io" == unit.centerHost {
                let tag = AppConfigSettings.shared.demoAlertTag
                if !tag {
                    demoAlertView.showFallDown()
                    AppConfigSettings.shared.demoAlertTag = true
                }
            }
        }
    }
    
    //MARK: -- delegate
    func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {
        if currentIndex == 2 && tabBarController.selectedIndex == 2 {
            if tabBarController.selectedViewController is ZLNavigationController {
                (tabBarController.selectedViewController as! ZLNavigationController).viewControllers.forEach { (vc) in
                    if vc is MailViewController {
                        DDLogDebug("点击了首页 portal")
                        (vc as! MailViewController).loadDetailSubject()
                    }
                    if vc is MainTaskSecondViewController {
                        DDLogDebug("点击了首页index")
                    }
                }
            }
        }
        self.currentIndex = tabBarController.selectedIndex
    }
    
    private func _initControllers() {
        //消息
        let conversationVC = JCConversationListViewController()
        conversationVC.title  = "消息"
        let messages = ZLNavigationController(rootViewController: conversationVC)
        
        messages.tabBarItem = UITabBarItem(title: "消息", image:UIImage(named: "icon_news_nor"), selectedImage: O2ThemeManager.image(for: "Icon.icon_news_pre"))
        
        //通讯录
        let addressVC = OOTabBarHelper.getVC(storyboardName: "contacts", vcName: nil)
        let address = ZLNavigationController(rootViewController: addressVC)
        address.tabBarItem = UITabBarItem(title: "通讯录", image:UIImage(named: "icon_address_g"), selectedImage: O2ThemeManager.image(for: "Icon.icon_address_list_pro"))
        
        // main
        let mainVC = mainController()
        mainVC.tabBarItem = UITabBarItem(title: nil, image: UIImage(named: "icon_zhuye_nor"), selectedImage: O2ThemeManager.image(for: "Icon.icon_zhuye_pre"))
        mainVC.tabBarItem.imageInsets = UIEdgeInsets(top: 6, left: 0, bottom: -6, right: 0)
        let blurImage = OOCustomImageManager.default.loadImage(.index_bottom_menu_logo_blur)
        let newBlurImage = blurImage?.withRenderingMode(.alwaysOriginal)
        mainVC.tabBarItem.image = newBlurImage
        let focusImage = OOCustomImageManager.default.loadImage(.index_bottom_menu_logo_focus)
        let newFocusImage = focusImage?.withRenderingMode(.alwaysOriginal)
        mainVC.tabBarItem.selectedImage = newFocusImage

        //应用
        let appsVC = OOTabBarHelper.getVC(storyboardName: "apps", vcName: nil)
        let apps = ZLNavigationController(rootViewController: appsVC)
        apps.tabBarItem = UITabBarItem(title: "应用", image:UIImage(named: "icon_yingyong"), selectedImage: O2ThemeManager.image(for: "Icon.icon_yingyong_pro"))
        
        //设置
        let settingsVC = OOTabBarHelper.getVC(storyboardName: "setting", vcName: nil)
        let settings =   ZLNavigationController(rootViewController: settingsVC)
        settings.tabBarItem = UITabBarItem(title: "设置", image:UIImage(named: "setting_normal"), selectedImage: O2ThemeManager.image(for: "Icon.setting_selected"))
        
        self.viewControllers = [messages, address, mainVC, apps, settings]
        
    }
    
    private func mainController() -> UIViewController {
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
    
    private func _loginIM() {
        viewModel.registerIM().then { (result) in
            self.viewModel.loginIM().then({ (result) in
                Log.debug(message: "IM登陆完成")
            })
            }.catch { (imError) in
                let error = imError as! OOLoginError
                switch error {
                case .imRegisterFail(let myErr):
                    Log.debug(message: myErr.errorDescription!)
                    self.viewModel.loginIM().then({ (result) in
                        Log.debug(message: "IM登陆完成")
                    }).catch({ (loginError) in
                        Log.error(message: "im Login Error \(loginError)")
                    })
                    break
                default:
                    break
                }
        }
    }
    
}
