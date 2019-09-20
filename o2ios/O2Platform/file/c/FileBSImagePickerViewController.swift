//
//  FileBSImagePickerViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/22.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import BSImagePicker

class FileBSImagePickerViewController: BSImagePickerViewController {
 
    var defaultmaxNumberOfSelections = 1
    var defaultTakePhotos = true
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationBar.isTranslucent = false
        self.navigationBar.barTintColor = navbar_barTint_color
        self.navigationBar.tintColor = navbar_tint_color
        self.navigationBar.titleTextAttributes = [NSAttributedString.Key.font:navbar_text_font,NSAttributedString.Key.foregroundColor:navbar_tint_color]
        self.albumButton.setTitleColor(navbar_tint_color, for: .normal)
        self.settings.maxNumberOfSelections = defaultmaxNumberOfSelections
        self.settings.takePhotos = defaultTakePhotos
        
        
        //隐藏返回按钮文字
        let barItem = UIBarButtonItem.appearance()
        let offset = UIOffset(horizontal: -200, vertical: 0)
        barItem.setBackButtonTitlePositionAdjustment(offset, for: .default)
        barItem.setTitleTextAttributes([NSAttributedString.Key.font:navbar_item_font,NSAttributedString.Key.foregroundColor:navbar_tint_color], for:UIControl.State())

        // Do any additional setup after loading the view.
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }
    
    

}
