//
//  CCDataExtension.swift
//  JChat
//
//  Created by deng on 2017/2/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

let FORMAT_PAST_SHORT:String = "yyyy/MM/dd"
let FORMAT_PAST_TIME:String = "ahh:mm"
let FORMAT_THIS_WEEK:String = "eee ahh:mm"
let FORMAT_THIS_WEEK_SHORT:String = "eee"
let FORMAT_YESTERDAY:String = "ahh:mm"
let FORMAT_TODAY:String = "ahh:mm"

let D_MINUTE = 60.0
let D_HOUR = 3600.0
let D_DAY = 86400.0
let D_WEEK = 604800.0
let D_YEAR = 31556926.0

internal let componentFlags: NSCalendar.Unit = [.year, .month, .day, .weekday, .hour, .minute, .second, .weekdayOrdinal]

extension Date {
    static func currentCalendar1() -> Calendar{
        let calendar = Calendar.autoupdatingCurrent
        return calendar
    }
    
    func isEqualToDateIgnoringTime(_ date:Date) -> Bool{
        let components1:DateComponents = (Date.currentCalendar() as NSCalendar).components(componentFlags, from: self)
        let components2:DateComponents = (Date.currentCalendar() as NSCalendar).components(componentFlags, from: date)
        return (components1.year == components2.year) && (components1.month == components2.month) && ((components1).day == components2.day)
        
    }
    
    func dateByAddingDays(_ days:Int) -> Date {
        var dateComponents = DateComponents()
        dateComponents.day = days
        let newDate:Date = (Calendar.current as NSCalendar).date(byAdding: dateComponents, to: self, options:NSCalendar.Options(rawValue: 0))!
        return newDate
    }
    
    func dateBySubtractingDays(_ days:Int) -> Date {
        return self.dateByAddingDays(days * -1)
    }
    
    static func dateWithDaysFromNow(_ days:Int) -> Date {
        return Date().dateByAddingDays(days)
    }
    
    static func dateWithDaysBeforeNow(_ days:Int) -> Date {
        return Date().dateBySubtractingDays(days)
    }
    
    static func dateTomorrow() -> Date{
        return Date.dateWithDaysFromNow(1)
    }
    
    static func dateYesterday() -> Date {
        
        return Date.dateWithDaysBeforeNow(1)
    }
    
    func isToday() -> Bool {
        return self.isEqualToDateIgnoringTime(Date())
    }
    
    func isTomorrow() -> Bool {
        return self.isEqualToDateIgnoringTime(Date.dateTomorrow())
    }
    
    func isYesterday() -> Bool {
        return self.isEqualToDateIgnoringTime(Date.dateYesterday())
    }
    
    func isSameWeekAsDate(_ date:Date) -> Bool {
        let components1:DateComponents = (Date.currentCalendar1() as NSCalendar).components(componentFlags, from: self)
        let components2:DateComponents = (Date.currentCalendar1() as NSCalendar).components(componentFlags, from: date)
        if components1.weekOfYear != components2.weekOfYear { return false }
        return (fabs(self.timeIntervalSince(date)) < D_WEEK)
    }
    
    func isThisWeek() -> Bool {
        return self.isSameWeekAsDate(Date())
    }
    
    func isThisYear() -> Bool {
        let calendar = NSCalendar.current
        let year1 = calendar.component(.year, from: Date())
        let year2 = calendar.component(.year, from: self)
        return year1 == year2
    }
    
    func conversationDate() -> String {
        
        // yy-MM-dd hh:mm
        // MM-dd hh:mm
        // 星期一 hh:mm - 7 * 24小时内
        // 昨天 hh:mm - 2 * 24小时内
        // 今天 hh:mm - 24小时内
        
        let s1 = TimeInterval(self.timeIntervalSince1970)
        let s2 = TimeInterval(time(nil))
        
        let dz = TimeInterval(TimeZone.current.secondsFromGMT())
        
        let formatter = DateFormatter()
//        let format1 = DateFormatter.dateFormat(fromTemplate: "hh:mm", options: 0, locale: nil) ?? "hh:mm"
        
        let days1 = Int64(s1 + dz) / (24 * 60 * 60)
        let days2 = Int64(s2 + dz) / (24 * 60 * 60)
        
        switch days1 - days2 {
        case +0:
            // Today
//            formatter.dateFormat = "\(format1)"
            formatter.dateFormat = "hh:mm"
        case +1:
            // Tomorrow
            formatter.dateFormat = "'明天'"
        case +2 ... +7:
            // 2 - 7 day later
            formatter.dateFormat = "EEEE"
        case -1:
            formatter.dateFormat = "'昨天'"
        case -2:
            formatter.dateFormat = "'前天'"
        case -7 ... -2:
            // 2 - 7 day ago
            formatter.dateFormat = "EEEE"
        default:
            // Distant
            if self.isThisYear() {
                formatter.dateFormat = "MM-dd"
            } else {
                formatter.dateFormat = "yy-MM-dd"
            }
        }
        return formatter.string(from: self)
    }
}
