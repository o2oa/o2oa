//
//  O2RunUserInfo.swift
//  O2OA
//
//  Created by FancyLou on 2019/9/11.
//  Copyright © 2019 O2OA. All rights reserved.
//


//跑步用户信息
class O2RunUserInfo {
    var userDN :String?
    var weight: Double? // 体重 计算卡路里
    var totalDistance: Double? //总的跑步公里数
    var totalRunTimes: Int? //跑步次数
    var totalUseTime: Int? //跑步总时长
    var totalCalories: Int? //总共消耗的卡路里
    
    var updateTime: Date? //本地更新时间
}
