//
//  OONewAttanceController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/14.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import Promises

public class OONewAttanceController: UITabBarController, UITabBarControllerDelegate {

    private var viewModel: OOAttandanceViewModel = {
        return OOAttandanceViewModel()
    }()

    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
//        self.tabBarItemsAttributes = OONewAttanceController.items
        self.viewControllers = OONewAttanceController.myViewControllers
    }

    override public func viewDidLoad() {
        super.viewDidLoad()
    }

//    private func commonInit(_ result:Bool){
//        if result == true {
////            self.tabBarItemsAttributes = OONewAttanceController.items
//            self.viewControllers = OONewAttanceController.myViewControllers
//        }else{
////            self.tabBarItemsAttributes = OONewAttanceController.items1
//            self.viewControllers = OONewAttanceController.myViewControllers1
//        }
//    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


    private static let myViewControllers: [UIViewController] = {
        //打卡
        if let value = StandDefaultUtil.share.userDefaultGetValue(key: O2.O2_Attendance_version_key) as? Bool, value == true {
            let vc1 = OOAttendanceCheckInNewController(nibName: "OOAttendanceCheckInNewController", bundle: nil)
            vc1.tabBarItem = UITabBarItem(title: "打卡", image: UIImage(named: "icon_daka_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_daka")!)
            let vc2 = OOAttanceTotalController(nibName: "OOAttanceTotalController", bundle: nil)
            //        let nav2 = ZLNavigationController(rootViewController: vc2)
            vc2.tabBarItem = UITabBarItem(title: "统计", image: UIImage(named: "icon_tongji_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_tongji")!)
            //设置
            let vc3 = OOAttanceSettingController(nibName: "OOAttanceSettingController", bundle: nil)
            //        let nav3 = ZLNavigationController(rootViewController: vc3)
            vc3.tabBarItem = UITabBarItem(title: "设置", image: UIImage(named: "icon_setup_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_setting")!)
            return [vc1, vc2, vc3]
        } else {
            let vc1 = OOAttanceCheckInController(nibName: "OOAttanceCheckInController", bundle: nil)
//        let nav1 = ZLNavigationController(rootViewController: vc1)
            vc1.tabBarItem = UITabBarItem(title: "打卡", image: UIImage(named: "icon_daka_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_daka")!)
            //统计
            let vc2 = OOAttanceTotalController(nibName: "OOAttanceTotalController", bundle: nil)
//        let nav2 = ZLNavigationController(rootViewController: vc2)
            vc2.tabBarItem = UITabBarItem(title: "统计", image: UIImage(named: "icon_tongji_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_tongji")!)
            //设置
            let vc3 = OOAttanceSettingController(nibName: "OOAttanceSettingController", bundle: nil)
//        let nav3 = ZLNavigationController(rootViewController: vc3)
            vc3.tabBarItem = UITabBarItem(title: "设置", image: UIImage(named: "icon_setup_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_setting")!)
            return [vc1, vc2, vc3]
        }
    }()


//    private static let myViewControllers1: [UIViewController] = {
//        //打卡
//        let vc1 = OOAttanceCheckInController(nibName: "OOAttanceCheckInController", bundle: nil)
//        let nav1 = ZLNavigationController(rootViewController: vc1)
//        nav1.tabBarItem = UITabBarItem(title: "打卡", image: UIImage(named: "icon_daka_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_daka")!)
//        //统计
//        let vc2 = OOAttanceTotalController(nibName: "OOAttanceTotalController", bundle: nil)
//        let nav2 = ZLNavigationController(rootViewController: vc2)
//        nav2.tabBarItem = UITabBarItem(title: "统计", image: UIImage(named: "icon_tongji_nor"), selectedImage: O2ThemeManager.image(for: "Icon.at_tongji")!)
//        return [nav1,nav2]
//    }()
//


    public func tabBarController(_ tabBarController: UITabBarController, didSelect viewController: UIViewController) {
    }
}
