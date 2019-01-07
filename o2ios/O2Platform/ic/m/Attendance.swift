//
//  Attendance.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/24.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class AttendanceDetailData:Mappable{
    var id:String?
    var createTime:String?
    var updateTime:String?
    var sequence:String?
    var empNo:String?
    var empName:String?
    var companyName:String?
    var departmentName:String?
    var yearString:String?
    var monthString:String?
    var recordDateString:String?
    var recordDate:String?
    var cycleYear:String?
    var cycleMonth:String?
    var isHoliday:Bool?
    var isWorkday:Bool?
    var isGetSelfHolidays:Bool?
    var selfHolidayDayTime:String?
    var absentDayTime:String?
    var abnormalDutyDayTime:String?
    var getSelfHolidayDays:Double?
    var isWeekend:Bool?
    var onWorkTime:String?
    var offWorkTime:String?
    var onDutyTime:String?
    var offDutyTime:String?
    var isLate:Bool?
    var lateTimeDuration:CLong?
    var isLeaveEarlier:Bool?
    var leaveEarlierTimeDuration:CLong?
    var isAbsent:Bool?
    var isAbnormalDuty:Bool?
    var isLackOfTime:Bool?
    var isWorkOvertime:Bool?
    var workOvertimeTimeDuration:CLong?
    var workTimeDuration:CLong?
    var attendance:Double?
    var absence:Double?
    var recordStatus:Int?
    var batchName:String?
    var desc:String?
//    //申诉相关信息
    var appealStatus:Int?//申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
    var appealReason:String?//原因  临时请假  出差 因公外出 其他
    var appealDescription:String?//事由
    var selfHolidayType:String?//如果原因是临时请假 这里需要选择一个请假类型 ：带薪年休假 带薪病假 带薪福利假 扣薪事假 其他
    var address:String?//外出地址
    var startTime:String?// yyyy-MM-dd HH:mm
    var endTime:String?// yyyy-MM-dd HH:mm
    var processPerson1:String?// 审批人一
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        sequence<-map["sequence"]
        empNo<-map["empNo"]
        empName<-map["empName"]
        companyName<-map["companyName"]
        departmentName<-map["idepartmentNamed"]
        yearString<-map["yearString"]
        monthString<-map["imonthStringd"]
        recordDateString<-map["recordDateString"]
        recordDate<-map["recordDate"]
        cycleYear<-map["cycleYear"]
        cycleMonth<-map["cycleMonth"]
        isHoliday<-map["isHoliday"]
        isWorkday<-map["isWorkday"]
        isGetSelfHolidays<-map["isGetSelfHolidays"]
        selfHolidayDayTime<-map["selfHolidayDayTime"]
        absentDayTime<-map["absentDayTime"]
        abnormalDutyDayTime<-map["abnormalDutyDayTime"]
        getSelfHolidayDays<-map["getSelfHolidayDays"]
        isWeekend<-map["iisWeekendd"]
        onWorkTime<-map["onWorkTime"]
        offWorkTime<-map["offWorkTime"]
        onDutyTime<-map["onDutyTime"]
        offDutyTime<-map["offDutyTime"]
        isLate<-map["isLate"]
        lateTimeDuration<-map["lateTimeDuration"]
        isLeaveEarlier<-map["isLeaveEarlier"]
        leaveEarlierTimeDuration<-map["leaveEarlierTimeDuration"]
        isAbsent<-map["isAbsent"]
        isAbnormalDuty<-map["isAbnormalDuty"]
        isLackOfTime<-map["isLackOfTime"]
        isWorkOvertime<-map["isWorkOvertime"]
        workOvertimeTimeDuration<-map["workOvertimeTimeDuration"]
        workTimeDuration<-map["workTimeDuration"]
        attendance<-map["attendance"]
        absence<-map["absence"]
        recordStatus<-map["recordStatus"]
        batchName<-map["batchName"]
        desc<-map["desc"]
        //    //申诉相关信息
        appealStatus<-map["appealStatus"]//申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
        appealReason<-map["appealReason"]//原因  临时请假  出差 因公外出 其他
        appealDescription<-map["appealDescription"]//事由
        selfHolidayType<-map["selfHolidayType"]//如果原因是临时请假 这里需要选择一个请假类型 ：带薪年休假 带薪病假 带薪福利假 扣薪事假 其他
        address<-map["address"]//外出地址
        startTime<-map["startTime"]// yyyy-MM-dd HH:mm
        endTime<-map["endTime"]// yyyy-MM-dd HH:mm
        processPerson1<-map["processPerson1"]// 审批人一
    }

}

class AttendanceSettingData:Mappable{
    var id:String?
    var createTime:String?
    var updateTime:String?
    var sequence:String?
    var configCode:String?
    var configName:String?
    var configValue:String?
    var ordernumber:Int?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        sequence<-map["sequence"]
        configCode<-map["configCode"]
        configName<-map["configName"]
        configValue<-map["configValue"]
        ordernumber<-map["ordernamber"]
    }
}


class AttendanceAppealInfoData:Mappable {
    var id:String?
    var createTime:String?
    var updateTime:String?
    var sequence:String?
    var detailId:String?
    var empName:String?
    var companyName:String?
    var departmentName:String?
    var yearString:String?
    var monthString:String?
    var appealDateString:String?
    var recordDateString:String?
    var recordDate:String?
    var status:Int?
    var startTime:String?
    var endTime:String?
    var appealReason:String?
    var selfHolidayType:String?
    var address:String?
    var appealDescription:String?
    var currentProcessor:String?
    var processPerson1:String?
    var processPersonDepartment1:String?
    var processPersonCompany1:String?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id<-map["id"]
        createTime<-map["createTime"]
        updateTime<-map["updateTime"]
        sequence<-map["sequence"]
        detailId<-map["detailId"]
        empName<-map["empName"]
        companyName<-map["companyName"]
        departmentName<-map["departmentName"]
        yearString<-map["yearString"]
        monthString<-map["monthString"]
        appealDateString<-map["appealDateString"]
        recordDateString<-map["recordDateString"]
        recordDate<-map["recordDate"]
        status<-map["status"]
        startTime<-map["startTime"]
        endTime<-map["endTime"]
        appealReason<-map["appealReason"]
        selfHolidayType<-map["selfHolidayType"]
        address<-map["address"]
        appealDescription<-map["appealDescription"]
        currentProcessor<-map["currentProcessor"]
        processPerson1<-map["processPerson1"]
        processPersonDepartment1<-map["processPersonDepartment1"]
        processPersonCompany1<-map["processPersonCompany1"]
    }
}


class AttendanceBackData:Mappable{
    var message:String?
    var status:String?
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        message<-map["message"]
        status<-map["status"]
    }
}


class AttendanceDetailWrapInFilter:Mappable{
    var cycleYear:String?//年份 如 2016
    var cycleMonth:String?//月份 如 04
    var key:String?//recordDateString
    var order:String?//排序 desc asc
    var q_empName:String?//当前用户
    
    init() {
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        cycleYear<-map["cycleYear"]
        cycleMonth<-map["cycleMonth"]
        key<-map["key"]
        order<-map["order"]
        q_empName<-map["q_empName"]
    }
}

class AttendanceAppealApprovalWrapInFilter:Mappable{
    var status:String? // 0待审批 1审批通过 -1审批未通过 999所有
    var yearString:String? //年份 2016
    var monthString:String?
    var processPerson1:String? //审批人 就是当前用户
    var appealReason:String?
    var departmentName:String?
    var empName:String?
    init() {
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        status<-map["status"]
        yearString<-map["yearString"]
        monthString<-map["monthString"]
        processPerson1<-map["processPerson1"]
        appealReason<-map["appealReason"]
        departmentName<-map["departmentName"]
        empName<-map["empName"]
    }
}




