//
//  ICModels.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/26.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

typealias ICTimeComponent = (year:String,month:String,day:String,hour:String,minute:String,second:String)

enum AttendanceStatusType:String {
    case LEAVE = "请假"
    case LATE = "迟到"
    case EARLY = "早退"
    case ABSENCE = "缺勤"
    case ABNORMAL = "异常打卡"
    case UNDERTIME = "工时不足"
    case NORMAL = "正常"
}

enum AttendanceWorkType:String {
    case HOLIDAY = "节假日"
    case WEEKEND = "周末"
    case WORKDAY = "调休工作日"
    case NORMALWORKDAY = "工作日"
}

///返回时间每个单元的值
func calcTimeComponetFromDate(date:Date) -> ICTimeComponent {
    let calendar = NSCalendar(calendarIdentifier: NSCalendar.Identifier.gregorian)
    let comps = calendar?.components([NSCalendar.Unit.year,NSCalendar.Unit.month,NSCalendar.Unit.day,NSCalendar.Unit.hour,NSCalendar.Unit.minute,NSCalendar.Unit.second], from: date)
    let year:Int = (comps?.year!)!
    let month:Int = (comps?.month!)!
    let monthStr = addPrefixByTimeUnit(timeUnit: month, prefix: "0")
    let day:Int = (comps?.day!)!
    let dayStr = addPrefixByTimeUnit(timeUnit: day, prefix: "0")
    let hour:Int = (comps?.hour!)!
    let hourStr = addPrefixByTimeUnit(timeUnit: hour, prefix: "0")
    let minute:Int = (comps?.minute!)!
    let minuteStr = addPrefixByTimeUnit(timeUnit: minute, prefix: "0")
    let second:Int = (comps?.second!)!
    let secondStr = addPrefixByTimeUnit(timeUnit: second, prefix: "0")
    return ("\(year)","\(monthStr)","\(dayStr)","\(hourStr)","\(minuteStr)","\(secondStr)")
    
}

private func addPrefixByTimeUnit(timeUnit:Int,prefix:String) -> String{
    if timeUnit < 10 {
        return "\(prefix)\(timeUnit)"
    }else {
        return "\(timeUnit)"
    }
}

///每个考勤数据类型
func calcAttendanceStatus(attendance:AttendanceDetailData) -> (statusType:AttendanceStatusType,workType:AttendanceWorkType,isAppeal:Bool) {
    //考勤类型
    var statusType:AttendanceStatusType = .NORMAL
    if attendance.isGetSelfHolidays == true {
        statusType = .LEAVE
    }else if attendance.isLate == true {
        statusType = .LATE
    }else if attendance.isLeaveEarlier == true {
        statusType = .EARLY
    }else if  attendance.isAbsent ==  true {
        statusType = .ABSENCE
    }else if attendance.isAbnormalDuty == true {
        statusType = .ABNORMAL
    }else if attendance.isLackOfTime == true {
        statusType = .UNDERTIME
    }
    //工作时间类型
    var workType:AttendanceWorkType = .NORMALWORKDAY
    if attendance.isHoliday == true {
        workType = .HOLIDAY
    }else if attendance.isWeekend == true {
        workType = .WEEKEND
    }else if attendance.isWorkday == true {
        workType = .WORKDAY
    }
    //是否申诉
    var isAppeal = false
    if attendance.isAbsent == true || attendance.isLate == true || attendance.isLeaveEarlier == true || attendance.isAbnormalDuty == true || attendance.isLackOfTime == true {
        isAppeal = true
    }
    
    return (statusType,workType,isAppeal)
}


///首页统计实体模型
class AttendanceTotalEntry {
    //数量
    var count:Int = 0
    //标签名
    var label:String?
    //类型
    var type:AttendanceStatusType = .NORMAL
    
    init(label:String,type:AttendanceStatusType) {
        self.label = label
        self.type = type
    }
    
    func incCount() {
        count+=1
    }
    
}

///待审核显示数据模型
class AttendanceCheckEntry {
    var identityName:String?
    var appealDate:String?
    var appealReson:String?
    var appealDesc:String?
    var appealObj:AttendanceAppealInfoData?
    
    static public func genernateEntry(infoData:AttendanceAppealInfoData) -> AttendanceCheckEntry {
        let entry = AttendanceCheckEntry()
        entry.identityName = "\(infoData.empName!)(\(infoData.departmentName!))"
        entry.appealDate = infoData.appealDateString
        let reson = calcAppealReson(infoData)
        entry.appealReson = reson.appealReson
        entry.appealDesc = reson.appealDesc
        entry.appealObj = infoData
        return entry
    }
    
    static private func calcAppealReson(_ infoData:AttendanceAppealInfoData) -> (appealReson:String,appealDesc:String) {
        var reson = infoData.appealReason!
        if let holidayType = infoData.selfHolidayType {
            if holidayType.isEmpty == false {
                reson.append("(\(holidayType))")
            }
        }
        var desc = infoData.appealDescription!
        var addr = infoData.address!
        if addr.isEmpty == false {
            addr = "地点:\(addr)"
        }
        if desc.isEmpty == false {
            desc = "事由:\(desc)"
        }
        desc = addr.isEmpty == false ? "\(addr),\(desc)" : desc
        return (reson,desc)
    }
}



///详细页显示数据模型
class AttendanceDetailEntry {
    var aDate:String?
    var aTimeInterval:String?
    var aWorkType:String?
    var aStatusType:String?
    var appealStatus:Int?
    var isAppeal:Bool = false
    var detailObj:AttendanceDetailData?
    
    init() {
        
    }
    
    static public func generateDetailEntry(detailData:AttendanceDetailData) -> AttendanceDetailEntry {
        let entry = AttendanceDetailEntry()
        entry.aDate = detailData.recordDateString //申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
        entry.appealStatus = detailData.appealStatus
        entry.aTimeInterval = "\(detailData.onDutyTime!) - \(detailData.offDutyTime!)"
        let t = calcAttendanceStatus(attendance: detailData)
        entry.aWorkType = t.workType.rawValue
        entry.aStatusType = t.statusType.rawValue
        entry.isAppeal = t.isAppeal
        entry.detailObj = detailData
        return entry
    }
    
}

class AttendanceAppealInfoEntry:Mappable {
    var appealStatus:Int = 1 //申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
    var appealReason:String?//原因  临时请假  出差 因公外出 其他
    var appealDescription:String?//事由
    var selfHolidayType:String?//如果原因是临时请假 这里需要选择一个请假类型 ：带薪年休假 带薪病假 带薪福利假 扣薪事假 其他
    var address:String?//外出地址
    var startTime:String?// yyyy-MM-dd HH:mm
    var endTime:String?// yyyy-MM-dd HH:mm
    var processPerson1:String?// 审批人一
    
    init() {
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        appealStatus<-map["appealStatus"]
        appealReason<-map["appealReason"]
        appealDescription<-map["appealDescription"]
        selfHolidayType<-map["selfHolidayType"]
        address<-map["address"]
        startTime<-map["startTime"]
        endTime<-map["endTime"]
        processPerson1<-map["processPerson1"]
    }
}

