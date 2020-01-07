//
//  JPushDevice.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/11.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Foundation

// x_jpush_assemble_control 设备绑定对象

class JPushDevice: NSObject,DataModel {
    
    @objc var deviceName : String?
    @objc var deviceType : String? = "ios"
    override required init() {
        
    }
    
    
}

