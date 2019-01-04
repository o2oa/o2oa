//
//  ZLUISearchBar.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/3/23.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class ZLUISearchBar: UISearchBar {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        commonInit()
    }
    
    private func commonInit(){
        self.setBackgroundImage(UIImage(), for: .any, barMetrics: .default)
        self.barTintColor = navbar_tint_color
        
        if let searchField = self.value(forKey: "searchField") as? UITextField {
            searchField.backgroundColor = UIColor.white
            searchField.layer.cornerRadius = 14
            searchField.layer.borderColor = UIColor.gray.cgColor
            searchField.layer.borderWidth  = 1
            searchField.layer.masksToBounds = true
            
        }
        
        self.setCancelButtonTitle("取消")
        
    }
    
    private func setCancelButtonTitle(_ title:String){
            UIBarButtonItem.appearance(whenContainedInInstancesOf: [UISearchBar.self]).title = title
    }
    
    
//    //3. 设置按钮文字和颜色
//    [self.customSearchBar fm_setCancelButtonTitle:@"取消"];
//    self.customSearchBar.tintColor = [UIColor colorWithRed:86/255.0 green:179/255.0 blue:11/255.0 alpha:1];
//    //修正光标颜色
//    [searchField setTintColor:[UIColor blackColor]];
//    
//    //其中fm_setCancelButtonTitle是我写的UISearchBar一个分类的方法
//    - (void)fm_setCancelButtonTitle:(NSString *)title {
//    if (IS_IOS9) {
//    [[UIBarButtonItem appearanceWhenContainedInInstancesOfClasses:@[[UISearchBar class]]] setTitle:title];
//    }else {
//    [[UIBarButtonItem appearanceWhenContainedIn:[UISearchBar class], nil] setTitle:title];
//    }
//    }


}
