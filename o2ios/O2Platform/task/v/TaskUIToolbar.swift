//
//  TaskUIToolbar.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/12.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class TaskUIToolbar: UIToolbar {

    override func awakeFromNib() {
        self.backgroundColor = UIColor.white
        self.barTintColor = UIColor.white
        self.alpha = 1.0
        self.addShadow(offset: CGSize.init(width: 4.0, height: 4.0), radius: 2, color: UIColor.lightGray, opacity: 0.8)
    }

}
