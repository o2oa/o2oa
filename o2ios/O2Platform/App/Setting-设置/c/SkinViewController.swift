//
//  SkinViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/6/13.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit

class SkinViewController: UIViewController {

    @IBOutlet weak var redButton: UIButton!
    @IBOutlet weak var blueButton: UIButton!
    @IBAction func redButtonAction(_ sender: UIButton) {
        print("click red theme................")
         self.setThemeAndRefreshUI(theme: "red")
    }
    @IBAction func blueButtonAction(_ sender: UIButton) {
        print("click blue theme................")
        self.setThemeAndRefreshUI(theme: "blue")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "个性换肤"
        self.redButton.cornerRadius = 8.0
        self.blueButton.cornerRadius = 8.0
        self.redButton.setBackgroundColor(UIColor.lightGray, forState: .disabled)
        self.blueButton.setBackgroundColor(UIColor.lightGray, forState: .disabled)
        if let themeName = O2ThemeManager.currentTheme?["name"] as? String {
            print("themeName:\(themeName)")
            if themeName == "red" {
                self.redButton.isEnabled = false
                self.blueButton.isEnabled = true
            }else if themeName == "blue" {
                self.redButton.isEnabled = true
                self.blueButton.isEnabled = false
            }
        }
        
    }
    
    private func setThemeAndRefreshUI(theme: String) {
        AppConfigSettings.shared.themeName = theme
        O2ThemeManager.setTheme(plistName: theme, path: .mainBundle)
        //搜索框
        UISearchBar.appearance().theme_barTintColor = ThemeColorPicker(keyPath: "Base.base_color")
        UISearchBar.appearance().tintColor = UIColor.white
        UITextField.appearance(whenContainedInInstancesOf: [UISearchBar.self]).theme_tintColor = ThemeColorPicker(keyPath: "Base.base_color")
        OOTabBarHelper.initTabBarStyle()
        //跳转到主页
        let destVC = O2MainController.genernateVC()
        destVC.selectedIndex = 2 // 首页选中 TODO 图标不亮。。。。。
        UIApplication.shared.keyWindow?.rootViewController = destVC
        UIApplication.shared.keyWindow?.makeKeyAndVisible()
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
