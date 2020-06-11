//
//  OOCalendarModels.swift
//  O2Platform
//
//  Created by FancyLou on 2018/7/24.
//  Copyright © 2018 zoneland. All rights reserved.
//

import HandyJSON


//MARK: - 日历对象
class OOCalendarInfo: NSObject, DataModel {
    @objc var id: String?
    @objc var name: String?
    @objc var type: String?
    @objc var color: String?
    var manageable: Bool?
    var isPublic: Bool?
    var followed: Bool?
    @objc var target: String?
    @objc var desc: String? // 服务端字段是：description 这边冲突所以用desc
    @objc var status: String?
    @objc var createor: String?
    
    // @objc var publishable:Bool?
    @objc var source:String?
    @objc var manageablePersonList:[String]?
    @objc var followers:[String]?
    @objc var viewablePersonList:[String]?
    @objc var viewableUnitList:[String]?
    @objc var viewableGroupList:[String]?
    @objc var publishablePersonList:[String]?
    @objc var publishableUnitList:[String]?
    @objc var publishableGroupList:[String]?

    
    required override init(){}
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
}

//MARK: - 我的日历列表
class OOMyCalendarList: NSObject, DataModel {
    @objc var myCalendars: [OOCalendarInfo]?
    @objc var unitCalendars: [OOCalendarInfo]?
    @objc var followCalendars: [OOCalendarInfo]?
    
    
    required override init() {
    }
}

//MARK: - 日程对象
class OOCalendarEventInfo: NSObject, DataModel {
    @objc var id: String?
    @objc var calendarId: String?
    @objc var repeatMasterId: String?
    @objc var eventType: String?
    @objc var title: String?
    @objc var color: String?
    @objc var comment: String?
    @objc var startTime: String?
    @objc var startTimeStr: String?
    @objc var endTime: String?
    @objc var endTimeStr: String?
    @objc var locationName: String?
    @objc var recurrenceRule: String?
    var alarm: Bool?
    @objc var alarmTime: String?
    var alarmAlready: Bool?
    @objc var valarmTime_config: String?
    @objc var valarm_Summary: String?
    var isAllDayEvent: Bool?
    var daysOfDuration: Int?
    var isPublic: Bool?
    @objc var source: String?
    @objc var createPerson: String?
    @objc var updatePerson: String?
    @objc var targetType: String?
    @objc var participants: [String]?
    @objc var manageablePersonList: [String]?
    @objc var viewablePersonList: [String]?
    @objc var viewableUnitList: [String]?
    @objc var viewableGroupList: [String]?
    
    
    required override init() {
    }
}
// 某一天的日程对象
class OOCalendarEventInOneDay: NSObject, DataModel {
    @objc var eventDate:String?
    @objc var inOneDayEvents: [OOCalendarEventInfo]?
    required override init() {
        
    }
}
// 查询日程返回对象
class OOCalendarEventResponse: NSObject, DataModel {
    @objc var inOneDayEvents: [OOCalendarEventInOneDay]?
    @objc var wholeDayEvents: [OOCalendarEventInfo]?
    
    required override init() {
        
    }
}
// 查询日程条件对象
class OOCalendarEventFilter: NSObject, DataModel {
    @objc var calendarIds:[String]?
    @objc var createPerson:String?
    @objc var startTime:String?
    @objc var endTime:String?
    
    required override init() {
        
    }
}
