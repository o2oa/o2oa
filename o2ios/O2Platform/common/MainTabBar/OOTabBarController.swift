//
//  OOTabBarController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/12.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CYLTabBarController
import CocoaLumberjack

class OOTabBarController: CYLTabBarController,UITabBarControllerDelegate {
    
    static var tabBarVC:OOTabBarController!
    
    private var currentIndex:Int = 0
    
    private let viewModel:OOLoginViewModel = {
        return OOLoginViewModel()
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.delegate = self
        selectedIndex = 2
        currentIndex = 2
        _init()
    }
    
    private func _init() {
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
    
    static func genernateVC(viewControllers:[UIViewController],tabBarItemsAttributes:[[String : String]]) -> OOTabBarController {
        guard let myVC = tabBarVC else {
            tabBarVC = OOTabBarController(viewControllers: viewControllers, tabBarItemsAttributes: tabBarItemsAttributes)
            return tabBarVC
        }
        return myVC
    }
    
    static func genernateVC() -> OOTabBarController  {
        guard let myVC = tabBarVC else {
            tabBarVC = OOTabBarController(viewControllers: OOTabBarHelper.viewControllers(), tabBarItemsAttributes: OOTabBarHelper.tabBarItemsAttributesForController())
            return tabBarVC
        }
        
        return myVC
    }
    
    func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
        self.cyl_tabBarController.updateSelectionStatusIfNeeded(for: tabBarController, shouldSelect: viewController)
        return true
    }
    

    
    
    override func tabBarController(_ tabBarController: UITabBarController!, didSelect control: UIControl!) {
        
        if control is CYLPlusButton {
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
        }
        self.currentIndex = tabBarController.selectedIndex
    }

}


