//
//  TaskBarButtonItem.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/12.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class TaskBarButtonItem: UIBarButtonItem {
    
    override func awakeFromNib() {
        self.tintColor = base_color
        
        self.setTitleTextAttributes( [NSAttributedString.Key.font: UIFont(name: "PingFangSC-Regular", size: 17.0)!,NSAttributedString.Key.foregroundColor: base_color], for: .normal)
        self.setTitleTextAttributes([NSAttributedString.Key.font: UIFont(name: "PingFangSC-Regular", size: 17.0)!,NSAttributedString.Key.foregroundColor: base_color], for: .selected)
        self.setTitleTextAttributes( [NSAttributedString.Key.font: UIFont(name: "PingFangSC-Regular", size: 17.0)!,NSAttributedString.Key.foregroundColor: base_color], for: .disabled)
        
        
    }

}
