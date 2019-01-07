//
//  DatePickerView.swift
//  DemoSwift
//
//  Created by yaoxinpan on 2018/5/28.
//  Copyright © 2018年 yaoxp. All rights reserved.
//

import UIKit


protocol NibLoadable {}

extension NibLoadable {
    static func loadViewFromNib() -> Self {
        return Bundle.main.loadNibNamed("\(self)", owner: nil, options: nil)?.last as! Self
    }
}

enum DateStyle {
    case all                    // 年月日时分秒
    case yearMonthDay           // 年月日
    case hourMinuteSecond       // 时分秒
    case yearMonthDayHourMinute // 年月日时分
}

class DatePickerView: UIView, NibLoadable, UIGestureRecognizerDelegate, UIPickerViewDelegate, UIPickerViewDataSource {

    // MARK: - 公开属性
    
    /// 最小的时间，默认是1970/1/1 00:00:00
    var minLimitDate: Date = Date(timeIntervalSince1970: 0) {
        didSet {
            
            if maxLimitDate < minLimitDate {
                
                minLimitDate = oldValue
                
            } else {

                if minLimitDate > scrollToDate {
                    
                    scrollToDate = minLimitDate
                    
                }
            }
        }
    }
    
    /// 最大的时间，默认是当前时间10年后
    var maxLimitDate: Date = Date(timeIntervalSinceNow: 60 * 60 * 24 * 365 * 10) {
        didSet {
            if maxLimitDate < minLimitDate {
                
                maxLimitDate = oldValue
                
            } else {
                
                if maxLimitDate < scrollToDate {
                    
                    scrollToDate = maxLimitDate
                    
                }
            }
        }
    }
    
    /// 默认显示的当前时间
    var scrollToDate: Date = Date() {
        didSet {
            
            if maxLimitDate < scrollToDate {
                
                scrollToDate = maxLimitDate
                
            }
            
            if minLimitDate > scrollToDate {
                
                scrollToDate = minLimitDate
                
            }
        }
    }
    
    /// 确定按钮背景色，默认蓝色
    var sureButtonBackgroundColor: UIColor? {
        didSet {
            sureButton.backgroundColor = sureButtonBackgroundColor
        }
    }
    
    /// 确定按钮字体颜色，默认白色
    var sureButtonTitleColor: UIColor? {
        didSet {
            sureButton.setTitleColor(sureButtonTitleColor, for: .normal)
        }
    }
    
    /// 年 背景水印，设置无色可以隐藏
    var yearPlaceholderColor: UIColor? {
        didSet {
            yearLabel.textColor = yearPlaceholderColor
        }
    }
    
    // MARK: - 私有属性
    
    @IBOutlet private weak var bottomView: UIView!
    @IBOutlet private weak var sureButton: UIButton!
    @IBOutlet private weak var yearLabel: UILabel!
    @IBOutlet private weak var bottomViewBottom: NSLayoutConstraint!
    @IBOutlet private weak var pickerView: UIPickerView!
    
    private var componentsArray: Array<Dictionary> = [Dictionary<String, Array<Int>>]()
    
    /// 显示的时间样式
    private var style: DateStyle = .all
    
    private let yearKey = "年"
    private let monthKey = "月"
    private let dayKey = "日"
    private let hourKey = "时"
    private let minuteKey = "分"
    private let secondKey = "秒"
    private var completionHandler: (_ date: Date?) -> Void = {_ in }
    
    ///
    /// - Parameters:
    ///   - style: 类型
    ///   - scrollToDate: 滚动到的时间，如果不设刚是当前时间
    /// - Returns: 新的DatePickerView
    class func datePicker(style: DateStyle = .all, scrollToDate: Date = Date(), completionHandler: @escaping (_ date: Date?) -> Void) -> DatePickerView {
        
        let view: DatePickerView = DatePickerView.loadViewFromNib()
        
        view.style = style
        
        view.completionHandler = completionHandler
        
        view.scrollToDate = scrollToDate
        
        view.setupUI()
        
        view.setupData()
        
        return view
    }
    
    func show() {
        
        setupData()
        
        UIApplication.shared.keyWindow!.addSubview(self)
        
        self.frame = UIScreen.main.bounds
        
        UIView.animate(withDuration: 0.3, animations: {
            
            self.bottomViewBottom.constant = 0
            
            self.backgroundColor = UIColor.hexRGB(0x000000, 0.5)
            
            self.layoutIfNeeded()
        }, completion: { (finish) in
            
            self.scrollTo(self.scrollToDate)
            
        })
        
    }
    
    @objc func dismiss() {
        UIView.animate(withDuration: 0.3, animations: {
            
            self.bottomViewBottom.constant = self.bottomView.frame.size.height
            
            self.backgroundColor = UIColor.hexRGB(0x000000, 0.0)
            
            self.layoutIfNeeded()
            
        }, completion: { (finished) in
            
            self.removeFromSuperview()
            
        })
    }
    
    @IBAction private func onSureButtonAction(_ sender: Any) {
        
        completionHandler(currentDate())
        
        dismiss()
    }
}

/*
extension DatePickerView {
    public struct DateStyle: OptionSet {
        let rawValue: Int
        
        static let second   = DateStyle(rawValue: 1 << 0)
        static let minute   = DateStyle(rawValue: 1 << 1)
        static let hour     = DateStyle(rawValue: 1 << 2)
        static let day      = DateStyle(rawValue: 1 << 3)
        static let month    = DateStyle(rawValue: 1 << 4)
        static let year     = DateStyle(rawValue: 1 << 5)
        
        static let all: DateStyle = [.year, .month, .day, .hour, .minute, .second]
    }
}
 */

extension DatePickerView {
    private func setupUI() {

        bottomViewBottom.constant = bottomView.frame.size.height
        
        backgroundColor = UIColor.hexRGB(0x000000, 0.0)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(dismiss))
        tap.delegate = self
        addGestureRecognizer(tap)
        
        pickerView.delegate = self
        pickerView.dataSource = self
    }
    
    private func setupData() {
        componentsArray = [Dictionary]()
        
        switch style {
            
        case .all:
            
            initDataWithAllStyle()
            
        case .yearMonthDay:
            
            initDataWithYearMonthDayStyle()
            
        case .hourMinuteSecond:
            
            initDataWithHourMinuteSecondStyle()
            
        case .yearMonthDayHourMinute:
            
            initDataWithYearMonthDayHourMinuteStyle()

        }
    }
    
    private func currentDate() -> Date? {
        var result = [Int]()
        
        for index in 0..<pickerView.numberOfComponents {
            result.append(componentsArray[index].values.first![pickerView.selectedRow(inComponent: index)])
        }
        
        switch style {
            
        case .all:
            
            guard result.count == 6 else { return nil }
            
            let dateStr = String(result[0]) + "-" + String(result[1]) + "-" + String(result[2]) + " " +
                String(result[3]) + ":" + String(result[4]) + ":" + String(result[5])
            
            return Date.date(dateStr, formatter: "yyyy-MM-dd HH:mm:ss")
            
        case .yearMonthDay:
            
            guard result.count == 3 else { return nil }
            
            let dateStr = String(result[0]) + "-" + String(result[1]) + "-" + String(result[2])
            
            return Date.date(dateStr, formatter: "yyyy-MM-dd")
            
        case .hourMinuteSecond:
            
            guard result.count == 3 else { return nil }
            
            let dateStr = String(result[0]) + ":" + String(result[1]) + ":" + String(result[2])
            
            return Date.date(dateStr, formatter: "HH:mm:ss")
            
        case .yearMonthDayHourMinute:
            
            guard result.count == 5 else { return nil }
            
            let dateStr = String(result[0]) + "-" + String(result[1]) + "-" + String(result[2]) + " " +
                String(result[3]) + ":" + String(result[4])
            
            return Date.date(dateStr, formatter: "yyyy-MM-dd HH:mm")
            
        }
        
    }
    
    /*
     
    class func dateStyleIsValid(_ style: DateStyle) -> Bool {
        // 传进来的时间单位必须是连续的
        let array = String(style.rawValue, radix: 2, uppercase: false).compactMap({Int(String($0))})

        guard let index = array.index(where: { $0 == 0 }) else { return true }
        
        if array.count == 1 || array[index..<array.count].contains(1) {
            return false
        }
        
        return true
    }
 */
    
}

// MARK: - scroll to date
extension DatePickerView {
    private func scrollTo(_ date: Date) {
        
        var scrollToDate = date
        
        if scrollToDate > maxLimitDate {
            scrollToDate = maxLimitDate
        }
        
        if scrollToDate < minLimitDate {
            scrollToDate = minLimitDate
        }
        
        switch style {
            
        case .all:
            
            allStyleScrollTo(scrollToDate)
            
        case .yearMonthDay:
            
            yearMonthDayStyleScrollTo(scrollToDate)
            
        case .hourMinuteSecond:
            
            hourMinuteSecondStyleScrollTo(scrollToDate)
            
        case .yearMonthDayHourMinute:
            
            yearMonthDayHourMinuteStyleScrollTo(scrollToDate)
            
        }

    }
    
    private func allStyleScrollTo(_ date: Date) {
        
        guard componentsArray.count == 6 else {
            return
        }
        
        if let yearIndex = componentsArray[0][yearKey]?.index(of: date.year) {
            pickerView.selectRow(yearIndex, inComponent: 0, animated: true)
        }
        
        if let monthIndex = componentsArray[1][monthKey]?.index(of: date.month) {
            pickerView.selectRow(monthIndex, inComponent: 1, animated: true)
        }
        
        if let dayIndex = componentsArray[2][dayKey]?.index(of: date.day) {
            pickerView.selectRow(dayIndex, inComponent: 2, animated: true)
        }
        
        if let hourIndex = componentsArray[3][hourKey]?.index(of: date.hour) {
            pickerView.selectRow(hourIndex, inComponent: 3, animated: true)
        }
        
        if let minuteIndex = componentsArray[4][minuteKey]?.index(of: date.minute) {
            pickerView.selectRow(minuteIndex, inComponent: 4, animated: true)
        }
        
        if let secondIndex = componentsArray[5][secondKey]?.index(of: date.second) {
            pickerView.selectRow(secondIndex, inComponent: 5, animated: true)
        }
    }
    
    private func yearMonthDayStyleScrollTo(_ date: Date) {
        
        guard componentsArray.count == 3 else {
            return
        }
        
        if let yearIndex = componentsArray[0][yearKey]?.index(of: date.year) {
            pickerView.selectRow(yearIndex, inComponent: 0, animated: true)
        }
        
        if let monthIndex = componentsArray[1][monthKey]?.index(of: date.month) {
            pickerView.selectRow(monthIndex, inComponent: 1, animated: true)
        }
        
        if let dayIndex = componentsArray[2][dayKey]?.index(of: date.day) {
            pickerView.selectRow(dayIndex, inComponent: 2, animated: true)
        }
    }
    
    private func hourMinuteSecondStyleScrollTo(_ date: Date) {
        
        guard componentsArray.count == 3 else {
            return
        }
        
        if let hourIndex = componentsArray[0][hourKey]?.index(of: date.hour) {
            pickerView.selectRow(hourIndex, inComponent: 0, animated: true)
        }
        
        if let minuteIndex = componentsArray[1][minuteKey]?.index(of: date.minute) {
            pickerView.selectRow(minuteIndex, inComponent: 1, animated: true)
        }
        
        if let secondIndex = componentsArray[2][secondKey]?.index(of: date.second) {
            pickerView.selectRow(secondIndex, inComponent: 2, animated: true)
        }
    }
    
    private func yearMonthDayHourMinuteStyleScrollTo(_ date: Date) {
        
        guard componentsArray.count == 5 else {
            return
        }
        
        if let yearIndex = componentsArray[0][yearKey]?.index(of: date.year) {
            pickerView.selectRow(yearIndex, inComponent: 0, animated: true)
        }
        
        if let monthIndex = componentsArray[1][monthKey]?.index(of: date.month) {
            pickerView.selectRow(monthIndex, inComponent: 1, animated: true)
        }
        
        if let dayIndex = componentsArray[2][dayKey]?.index(of: date.day) {
            pickerView.selectRow(dayIndex, inComponent: 2, animated: true)
        }
        
        if let hourIndex = componentsArray[3][hourKey]?.index(of: date.hour) {
            pickerView.selectRow(hourIndex, inComponent: 3, animated: true)
        }
        
        if let minuteIndex = componentsArray[4][minuteKey]?.index(of: date.minute) {
            pickerView.selectRow(minuteIndex, inComponent: 4, animated: true)
        }

    }
}

// MARK: - init data
extension DatePickerView {
    private func initDataWithAllStyle() {
        componentsArray.append([yearKey : Array(minLimitDate.year...maxLimitDate.year)])
        
        if minLimitDate.haveSameYear(maxLimitDate) {
            componentsArray.append([monthKey : Array(minLimitDate.month...maxLimitDate.month)])
        } else {
            componentsArray.append([monthKey : Array(1...12)])
        }
        
        if minLimitDate.haveSameYearAndMonth(maxLimitDate) {
            componentsArray.append([dayKey : Array(minLimitDate.day...maxLimitDate.day)])
        } else {
            componentsArray.append([dayKey : Array(1...scrollToDate.numberOfDaysInMonth())])
        }
        
        if minLimitDate.haveSameYearMonthAndDay(maxLimitDate) {
            componentsArray.append([hourKey : Array(minLimitDate.day...maxLimitDate.day)])
        } else {
            componentsArray.append([hourKey : Array(0...23)])
        }
        
        if minLimitDate.haveSameYearMonthDayAndHour(maxLimitDate) {
            componentsArray.append([minuteKey : Array(minLimitDate.minute...maxLimitDate.minute)])
        } else {
            componentsArray.append([minuteKey : Array(0...59)])
        }
        
        if minLimitDate.haveSameYearMonthDayHourAndMinute(maxLimitDate) {
            componentsArray.append([secondKey : Array(minLimitDate.minute...maxLimitDate.minute)])
        } else {
            componentsArray.append([secondKey : Array(0...59)])
        }

    }
    
    private func initDataWithYearMonthDayStyle() {
        componentsArray.append([yearKey : Array(minLimitDate.year...maxLimitDate.year)])
        
        if minLimitDate.haveSameYear(maxLimitDate) {
            componentsArray.append([monthKey : Array(minLimitDate.month...maxLimitDate.month)])
        } else {
            componentsArray.append([monthKey : Array(1...12)])
        }
        
        if minLimitDate.haveSameYearAndMonth(maxLimitDate) {
            componentsArray.append([dayKey : Array(minLimitDate.day...maxLimitDate.day)])
        } else {
            componentsArray.append([dayKey : Array(1...scrollToDate.numberOfDaysInMonth())])
        }
    }
    
    private func initDataWithHourMinuteSecondStyle() {
        
        if let date = Date.date("00:00:00", formatter: "HH:mm:ss") {
            minLimitDate = date
        }
        
        if let date = Date.date("23:59:59", formatter: "HH:mm:ss") {
            maxLimitDate = date
        }
        
        componentsArray.append([hourKey : Array(minLimitDate.hour...maxLimitDate.hour)])

        if minLimitDate.haveSameYearMonthDayAndHour(maxLimitDate) {
            componentsArray.append([minuteKey : Array(minLimitDate.minute...maxLimitDate.minute)])
        } else {
            componentsArray.append([minuteKey : Array(0...59)])
        }
        
        if minLimitDate.haveSameYearMonthDayHourAndMinute(maxLimitDate) {
            componentsArray.append([secondKey : Array(minLimitDate.second...maxLimitDate.second)])
        } else {
            componentsArray.append([secondKey : Array(0...59)])
        }
    }
    
    private func initDataWithYearMonthDayHourMinuteStyle() {
        componentsArray.append([yearKey : Array(minLimitDate.year...maxLimitDate.year)])
        
        if minLimitDate.haveSameYear(maxLimitDate) {
            componentsArray.append([monthKey : Array(minLimitDate.month...maxLimitDate.month)])
        } else {
            componentsArray.append([monthKey : Array(1...12)])
        }
        
        if minLimitDate.haveSameYearAndMonth(maxLimitDate) {
            componentsArray.append([dayKey : Array(minLimitDate.day...maxLimitDate.day)])
        } else {
            componentsArray.append([dayKey : Array(1...scrollToDate.numberOfDaysInMonth())])
        }
        
        if minLimitDate.haveSameYearMonthAndDay(maxLimitDate) {
            componentsArray.append([hourKey : Array(minLimitDate.day...maxLimitDate.day)])
        } else {
            componentsArray.append([hourKey : Array(0...23)])
        }
        
        if minLimitDate.haveSameYearMonthDayAndHour(maxLimitDate) {
            componentsArray.append([minuteKey : Array(minLimitDate.minute...maxLimitDate.minute)])
        } else {
            componentsArray.append([minuteKey : Array(0...59)])
        }
    }
}

// MARK: - UIGestureRecognizerDelegate
extension DatePickerView {
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        guard let touchView = touch.view else { return false }
        
        if touchView.isDescendant(of: bottomView) {
            // 点击的view是否是bottomView或者bottomView的子视图
            return false
        }
        
        return true
    }
}

// MARK: - UIPickerViewDelegate, UIPickerViewDataSource
extension DatePickerView {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return componentsArray.count
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if let array = componentsArray[component].values.first {
            return array.count
        }
        return 0
    }
    
    func pickerView(_ pickerView: UIPickerView, rowHeightForComponent component: Int) -> CGFloat {
        return 40
    }
    
    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        
        var title: String = " "
        let dic = componentsArray[component]
        if let key = dic.keys.first {
            let data = dic[key]
            title = String(data![row]) + key
        }
        
        if let label = view as? UILabel {
            label.text = title
            return label
        }
        
        let label = UILabel()
        label.textAlignment = .center
        label.font = UIFont.systemFont(ofSize: 17)
        label.text = title
        label.sizeToFit()
        return label
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        // 月分变化了，天数要跟着变化.年份变化了，如果是2月,天数也可能变化
        
        func scrollToValidTimeRange() {
            // 检查当前时间是否在最大和最小时间之前
            if let currentDate = currentDate() {
                
                if currentDate > maxLimitDate {
                    // 超过最大值，则滚回最大值
                    scrollTo(maxLimitDate)
                    
                } else if currentDate < minLimitDate {
                    // 小于最小值，刚滚回最小值
                    scrollTo(minLimitDate)
                }
            }
        }
        
        guard let key = componentsArray[component].keys.first, key == yearKey || key == monthKey else {
            // 只是滚动年或者月时，才需要处理天数的变化
            scrollToValidTimeRange()
            
            return
        }
        
        let yearIndex = pickerView.selectedRow(inComponent: 0)
        if yearIndex < 0 {
            scrollToValidTimeRange()
            
            return
        }
        
        if key == yearKey {
            // 更新年份水印
            let year = componentsArray[0][yearKey]![yearIndex]      // 年份
            
            yearLabel.text = String(year)
        }
        
        if key == yearKey && pickerView.selectedRow(inComponent: component + 1) != 1 {
            // 滚动年，月分不是2月时，不需要处理天数变化
            scrollToValidTimeRange()
            
            return
        }
        
        
        let monthIndex = pickerView.selectedRow(inComponent: 1)
        if monthIndex < 0 {
            
            scrollToValidTimeRange()
            
            return
        }
        
        let year = componentsArray[0][yearKey]![yearIndex]      // 年份
        let month = componentsArray[1][monthKey]![monthIndex]   // 月份
        
        guard let date = Date.date(String(year) + "-" + String(month), formatter: "yyyy-MM") else {
            scrollToValidTimeRange()
            
            return
            
        }

        let numberOfDays = date.numberOfDaysInMonth() // 当前月的天数
        
        if numberOfDays != 0 && numberOfDays == componentsArray[2][dayKey]!.count {
            // 天数没有变化，不需要任何操作
            scrollToValidTimeRange()
            
            return
        }
        
        let newDayDic = [dayKey : Array(1...numberOfDays)] // 新的天数数据

        componentsArray.replaceSubrange(2...2, with: [newDayDic])
        
        pickerView.reloadComponent(2)
        
        scrollToValidTimeRange()
    }
}
