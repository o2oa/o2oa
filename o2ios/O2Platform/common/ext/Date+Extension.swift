//
//  Date+Extension.swift
//  O2Platform
//
//  Created by FancyLou on 2018/7/26.
//  Copyright © 2018 zoneland. All rights reserved.
//

import Foundation

extension Date {
    
    func add(component: Calendar.Component, value: Int) -> Date {
        return Calendar.current.date(byAdding: component, value: value, to: self)!
    }
    
    var startOfDay: Date {
        return Calendar.current.startOfDay(for: self)
    }
    
    func getDateWithDay() -> Int {
        
        return NSCalendar.current.component(.day, from: self)
    }
    
    func getDateWithMonth() -> Int {
        return NSCalendar.current.component(.month, from: self)
    }
    
    func getDateWithYear() -> Int {
        return NSCalendar.current.component(.year, from: self)
    }
    
    /// 判断当前日期是否为今年
//    func isThisYear() -> Bool {
//        // 获取当前日历
//        let calender = Calendar.current
//        // 获取日期的年份
//        let yearComps = calender.component(.year, from: self)
//        // 获取现在的年份
//        let nowComps = calender.component(.year, from: Date())
//
//        return yearComps == nowComps
//    }
    
    /// 是否是昨天
//    func isYesterday() -> Bool {
//        // 获取当前日历
//        let calender = Calendar.current
//        // 获取日期的年份
//        let comps = calender.dateComponents([.year, .month, .day], from: self, to: Date())
//        // 根据头条显示时间 ，我觉得可能有问题 如果comps.day == 0 显示相同，如果是 comps.day == 1 显示时间不同
//        // 但是 comps.day == 1 才是昨天 comps.day == 2 是前天
//        //        return comps.year == 0 && comps.month == 0 && comps.day == 1
//        return comps.year == 0 && comps.month == 0 && comps.day == 0
//    }
    
    /// 是否是前天
    func isBeforeYesterday() -> Bool {
        // 获取当前日历
        let calender = Calendar.current
        // 获取日期的年份
        let comps = calender.dateComponents([.year, .month, .day], from: self, to: Date())
        //
        //        return comps.year == 0 && comps.month == 0 && comps.day == 2
        return comps.year == 0 && comps.month == 0 && comps.day == 1
    }
    
    /// 判断是否是今天
//    func isToday() -> Bool {
//        // 日期格式化
//        let formatter = DateFormatter()
//        // 设置日期格式
//        formatter.dateFormat = "yyyy-MM-dd"
//
//        let dateStr = formatter.string(from: self)
//        let nowStr = formatter.string(from: Date())
//        return dateStr == nowStr
//    }
    
}

extension Date {
    
    /// String -> Date
    ///
    /// - Parameters:
    ///   - dateStr: date string
    ///   - formatter: date formatter
    /// - Returns: Date
    static func date(_ dateStr: String, formatter: String = "yyyy-MM-dd HH:mm:ss") -> Date? {
        
        let dateFormatter = DateFormatter()
        
        dateFormatter.dateFormat = formatter
        
        dateFormatter.locale = Locale.current
        
        return dateFormatter.date(from: dateStr)
    }
    
    /// Date -> String
    ///
    /// - Parameter formatter: date formatter
    /// - Returns: date string
    func toString(_ formatter: String) -> String {
        let dateFormatter = DateFormatter()
        
        dateFormatter.dateFormat = formatter
        
        dateFormatter.locale = Locale.current
        
        return dateFormatter.string(from: self)
    }
    
}

extension Date {
    static func currentCalendar() -> Calendar {
        var sharedCalendar = Calendar(identifier: .gregorian)
        
        sharedCalendar.locale = Locale.current
        
        return sharedCalendar
    }
    
    
    /// Example: 2000/1/2 03:04:05 return 2000
    var year: Int {
        get {
            return Date.currentCalendar().component(.year, from: self)
        }
    }
    
    /// Example: 2000/1/2 03:04:05 return 1
    var month: Int {
        get {
            return Date.currentCalendar().component(.month, from: self)
        }
    }
    
    /// Example: 2000/1/2 03:04:05 return 2
    var day: Int {
        get {
            return Date.currentCalendar().component(.day, from: self)
        }
    }
    
    /// Example: 2000/1/2 03:04:05 return 3
    var hour: Int {
        get {
            return Date.currentCalendar().component(.hour, from: self)
        }
    }
    
    /// Example: 2000/1/2 03:04:05 return 4
    var minute: Int {
        get {
            return Date.currentCalendar().component(.minute, from: self)
        }
    }
    
    /// Example: 2000/1/2 03:04:05 return 5
    var second: Int {
        get {
            return Date.currentCalendar().component(.second, from: self)
        }
    }
    
    /// 当前时间的月份的第一天是周几
    var firstWeekDayInThisMonth: Int {
        var calendar = Calendar.current
        let componentsSet = Set<Calendar.Component>([.year, .month, .day])
        var components = calendar.dateComponents(componentsSet, from: self)
        
        calendar.firstWeekday = 1
        components.day = 1
        let first = calendar.date(from: components)
        let firstWeekDay = calendar.ordinality(of: .weekday, in: .weekOfMonth, for: first!)
        return firstWeekDay! - 1
    }
    /// 当前时间的月份共有多少天
    var totalDaysInThisMonth: Int {
        let totalDays = Calendar.current.range(of: .day, in: .month, for: self)
        return (totalDays?.count)!
    }
    
    /// 上个月份的此刻日期时间
    var lastMonth: Date {
        var dateComponents = DateComponents()
        dateComponents.month = -1
        let newData = Calendar.current.date(byAdding: dateComponents, to: self)
        return newData!
    }
    /// 下个月份的此刻日期时间
    var nextMonth: Date {
        var dateComponents = DateComponents()
        dateComponents.month = +1
        let newData = Calendar.current.date(byAdding: dateComponents, to: self)
        return newData!
    }
}

extension Date {
    
    /// the same year
    ///
    /// - Parameter date: contrast time
    /// - Returns: true: equal; false: not equal
    func haveSameYear(_ date: Date) -> Bool {
        return self.year == date.year
    }
    
    func haveSameYearAndMonth(_ date: Date) -> Bool {
        return self.haveSameYear(date) && self.month == date.month
    }
    
    func haveSameYearMonthAndDay(_ date: Date) -> Bool {
        let components1 = Date.currentCalendar().dateComponents([.year, .month, .day], from: self)
        let components2 = Date.currentCalendar().dateComponents([.year, .month, .day], from: date)
        return components1 == components2
    }
    
    func haveSameYearMonthDayAndHour(_ date: Date) -> Bool {
        let components1 = Date.currentCalendar().dateComponents([.year, .month, .day, .hour], from: self)
        let components2 = Date.currentCalendar().dateComponents([.year, .month, .day, .hour], from: date)
        return components1 == components2
    }
    
    func haveSameYearMonthDayHourAndMinute(_ date: Date) -> Bool {
        let components1 = Date.currentCalendar().dateComponents([.year, .month, .day, .hour, .minute], from: self)
        let components2 = Date.currentCalendar().dateComponents([.year, .month, .day, .hour, .minute], from: date)
        return components1 == components2
    }
    
    func haveSameYearMonthDayHourMinuteAndSecond(_ date: Date) -> Bool {
        let components1 = Date.currentCalendar().dateComponents([.year, .month, .day, .hour, .minute, .second], from: self)
        let components2 = Date.currentCalendar().dateComponents([.year, .month, .day, .hour, .minute, .second], from: date)
        return components1 == components2
    }
}

extension Date {
    
    /// the number of days in the month
    ///
    /// - Returns: number of day
    func numberOfDaysInMonth() -> Int {
        if let range = Date.currentCalendar().range(of: .day, in: .month, for: self) {
            return range.count
        }
        
        return 0
    }
    
    /// 格式化时间
    ///
    /// - Parameters:
    ///   - formatter: 格式 yyyy-MM-dd/YYYY-MM-dd/HH:mm:ss/yyyy-MM-dd HH:mm:ss
    /// - Returns: 格式化后的时间 String
    func formatterDate(formatter: String) -> String {
        let dateformatter = DateFormatter()
        dateformatter.dateFormat = formatter
        let dateString = dateformatter.string(from: self)
        return dateString
    }
    
    
    func friendlyTime() -> String {
        var returnTimeString = ""
        let now = Date()
        if now.haveSameYearMonthDayAndHour(self) {
            let gap = now.minute - self.minute
            returnTimeString = "\(gap)分钟前"
        }else if now.haveSameYearMonthAndDay(self) {
            let gap = now.hour - self.hour
            returnTimeString = "\(gap)小时前"
        }else if now.haveSameYearAndMonth(self) {
            let gap = now.day - self.day
            if gap == 1 {
                returnTimeString = "昨天"
            }else if gap == 2 {
                returnTimeString = "前天"
            }else {
                returnTimeString = "\(gap)天前"
            }
        }else if now.haveSameYear(self) {
            let gap = now.month - self.month
            if gap < 4 {
                returnTimeString = "\(gap)个月前"
            }else {
                returnTimeString = self.formatterDate(formatter: "yyyy-MM-dd")
            }
        }else {
            returnTimeString = self.formatterDate(formatter: "yyyy-MM-dd")
        }
        return returnTimeString
    }
}

