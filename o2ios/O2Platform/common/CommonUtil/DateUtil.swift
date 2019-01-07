//
//  DateUtils.swift
//  CommonUtil
//
//  Created by lijunjie on 15/11/9.
//  Copyright © 2015年 lijunjie. All rights reserved.
//

import Foundation

public class DateUtil {
    public let kNSDateHelperFormatFullDateWithTime    = "MMM d, yyyy h:mm a"
    public let kNSDateHelperFormatFullDate            = "MMM d, yyyy"
    public let kNSDateHelperFormatShortDateWithTime   = "MMM d h:mm a"
    public let kNSDateHelperFormatShortDate           = "MMM d"
    public let kNSDateHelperFormatWeekday             = "EEEE"
    public let kNSDateHelperFormatWeekdayWithTime     = "EEEE h:mm a"
    public let kNSDateHelperFormatTime                = "h:mm a"
    public let kNSDateHelperFormatTimeWithPrefix      = "'at' h:mm a"
    public let kNSDateHelperFormatSQLDate             = "yyyy-MM-dd"
    public let kNSDateHelperFormatSQLTime             = "HH:mm:ss"
    public let kNSDateHelperFormatSQLDateWithTime     = "yyyy-MM-dd HH:mm:ss"
    
    static let share = DateUtil()
    
    private init () {}
    
    public func sharedCalendar() -> NSCalendar {
        var res = NSCalendar.current
        res.timeZone = NSTimeZone.system
        res.firstWeekday = 2
        return res as NSCalendar
    }
    
    public func sharedDateFormatter() -> DateFormatter {
//        struct Static {
//            static var onceToken: dispatch_once_t = 0
//            static var instance: DateFormatter? = nil
//        }
//        dispatch_once(&Static.onceToken) {
//            Static.instance = DateFormatter()
//        }
//        return Static.instance!
        return {DateFormatter()}()
    }
    
    public func detailTimeAgoString(date: NSDate) -> String {
        let timeNow = date.timeIntervalSince1970
        let calendar = self.sharedCalendar()
        let unitFlags = NSCalendar.Unit.month.rawValue | NSCalendar.Unit.day.rawValue | NSCalendar.Unit.year.rawValue | NSCalendar.Unit.hour.rawValue | NSCalendar.Unit.minute.rawValue | NSCalendar.Unit.second.rawValue | NSCalendar.Unit.weekOfYear.rawValue | NSCalendar.Unit.weekday.rawValue
        var component = calendar.components(NSCalendar.Unit(rawValue: unitFlags), from: date as Date)
        let year = component.year
        let month = component.month
        let day = component.day
        let today = NSDate()
        component = calendar.components(NSCalendar.Unit(rawValue: unitFlags), from: today as Date)
        
        let t_year = component.year
        
        var string: String
        
        let now = today.timeIntervalSince1970
        let distance = now - timeNow
        if distance < 60 {
            string = "刚刚"
        } else if distance < 60*60 {
            string = String(format: "%lld分钟前", distance/60)
        } else if distance < 60*60*24 {
            string = String(format: "%lld小时前", distance/60/60)
        } else if distance < 60*60*24*7 {
            string = String(format: "%lld天前", distance/60/60/24)
        } else if year == t_year {
            string = String(format: "%ld月%ld日", month!, day!)
        } else {
            string = String(format: "%ld年%ld月%ld日", year!, month!, day!)
        }
        return string
    }
    
    public func detailTimeAgoStringByInterval(timeInterval: TimeInterval) -> String {
        return self.detailTimeAgoString(date: self.dateFromTimeInterval(timeInterval: timeInterval))
    }
    
    public func daysAgoFromNow(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let components = calendar.components(NSCalendar.Unit.day, from: date as Date, to: NSDate() as Date, options: NSCalendar.Options(rawValue: 0))
        return components.day!
    }
    
    public func daysAgoAgainstMidnight(date: NSDate) -> Int {
        let mdf = self.sharedDateFormatter()
        mdf.dateFormat = kNSDateHelperFormatSQLDate
        let midnight = mdf.date(from: mdf.string(from: date as Date))
        return Int((midnight?.timeIntervalSinceNow)! / (-60 * 60 * 24))
    }
    
    public func stringDaysAgoAgainstMidnight(flag: Bool, withDate date:NSDate ) -> String {
        let daysAgo: Int = flag ? self.daysAgoAgainstMidnight(date: date) : self.daysAgoFromNow(date: date)
        var text: String
        switch daysAgo {
        case 0:
            text = "今天"
        case 1:
            text = "昨天"
        default:
            text = String(format: "%lu天前", daysAgo)
        }
        return text
    }
  
    public func stringDaysAgo(date:NSDate) -> String {
        return self.stringDaysAgoAgainstMidnight(flag: true, withDate: date)
    }
    
    /*
     * iOS中规定的就是周日为1，周一为2，周二为3，周三为4，周四为5，周五为6，周六为7，
     * 无法通过某个设置改变这个事实的，只能在使用的时候注意一下这个规则了。
     */
    public func weekDay(date: NSDate) -> Int {
        let weekdayComponents = self.sharedCalendar().components(NSCalendar.Unit.weekday, from: date as Date)
        var wDay = weekdayComponents.weekday!
        if wDay == 1 {
            wDay = 7
        } else {
            wDay -= 1
        }
        return wDay
    }
    
    public func weekDayString(date: NSDate) -> String {
        let weekNameDict = [
            1 : "一",
            2 : "二",
            3 : "三",
            4 : "四",
            5 : "五",
            6 : "六",
            7 : "日"
        ]
        let weekName = weekNameDict[self.weekDay(date: date)]
        return String(format: "星期%@", weekName!)
    }

    
    public func weekNumberString(date: NSDate) -> String {
        return String(format: "第%lu周", self.weekNumber(date: date))
    }

    public func weekNumber(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let dateComponents = calendar.components(NSCalendar.Unit.weekOfYear, from: date as Date)
        return dateComponents.weekOfYear!
    }
    
    public func hour(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let dateComponents = calendar.components(NSCalendar.Unit.hour, from: date as Date)
        return dateComponents.hour!
    }
    
    public func minute(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let dateComponents = calendar.components(NSCalendar.Unit.minute, from: date as Date)
        return dateComponents.minute!
    }
    
    public func year(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let dateComponents = calendar.components(NSCalendar.Unit.year, from: date as Date)
        return dateComponents.year!
    }
    
    public func month(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let dateComponents = calendar.components(NSCalendar.Unit.month, from: date as Date)
        return dateComponents.month!
    }
    
    public func day(date: NSDate) -> Int {
        let calendar = self.sharedCalendar()
        let dateComponents = calendar.components(NSCalendar.Unit.day, from: date as Date)
        return dateComponents.day!
    }
    
    public func dateFromTimeInterval(timeInterval: TimeInterval) -> NSDate {
        return NSDate(timeIntervalSince1970: timeInterval)
    }
    
    public func dateFromString(string: String) -> NSDate {
        return self.dateFromString(string: string, withFormat: kNSDateHelperFormatSQLDate)
    }
    
    public func dateTimeFromString(string: String) -> NSDate {
        return self.dateFromString(string: string, withFormat:kNSDateHelperFormatSQLDateWithTime)
    }
    
    public func dateFromString(string: String, withFormat format: String) -> NSDate {
        let formatter = self.sharedDateFormatter()
        formatter.dateFormat = format
        return formatter.date(from: string)! as NSDate
    }
    
    public func stringFromDate(date: NSDate, withFormat format: String) -> String {
        let formatter = self.sharedDateFormatter()
        formatter.dateFormat = format
        return self.sharedDateFormatter().string(from: date as Date)
    }
    
    public func stringFromDate(date: NSDate) -> String {
        return self.stringFromDate(date: date, withFormat: kNSDateHelperFormatSQLDateWithTime)
    }
    
    public func stringWithDateStyle(dateStyle: DateFormatter.Style, timeStyle: DateFormatter.Style, withDate date: NSDate) -> String {
        let formatter = self.sharedDateFormatter()
        formatter.dateStyle = dateStyle
        formatter.timeStyle = timeStyle
        return formatter.string(from: date as Date)
    }
    
    public func beginningOfWeek(date: NSDate) -> NSDate {
        let calendar = self.sharedCalendar()
        var beginningOfWeek: NSDate? = nil
        let ok = calendar.range(of: NSCalendar.Unit.weekOfYear, start: &beginningOfWeek, interval: nil, for: date as Date)
        if ok {
            return beginningOfWeek!
        }
        let weekdayComponents = calendar.components(NSCalendar.Unit.weekday, from: date as Date)
        let componentsToSubtract = NSDateComponents()
        componentsToSubtract.day = -(weekdayComponents.weekday! - 1)
        beginningOfWeek = nil
        beginningOfWeek = calendar.date(byAdding: componentsToSubtract as DateComponents, to: date as Date, options: NSCalendar.Options(rawValue: 0)) as NSDate?
        let components = calendar.components(NSCalendar.Unit(rawValue: NSCalendar.Unit.year.rawValue | NSCalendar.Unit.month.rawValue | NSCalendar.Unit.day.rawValue), from: beginningOfWeek! as Date)
        return calendar.date(from: components)! as NSDate
        
    }
    
    public func beginningOfDay(date: NSDate) -> NSDate {
        let calendar = self.sharedCalendar()
        let components = calendar.components(NSCalendar.Unit(rawValue: NSCalendar.Unit.year.rawValue | NSCalendar.Unit.month.rawValue | NSCalendar.Unit.day.rawValue), from: date as Date)
        return calendar.date(from: components)! as NSDate
    }
    
    public func endOfWeek(date: NSDate) -> NSDate {
        let calendar = self.sharedCalendar()
        let weekdayComponents = calendar.components(NSCalendar.Unit(rawValue: NSCalendar.Unit.weekday.rawValue), from: date as Date)
        let componentsToAdd = NSDateComponents()
        componentsToAdd.day = 7 - weekdayComponents.weekday!
        return calendar.date(byAdding: componentsToAdd as DateComponents, to: date as Date, options: NSCalendar.Options(rawValue: 0))! as NSDate
        
    }
    
    public func dateFormatString() -> String {
        return kNSDateHelperFormatSQLDate
    }
    
//
//    + (NSString *)dateFormatString
//    {
//    return kNSDateHelperFormatSQLDate
//    }
//    
//    + (NSString *)timeFormatString
//    {
//    return kNSDateHelperFormatSQLTime
//    }
//    
//    + (NSString *)timestampFormatString
//    {
//    return kNSDateHelperFormatSQLDateWithTime
//    }
//    
//    + (NSString *)dbFormatString
//    {
//    return kNSDateHelperFormatSQLDateWithTime
//    }
    
    public func birthdayToAge(date: NSDate) -> String {
        let calendar = self.sharedCalendar()
        let components = calendar.components(NSCalendar.Unit(rawValue: NSCalendar.Unit.year.rawValue | NSCalendar.Unit.month.rawValue | NSCalendar.Unit.day.rawValue), from: date as Date, to: NSDate() as Date, options:NSCalendar.Options(rawValue: 0))
        if components.year! > 0 {
            return String(format:"%ld岁", components.year!)
        } else if components.month! > 0 {
            return String(format:"%ld个月%ld天" , components.month!, components.day!)
        } else {
            return String(format: "%ld天", components.day!)
        }
        
    }
    
    public func birthdayToAgeByTimeInterval(date: TimeInterval) -> String {
        return self.birthdayToAge(date: self.dateFromTimeInterval(timeInterval: date))
    }
    
    public func dateToConstellation(date: NSDate) -> String? {
        let day = self.day(date: date)
        let month = self.month(date: date)
        if day == NSNotFound || month == NSNotFound {
            return nil
        }
        
        let constellations = [
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"
        ]
        var res: String? = nil
        
        if day <= 22 {
            if month != 1 {
                res = constellations[month - 2]
            } else {
                res = constellations[11]
            }
        } else {
            res = constellations[month - 1]
        }
        return res
    }
    
    public func dateToConstellationByTimeInterval(date: TimeInterval) -> String {
        return self.dateToConstellation(date: self.dateFromTimeInterval(timeInterval: date))!
    }
    // 月份开始日期
    public func monthStartDate(date: Date) -> Date {
            let calendar = Calendar.current
            let components = calendar.dateComponents(
                Set<Calendar.Component>([.year, .month]), from: date)
            let startOfMonth = calendar.date(from: components)!
            return startOfMonth
    }
    //月份最后日期 startDate是月份开始日期
    //returnEndTime 最后的时间是 true 23:59:59 false 00:00:00
    public func monthEndDate(startDate: Date, returnEndTime:Bool = true) -> Date {
        let calendar = Calendar.current
        var components = DateComponents()
        components.month = 1
        if returnEndTime {
            components.second = -1
        } else {
            components.day = -1
        }
        let endOfMonth =  calendar.date(byAdding: components, to: startDate)!
        return endOfMonth
    }
    
    public func getMonthEnd(date: NSDate) -> NSDate {
        let format = DateFormatter.init()
        format.dateFormat = "yyyy-MM"
        let newDate: Date = format.date(from: format.string(from: date as Date))!
        var interval: Double = 0
        var beginDate: Date = Date()
        var endDate: Date = Date()
        var calendar = NSCalendar.current
        calendar.firstWeekday = 2 //设定周一为周首日
        
        let ok: Bool = calendar.dateInterval(of: .month, start: &beginDate, interval: &interval, for: newDate)
        //分别修改为 NSDayCalendarUnit NSWeekCalendarUnit NSYearCalendarUnit
        if (ok) {
            endDate = beginDate.addingTimeInterval(interval - 1)
        }else {
            return Date.init() as NSDate;
        }
        
        let myDateFormatter = DateFormatter.init()
        myDateFormatter.dateFormat = "yyyy-MM-dd"
        endDate = myDateFormatter.date(from: myDateFormatter.string(from: endDate))!
        
        return endDate as NSDate;
    }
    
    public func getMonthBegin(date: NSDate) -> NSDate {
        let format = DateFormatter.init()
        format.dateFormat = "yyyy-MM"
        let newDate: Date = format.date(from: format.string(from: date as Date))!
        var interval: Double = 0
        var beginDate: Date = Date()
        var endDate: Date = Date()
        var calendar = NSCalendar.current
        calendar.firstWeekday = 2 //设定周一为周首日
        
        let ok: Bool = calendar.dateInterval(of: .month, start: &beginDate, interval: &interval, for: newDate)
        //分别修改为 NSDayCalendarUnit NSWeekCalendarUnit NSYearCalendarUnit
        if (ok) {
            endDate = beginDate.addingTimeInterval(interval - 1)
        }else {
            return Date.init() as NSDate;
        }
        
        let myDateFormatter = DateFormatter.init()
        myDateFormatter.dateFormat = "yyyy-MM-dd"
        endDate = Date.init(timeInterval: -86400, since: beginDate)
        
        return endDate as NSDate;
    }
    
    
    public func compareDate(oneDay: NSDate, anotherDay: NSDate) -> Int{
        
        let dateFormatter = DateFormatter.init()
        dateFormatter.dateFormat = "yyyy-MM"
        
        let dateA = dateFormatter.date(from: dateFormatter.string(from: oneDay as Date))
        let dateB = dateFormatter.date(from: dateFormatter.string(from: anotherDay as Date))
        
        let result = dateA?.compare(dateB!)
        if (result!.rawValue == ComparisonResult.orderedDescending.rawValue) {
            //NSLog(@"Date1  is in the future");
            return 1;
        }
        else if (result!.rawValue == ComparisonResult.orderedAscending.rawValue){
            //NSLog(@"Date1 is in the past");
            return -1;
        }
        //NSLog(@"Both dates are the same");
        return 0;
    }
    
}

public let SharedDateUtil: DateUtil = DateUtil.share
