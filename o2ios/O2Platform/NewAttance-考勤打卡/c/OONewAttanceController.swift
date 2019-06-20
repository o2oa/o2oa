//
//  OONewAttanceController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/14.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import CYLTabBarController
import Promises

public class OONewAttanceController: CYLTabBarController {
    
    private var viewModel:OOAttandanceViewModel = {
       return OOAttandanceViewModel()
    }()
    
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.tabBarItemsAttributes = OONewAttanceController.items
        self.viewControllers = OONewAttanceController.myViewControllers
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
//        let myAcc = AppConfigSettings.shared.account?.distinguishedName
//        viewModel.getAttendanceAdmin().then { (admins) -> Promise<Bool> in
//            return Promise { fulfill,reject in
//                let adminsStrings = admins.map({ (admin) -> String in
//                    return admin.adminName ?? ""
//                })
//                if adminsStrings.contains(myAcc ?? "") {
//                    fulfill(true)
//                }else{
//                    fulfill(false)
//                }
//            }
//            }.then { (result) in
//                self.commonInit(result)
//            }.catch { (myerror) in
//                self.commonInit(false)
//        }
        // Do any additional setup after loading the view.
    }
    
    private func commonInit(_ result:Bool){
        if result == true {
            self.tabBarItemsAttributes = OONewAttanceController.items
            self.viewControllers = OONewAttanceController.myViewControllers
        }else{
            self.tabBarItemsAttributes = OONewAttanceController.items1
            self.viewControllers = OONewAttanceController.myViewControllers1
        }
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
   private static let myViewControllers: [UIViewController] = {
        //打卡
        let vc1 = OOAttanceCheckInController(nibName: "OOAttanceCheckInController", bundle: nil)
        let nav1 = OONewAttanceNavController(rootViewController: vc1)
        //统计
        let vc2 = OOAttanceTotalController(nibName: "OOAttanceTotalController", bundle: nil)
        let nav2 = OONewAttanceNavController(rootViewController: vc2)
        //设置
        let vc3 = OOAttanceSettingController(nibName: "OOAttanceSettingController", bundle: nil)
        let nav3 = OONewAttanceNavController(rootViewController: vc3)
        return [nav1,nav2,nav3]
    }()
    
    private static let items: [[String : String]] = {
        let dakavalue = O2ThemeManager.string(for: "Icon.icon_daka_pro")!
        let tabBarItemOne = [CYLTabBarItemTitle:"打卡",
                             CYLTabBarItemImage:"icon_daka_nor",
                             CYLTabBarItemSelectedImage: dakavalue]
        
        let tongjivalue = O2ThemeManager.string(for: "Icon.icon_tongji_pro")!
        let tabBarItemTwo = [CYLTabBarItemTitle:"统计",
                             CYLTabBarItemImage:"icon_tongji_nor",
                             CYLTabBarItemSelectedImage: tongjivalue]
        
        let setupvalue = O2ThemeManager.string(for: "Icon.icon_setup_pre")!
        let tabBarItemThree = [CYLTabBarItemTitle:"设置",
                             CYLTabBarItemImage:"icon_setup_nor",
                             CYLTabBarItemSelectedImage: setupvalue]
        

        return [tabBarItemOne,tabBarItemTwo,tabBarItemThree]
    }()
    
    private static let myViewControllers1: [UIViewController] = {
        //打卡
        let vc1 = OOAttanceCheckInController(nibName: "OOAttanceCheckInController", bundle: nil)
        let nav1 = OONewAttanceNavController(rootViewController: vc1)
        //统计
        let vc2 = OOAttanceTotalController(nibName: "OOAttanceTotalController", bundle: nil)
        let nav2 = OONewAttanceNavController(rootViewController: vc2)
        //设置
        let vc3 = OOAttanceSettingController(nibName: "OOAttanceSettingController", bundle: nil)
        let nav3 = OONewAttanceNavController(rootViewController: vc3)
        return [nav1,nav2]
    }()
    
    private static let items1: [[String : String]] = {
        let dakavalue = O2ThemeManager.string(for: "Icon.icon_daka_pro")!
        let tabBarItemOne = [CYLTabBarItemTitle:"打卡",
                             CYLTabBarItemImage:"icon_daka_nor",
                             CYLTabBarItemSelectedImage: dakavalue]
        
        let tongjivalue = O2ThemeManager.string(for: "Icon.icon_tongji_pro")!
        let tabBarItemTwo = [CYLTabBarItemTitle:"统计",
                             CYLTabBarItemImage:"icon_tongji_nor",
                             CYLTabBarItemSelectedImage: tongjivalue]
        
        let setupvalue = O2ThemeManager.string(for: "Icon.icon_setup_pre")!
        let tabBarItemThree = [CYLTabBarItemTitle:"设置",
                               CYLTabBarItemImage:"icon_setup_nor",
                               CYLTabBarItemSelectedImage: setupvalue]
        
        
        return [tabBarItemOne,tabBarItemTwo]
    }()

    public override func tabBarController(_ tabBarController: UITabBarController!, didSelect control: UIControl!) {
        print(control)
    }

}
