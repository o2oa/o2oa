//
//  Head.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/12.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import Foundation
class HeadTitle {
    var name:String?
    var icon:String?
    var isBar = false
    var barText: [OrgUnit]?
    
    init(name : String, icon : String) {
        self.name = name
        self.icon = icon
    }
    
    init(name : String, barText: [OrgUnit], icon : String = "", isBar: Bool = true) {
        self.name = name
        self.barText = barText
        self.icon = icon
        self.isBar = isBar
        
    }
}
