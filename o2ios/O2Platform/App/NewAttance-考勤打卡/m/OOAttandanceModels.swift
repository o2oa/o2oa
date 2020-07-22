//
//  OOAttandanceModels.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/16.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import HandyJSON

// MARK:- 移动端获到打卡记录Bean
class OOAttandanceMobileQueryBean:NSObject,DataModel {
    
    @objc var empNo:String? //员工号，根据员工号查询记录
    
    @objc var empName:String? //员工姓名，根据员工姓名查询记录.
    
    @objc var startDate:String? //开始日期：yyyy-mm-dd.
    
    @objc var endDate:String? //结束日期：yyyy-mm-dd,如果开始日期填写，结束日期不填写就是只查询开始日期那一天
    
    @objc var signDescription:String? //打卡说明:上班打卡，下班打卡.
    
    override required init() {
        
    }
}

// MARK:- 移动端打卡数据
class OOAttandanceMobileDetail:NSObject,DataModel {

    @objc var id:String? //数据库主键,自动生成.
    @objc var createTime:String? //创建时间,自动生成.
    @objc var updateTime:String? //修改时间,自动生成.
    @objc var empNo:String? //员工号
    @objc var empName:String? //员工姓名
    @objc var recordDateString:String? //打卡记录日期字符串
    @objc var recordDate:String? //打卡记录日期
    @objc var signTime:String? //打卡时间
    @objc var signDescription:String? //打卡说明
    @objc var desc:String? //其他说明备注
    @objc var recordAddress:String?  //打卡地点描述
    @objc var longitude:String? //经度
    @objc var latitude:String?  //纬度
    @objc var optMachineType:String? // 操作设备类别：手机品牌|PAD|PC|其他
    @objc var optSystemName:String?  // 操作设备类别：Mac|Windows|IOS|Android|其他
    var recordStatus:Int?  //记录状态：0-未分析 1-已分析
    @objc var checkin_type: String? // 打卡类型 上午上班打卡 下午下班打卡
    
    required override init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }

}

// MARK: - 打卡班次对象
class OOAttandanceScheduleSetting: NSObject, DataModel {
    /**
     "id": "7c89ddfe-7e69-40ce-9908-d699081aa660",
                "topUnitName": "浙江兰德纵横网络技术股份有限公司@1@U",
                "unitName": "移动开发组@320494093@U",
                "unitOu": "移动开发组@320494093@U",
                "onDutyTime": "09:00",
                "offDutyTime": "17:00",
                "signProxy": 0,
                "lateStartTime": "9:05",
                "createTime": "2020-05-27 09:19:16",
                "updateTime": "2020-05-27 09:19:16"
     */
    @objc var id: String?
    @objc var topUnitName: String?
    @objc var unitName: String?
    @objc var unitOu: String?
    @objc var onDutyTime: String?
    @objc var offDutyTime: String?
    var signProxy: Int?
    @objc var lateStartTime: String?
    @objc var createTime: String?
    @objc var updateTime: String?
    
    required override init() {
        
    }
    
}

// MARK: - 当前用户当天打卡功能
class OOAttandanceFeature: NSObject, DataModel {
    /**
     "signSeq": 1,
     "signDate": "2020-06-02",
     "signTime": "09:00",
     "checkinType": "上午上班打卡"
     */
    @objc var signDate: String?
    @objc var signTime: String?
    @objc var checkinType: String?
    var signSeq: Int? //第几次打卡 -1就不能打卡了
    
    required override init() {
        
    }
    
}

// MARK: - 打卡班次对象 和 打卡结果拼接的结果
class OOAttandanceMobileScheduleInfo: NSObject, DataModel {
    @objc var signDate: String?
    @objc var signTime: String?
    @objc var checkinType: String?
    var signSeq: Int?
    @objc var checkinStatus: String? // 未打卡 已打卡
    @objc var checkinTime: String? //打卡时间
    @objc var recordId: String? //打卡结果的id 更新打卡用
    
    required override init() {
        
    }
    
}

// MARK: - MyRecords 登录者当天的所有移动打卡信息记录 排版情况等
class OOMyAttandanceRecords: NSObject, DataModel {
   
    @objc var records:[OOAttandanceMobileDetail]?
    @objc var scheduleSetting:OOAttandanceScheduleSetting?
    @objc var feature: OOAttandanceFeature?
    //2020-07-21 新添加的
    @objc var scheduleInfos: [OOAttandanceFeature]?
    
    required override init() {
        
    }
}

// MARK:- 提交打卡数据FormBean
class OOAttandanceMobileCheckinForm:NSObject,DataModel {
    
    @objc var id:String? //id 为空就是新增 有id就是更新
    
    @objc var empNo:String? //员工号, 可以为空.
    
    @objc var empName:String? //员工姓名, 必须填写.
    
    @objc var recordDateString:String? //打卡记录日期字符串：yyyy-mm-dd, 必须填写.
    
    @objc var signTime:String? //打卡时间: hh24:mi:ss, 必须填写.
    
    @objc var signDescription:String? //打卡说明:上班打卡，下班打卡, 可以为空.
    
    @objc var desc:String? //其他说明备注, 可以为空.
    
    @objc var recordAddress:String? //打卡地点描述, 可以为空.
    
    @objc var longitude:String? //经度, 可以为空.
    
    @objc var latitude:String? //纬度, 可以为空.
    
    @objc var optMachineType:String? //操作设备类别：手机品牌|PAD|PC|其他, 可以为空.
    
    @objc var optSystemName:String? //操作设备类别：Mac|Windows|IOS|Android|其他, 可以为空
    
    @objc var checkin_type: String? //上午上班打卡 下午下班打卡 。。。。 对应OOAttandanceFeature里面的checkinType
    
    required override init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
}

// MARK:- 打卡地点配置
class OOAttandanceWorkPlace:NSObject,DataModel{
    
    @objc var desc: String?
    @objc var id:String? // 数据库主键,自动生成.
    @objc var createTime:String?  //   创建时间,自动生成.
    @objc var updateTime:String?  // 修改时间,自动生成.
    @objc var placeName:String?   // 场所名称
    @objc var placeAlias:String?  // 场所别名
    @objc var creator:String?    // 创建人
    @objc var longitude:String?  //  经度
    @objc var latitude:String?   //  纬度
    var errorRange:Int? //   误差范围
    
    required override init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
}

// MARK:- 提交新的打卡地点
class OOAttandanceNewWorkPlace:NSObject,DataModel {
    
    @objc var placeName:String? //场所名称
    
    @objc var placeAlias:String? //场所别名
    
    @objc var creator:String? //创建人
    
    @objc var longitude:String? //经度
    
    @objc var latitude:String? //纬度
    
    @objc var errorRange:String? //误差范围
    
    @objc var desc:String? //说明备注
    
    required override init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
}

// MARK:- 考勤配置管理员
class OOAttandanceAdmin:NSObject,DataModel {
    @objc var desc: String?
    @objc var id:String? //   数据库主键,自动生成.
    @objc var createTime:String? //   创建时间,自动生成.
    @objc var updateTime:String? //    修改时间,自动生成.
    @objc var unitName:String? //    组织名称
    @objc var unitOu:String?  //    组织编号
    @objc var adminName:String? //    管理员姓名
    @objc var adminLevel:String? //    管理级别:UNIT|TOPUNIT
    required override init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
    
}

class OOAttandanceTotalBean:NSObject,DataModel {
    @objc var q_year:String? //  : 打卡年份
    @objc var q_month:String? // : 打卡月份
    @objc var cycleYear:String? // : 考勤周期年份
    @objc var cycleMonth:String? // : 考勤周期月份
    @objc var q_empName:String? //：人员全名
    
    required override init() {
        
    }
}

// MARK:- cycleDetail
class OOAttandanceCycleDetail:NSObject,DataModel {
    @objc var id:String? //: "ea55970a-bd18-4388-a40b-b0cc7d6cc576",
    @objc var createTime:String? //: "2018-04-12 12:49:37",
    @objc var updateTime:String? //: "2018-04-12 12:49:37",
    @objc var topUnitName:String? //": "*",
    @objc var unitName:String? //: "*",
    @objc var cycleYear:String? //: "2018",
    @objc var cycleMonth:String? //: "04",
    @objc var cycleStartDateString:String? //: "2018-04-01",
    @objc var cycleEndDateString:String? //: "2018-05-01",
    @objc var cycleStartDate:String? //: "2018-04-01",
    @objc var cycleEndDate:String? //: "2018-05-01",
    @objc var desc:String? //: "系统自动创建"
    
    required override init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
}

// MARK:- OOAttandanceCheckinTotal
class OOAttandanceCheckinTotal:NSObject,DataModel {
    @objc var abnormalDutyDayTime : String?
    var absence : Int?
    @objc var absentDayTime : String?
    @objc var appealDescription : String?
    @objc var appealProcessor : String?
    @objc var appealReason : String?
    var appealStatus : Int?
    var attendance : Int?
    @objc var batchName : String?
    @objc var createTime : String?
    @objc var cycleMonth : String?
    @objc var cycleYear : String?
    @objc var descriptionField : String?
    @objc var empName : String?
    @objc var empNo : String?
    var getSelfHolidayDays : Int?
    @objc var id : String?
    var isAbnormalDuty : Bool?
    var isAbsent : Bool?
    var isGetSelfHolidays : Bool?
    var isHoliday : Bool?
    var isLackOfTime : Bool?
    var isLate : Bool?
    var isLeaveEarlier : Bool?
    var isWeekend : Bool?
    var isWorkOvertime : Bool?
    var isWorkday : Bool?
    var lateTimeDuration : Int?
    var leaveEarlierTimeDuration : Int?
    @objc var monthString : String?
    @objc var offDutyTime : String?
    @objc var offWorkTime : String?
    @objc var onDutyTime : String?
    @objc var onWorkTime : String?
    @objc var recordDate : String?
    @objc var recordDateString : String?
    var recordStatus : Int?
    @objc var selfHolidayDayTime : String?
    @objc var topUnitName : String?
    @objc var unitName : String?
    @objc var updateTime : String?
    var workOvertimeTimeDuration : Int?
    var workTimeDuration : Int?
    @objc var yearString : String?
    
    
    required override init() {
        
    }
}

// MARK:- 考勤统计分析model
class OOAttandanceAnalyze:NSObject,DataModel {
    var abNormalDutyCount : Int?
    var absenceDayCount : Int?
    @objc var createTime : String?
    @objc var employeeName : String?
    @objc var id : String?
    var lackOfTimeCount : Int?
    var lateTimes : Int?
    var leaveEarlyTimes : Int?
    var offDutyTimes : Int?
    var onDutyDayCount : Int?
    var onDutyTimes : Int?
    var onSelfHolidayCount : Int?
    @objc var statisticMonth : String?
    @objc var statisticYear : String?
    @objc var topUnitName : String?
    @objc var unitName : String?
    @objc var updateTime : String?
    var workDayCount : Int?
    
    required override init() {
        
    }
}




