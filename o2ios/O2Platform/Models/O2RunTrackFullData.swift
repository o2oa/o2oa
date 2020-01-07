//
//  O2RunTrackFullData.swift
//  O2Platform
//
//  Created by FancyLou on 2019/10/30.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Foundation




class O2RunTrackFullData {
    var id: String? //数据库id UUID防止冲突
    var userDN :String?
    var createTime: Date?//数据库插入时间
    
    var runDate: Date? // 跑步日期时间
    var startTime: Date? //开始时间
    var endTime: Date? //结束时间
    var useTime: Int? //总用时
    var distance: Double? //总距离
    var calories: Int? //总消耗卡路里
    var stepNumber: Int? //总步数
    var speed: Double?//配速
    var entityName: String? //百度鹰眼设备名
    
    var points:[O2RunTrackPointInfo]?
    
}
