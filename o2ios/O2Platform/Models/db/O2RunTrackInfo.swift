//
//  O2RunTrackInfo.swift
//  O2OA
//
//  Created by FancyLou on 2019/9/11.
//  Copyright © 2019 O2OA. All rights reserved.
//

//一次跑步记录
class O2RunTrackInfo {
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
    //每公里的耗时列表
    //地图轨迹 暂定用百度鹰眼 轨迹用开始时间和结束时间查询
    var entityName: String? //百度鹰眼设备名 暂定用userDN



    func validateData() -> Bool {
        if self.userDN == nil {
            return false
        }
        if self.runDate == nil {
            return false
        }
        if self.startTime == nil {
            return false
        }
        if self.endTime == nil {
            return false
        }
        if self.entityName == nil {
            return false
        }
        if self.useTime == nil {
            return false
        }
        if self.distance == nil {
            return false
        }
        if self.calories == nil {
            return false
        }
        if self.stepNumber == nil {
            return false
        }
        if self.speed == nil {
            return false
        }

        return true
    }
}
