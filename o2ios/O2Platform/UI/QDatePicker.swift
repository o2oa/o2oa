//
//  QDatePicker.swift
//  O2Platform
//  来自GitHub上的https://github.com/qyfeng009/QTimePicker
//
//  Created by FancyLou on 2019/5/7.
//  Copyright © 2019 zoneland. All rights reserved.
//


import UIKit

private let screenWidth = UIScreen.main.bounds.width
private let screenHeight = UIScreen.main.bounds.height
private let keyWindow = UIApplication.shared.keyWindow
private let yearLH: CGFloat = 226.0
private let sureVH: CGFloat = 44.0
private let margin: CGFloat = 10.0
class QDatePicker: UIView, UIGestureRecognizerDelegate, UIPickerViewDelegate, UIPickerViewDataSource {
    enum QPickerStyle {
        case datePicker // 时间选择器
        case singlePicker // 单项选择器
    }
    enum DatePickerStyle { // 此只在 datePicker 下生效
        case YMDHM // 选择 年月日时分
        case YMD   // 选择 年月日
        case MDHM  // 选择 月日时分
        case MD    // 选择 月日
        case HM    // 选择 时分
    }
    enum AminationStyle {
        case styleDefault // 默认弹出样式
        case styleOptional // 可选弹出样式
    }
    
    typealias DidSelectedDate = (_ date: String) -> Void ///< 定义确认回调
    private var selectedBack: DidSelectedDate?
    var sureView: UIButton!
    var yearL: UILabel!
    open var pickerStyle: QPickerStyle = .datePicker ///< 选择样式 默认 datePicker
    open var datePickerStyle: DatePickerStyle = .MDHM ///< 日期选择样式 默认 MDHM，只在 datePicker 样式下
    open var animationStyle: AminationStyle = .styleDefault { ///< 弹出动画样式 默认 styleDefault
        didSet {
            if animationStyle == .styleOptional {
                yearL.y = -yearLH
                yearL.transform = CGAffineTransform.identity
                sureView.y = screenHeight
                sureView.transform = CGAffineTransform.identity
            }
        }
    }
    open var themeColor: UIColor? = UIColor.hexInt(0x00B3C4) { ///< 主题颜色样式
        didSet {
            sureView.backgroundColor = themeColor
        }
    }
    open var singlePickerDatas: [String] = [] {
        didSet {
            singleSelectedData = singlePickerDatas[0]
        }
    } // 单项选择器数据源
    private var singleSelectedData: String = ""
    
    private lazy var pickerView: UIPickerView = {
        let pickerView = UIPickerView(frame: CGRect(x: 0, y: 0, width: yearL.width, height: yearL.height))
        pickerView.backgroundColor = .clear
        pickerView.showsSelectionIndicator = true
        pickerView.delegate = self
        pickerView.dataSource = self
        return pickerView
    }()
    
    init(selectedDate: @escaping DidSelectedDate) {
        super.init(frame: UIScreen.main.bounds)
        selectedBack = selectedDate
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(close))
        tapGesture.delegate = self
        addGestureRecognizer(tapGesture)
        backgroundColor = .clear
        
        yearL = UILabel(frame: CGRect(x: margin, y: screenHeight, width: screenWidth - margin * 2, height: yearLH))
        yearL.transform = CGAffineTransform.identity
        yearL.isUserInteractionEnabled = true
        yearL.backgroundColor = .white
        yearL.textColor = UIColor.hexInt(0xE9ECF2)
        yearL.textAlignment = NSTextAlignment.center
        yearL.font = UIFont.systemFont(ofSize: 110)
        yearL.adjustsFontSizeToFitWidth = true
        addSubview(yearL)
        yearL.roundedCorners(cornerRadius: 10, rectCorner: UIRectCorner([.topLeft, .topRight]))
        
        sureView = UIButton(type: UIButton.ButtonType.system)
        sureView.frame = CGRect(x: margin, y: yearL.y + yearL.height, width: yearL.width, height: sureVH)
        sureView.transform = CGAffineTransform.identity
        sureView.setTitle("确认", for: UIControl.State.normal)
        sureView.setTitleColor(.white, for: UIControl.State.normal)
        sureView.backgroundColor = themeColor
        sureView.addTarget(self, action: #selector(sureDate(_:)), for: UIControl.Event.touchUpInside)
        addSubview(sureView)
        sureView.roundedCorners(cornerRadius: 10, rectCorner: UIRectCorner([.bottomLeft, .bottomRight]))
        
        if pickerStyle == .datePicker {
            self.initDefaultDate()
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    /// 弹出 Picker datePicker类型 可以定义默认显示的时间
    open func showDatePicker(defaultDate: Date = Date()) {
        if self.pickerStyle != .datePicker {
            self.show()
            return
        }
        
        keyWindow?.addSubview(self)
        keyWindow?.bringSubviewToFront(self)
        if animationStyle == .styleDefault {
            var duraton = 0.8
            var y = -(margin + yearLH + sureVH)
            if screenHeight == 812 {
                y -= 34
                duraton = 1.0
            }
            UIView.animate(withDuration: duraton, delay: 0, usingSpringWithDamping: 0.6, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.backgroundColor = UIColor.black.withAlphaComponent(0.4)
                self.sureView.transform = CGAffineTransform(translationX: 0, y: y)
            })
            UIView.animate(withDuration: duraton, delay: 0, usingSpringWithDamping: 0.4, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.yearL.transform = CGAffineTransform(translationX: 0, y:  y)
            }, completion: { (finish: Bool) in
                self.yearL.addSubview(self.pickerView)
                self.resetDateShow(date: defaultDate)
            })
        } else {
            UIView.animate(withDuration: 0.8, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.backgroundColor = UIColor.black.withAlphaComponent(0.4)
                self.sureView.transform = CGAffineTransform(translationX: 0, y: -sureVH-(screenHeight/2 - (yearLH + sureVH)/2))
            })
            UIView.animate(withDuration: 0.8, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.yearL.transform = CGAffineTransform(translationX: 0, y: (yearLH + screenHeight/2-(yearLH + sureVH)/2))
            }, completion: { (finish: Bool) in
                self.yearL.addSubview(self.pickerView)
                self.resetDateShow(date: defaultDate)
            })
        }
    }
    /// 弹出 Picker
    open func show() {
        keyWindow?.addSubview(self)
        keyWindow?.bringSubviewToFront(self)
        if animationStyle == .styleDefault {
            var duraton = 0.8
            var y = -(margin + yearLH + sureVH)
            if screenHeight == 812 {
                y -= 34
                duraton = 1.0
            }
            UIView.animate(withDuration: duraton, delay: 0, usingSpringWithDamping: 0.6, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.backgroundColor = UIColor.black.withAlphaComponent(0.4)
                self.sureView.transform = CGAffineTransform(translationX: 0, y: y)
            })
            UIView.animate(withDuration: duraton, delay: 0, usingSpringWithDamping: 0.4, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.yearL.transform = CGAffineTransform(translationX: 0, y:  y)
            }, completion: { (finish: Bool) in
                self.yearL.addSubview(self.pickerView)
                if self.pickerStyle == .datePicker {
                    self.resetDateShow(date: Date())
                }
            })
        } else {
            UIView.animate(withDuration: 0.8, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.backgroundColor = UIColor.black.withAlphaComponent(0.4)
                self.sureView.transform = CGAffineTransform(translationX: 0, y: -sureVH-(screenHeight/2 - (yearLH + sureVH)/2))
            })
            UIView.animate(withDuration: 0.8, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 0, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.yearL.transform = CGAffineTransform(translationX: 0, y: (yearLH + screenHeight/2-(yearLH + sureVH)/2))
            }, completion: { (finish: Bool) in
                self.yearL.addSubview(self.pickerView)
                if self.pickerStyle == .datePicker {
                    self.resetDateShow(date: Date())
                }
            })
        }
    }
    @objc private func close() {
        if animationStyle == .styleDefault {
            let ani = CAKeyframeAnimation(keyPath: "transform.translation.y")
            let currentTy = yearL.transform.ty
            ani.duration = 0.3
            ani.values = [currentTy, currentTy - 20, currentTy]
            ani.keyTimes = [0, 0.2, 0.1]
            ani.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
            yearL.layer.add(ani, forKey: "kViewShakerAnimationKey")
            var duraton = 0.21
            var y = -(margin + yearLH + sureVH) + 20
            if screenHeight == 812 {
                y -= 34
                duraton = 0.27
            }
            UIView.animate(withDuration: 0.3, delay: 0.3, usingSpringWithDamping: 0.3, initialSpringVelocity: 21, options: UIView.AnimationOptions([.curveEaseInOut, .beginFromCurrentState, .layoutSubviews]), animations: {
                self.yearL.transform = CGAffineTransform(translationX: 0, y: y)
            })
            UIView.animate(withDuration: duraton, delay: 0.36, animations: {
                self.sureView.transform  = CGAffineTransform.identity
                self.backgroundColor = .clear
            }) { (finish: Bool) in
                self.removeFromSuperview()
            }
        } else {
            UIView.animate(withDuration: 0.21, animations: {
                self.yearL.transform  = CGAffineTransform.identity
                self.sureView.transform  = CGAffineTransform.identity
                self.backgroundColor = .clear
            }, completion: { (finish: Bool) in
                self.removeFromSuperview()
            })
        }
    }
    
    @objc private func sureDate(_ button: UIButton) {
        var string = ""
        if pickerStyle == .datePicker {
            switch datePickerStyle {
            case .YMDHM:
                string = intentDate.formatterDate(formatter: "yyyy-MM-dd HH:mm")
            case .YMD:
                string = intentDate.formatterDate(formatter: "yyyy-MM-dd")
            case .MDHM:
                string = intentDate.formatterDate(formatter: "yyyy-MM-dd HH:mm")
            case .MD:
                string = intentDate.formatterDate(formatter: "yyyy-MM-dd")
            case .HM:
                string = intentDate.formatterDate(formatter: "yyyy-MM-dd HH:mm")
            }
        }
        if pickerStyle == .singlePicker {
            string = singleSelectedData
        }
        selectedBack?(string)
        close()
    }
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if touch.view != self {
            return false
        }
        return true
    }
    
    
    // MARK: - 配置滚轴
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        var num = 0
        if pickerStyle == .datePicker {
            switch datePickerStyle {
            case .YMDHM:
                num = 5
            case .YMD:
                num = 3
            case .MDHM:
                num = 4
            case .MD:
                num = 2
            case .HM:
                num = 2
            }
        }
        if pickerStyle == .singlePicker {
            num = 1
        }
        return num
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerStyle == .datePicker {
            return getNumberOfRowsInComponent()[component]
        } else {
            return singlePickerDatas.count
        }
    }
    func pickerView(_ pickerView: UIPickerView, rowHeightForComponent component: Int) -> CGFloat {
        return 44
    }
    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        let label = UILabel()
        label.textAlignment = NSTextAlignment.center
        var title = ""
        if pickerStyle == .datePicker {
            switch datePickerStyle {
            case .YMDHM:
                addLabel(tags: ["年", "月", "日", "时", "分"])
                if component == 0 {
                    title = String(format: "%d", yearArray[row])
                }
                if component == 1 {
                    title = String(format: "%02d", monthArray[row])
                }
                if component == 2 {
                    title = String(format: "%02d", dayArray[row])
                }
                if component == 3 {
                    title = String(format: "%02d", hourArray[row])
                }
                if component == 4 {
                    title = String(format: "%02d", minuteArray[row])
                }
            case .YMD:
                addLabel(tags: ["年", "月", "日"])
                if component == 0 {
                    title = String(format: "%d", yearArray[row])
                }
                if component == 1 {
                    title = String(format: "%02d", monthArray[row])
                }
                if component == 2 {
                    title = String(format: "%02d", dayArray[row])
                }
            case .MDHM:
                addLabel(tags: ["月", "日", "时", "分"])
                if component == 0 {
                    title = String(format: "%02d", monthArray[row%12])
                }
                if component == 1 {
                    title = String(format: "%02d", dayArray[row])
                }
                if component == 2 {
                    title = String(format: "%02d", hourArray[row])
                }
                if component == 3 {
                    title = String(format: "%02d", minuteArray[row])
                }
            case .MD:
                addLabel(tags: ["月", "日"])
                if component == 0 {
                    title = String(format: "%02d", monthArray[row%12])
                }
                if component == 1 {
                    title = String(format: "%02d", dayArray[row])
                }
            case .HM:
                addLabel(tags: ["时", "分"])
                if component == 0 {
                    title = String(format: "%02d", hourArray[row])
                }
                if component == 1 {
                    title = String(format: "%02d", minuteArray[row])
                }
            }
        }
        if pickerStyle == .singlePicker {
            title = singlePickerDatas[row]
            let view = UIView(frame: CGRect(x: 10, y: yearL.height/2 + 10, width: pickerView.width - 20, height: 0.5))
            view.backgroundColor = themeColor
            yearL.addSubview(view)
        }
        label.text = title
        return label
    }
    func addLabel(tags: [String]) {
        for subView in yearL.subviews {
            if subView is UILabel {
                subView.removeFromSuperview()
            }
        }
        let cellW: CGFloat = pickerView.width/CGFloat(tags.count)
        for i in 0..<tags.count {
            let labelX: CGFloat = cellW/4*3 + cellW*CGFloat(i)
            creatTagLabel(name: tags[i], x: labelX)
        }
    }
    func creatTagLabel(name: String, x: CGFloat) {
        let label = UILabel(frame: CGRect(x: x, y: yearL.height/2-15/2, width: 15, height: 15))
        label.text = name
        label.textAlignment = NSTextAlignment.center
        label.font = UIFont.systemFont(ofSize: 14)
        label.textColor = themeColor
        label.backgroundColor = .clear
        yearL.addSubview(label)
    }
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerStyle == .datePicker {
            switch datePickerStyle {
            case .YMDHM:
                if component == 0 {
                    yearIndex = row
                    yearL.text = "\(yearArray[yearIndex])"
                }
                if component == 1 {
                    monthIndex = row
                }
                if component == 2 {
                    dayIndex = row
                }
                if component == 3 {
                    hourIndex = row
                }
                if component == 4 {
                    minuteIndex = row
                }
                if component == 0 || component == 1 {
                    dayArray = daysInTheDate(year: yearArray[yearIndex], month: monthArray[monthIndex])
                    if dayArray.count - 1 < dayIndex {
                        dayIndex = dayArray.count - 1
                    }
                }
            case .YMD:
                if component == 0 {
                    yearIndex = row
                    yearL.text = "\(yearArray[yearIndex])"
                }
                if component == 1 {
                    monthIndex = row
                }
                if component == 2 {
                    dayIndex = row
                }
                if component == 0 || component == 1 {
                    dayArray = daysInTheDate(year: yearArray[yearIndex], month: monthArray[monthIndex])
                    if dayArray.count - 1 < dayIndex {
                        dayIndex = dayArray.count - 1
                    }
                }
            case .MDHM:
                if component == 1 {
                    dayIndex = row
                }
                if component == 2 {
                    hourIndex = row
                }
                if component == 3 {
                    minuteIndex = row
                }
                if component == 0 {
                    yearChange(row: row)
                    dayArray = daysInTheDate(year: yearArray[yearIndex], month: monthArray[monthIndex])
                    if dayArray.count - 1 < dayIndex {
                        dayIndex = dayArray.count - 1
                    }
                }
            case .MD:
                if component == 1 {
                    dayIndex = row
                }
                if component == 0 {
                    yearChange(row: row)
                    dayArray = daysInTheDate(year: yearArray[yearIndex], month: monthArray[monthIndex])
                    if dayArray.count - 1 < dayIndex {
                        dayIndex = dayArray.count - 1
                    }
                }
            case .HM:
                if component == 0 {
                    hourIndex = row
                }
                if component == 1 {
                    minuteIndex = row
                }
            }
            
            pickerView.reloadAllComponents()
            let dateString = String(format: "%d-%d-%d %d:%d", yearArray[yearIndex], monthArray[monthIndex], dayArray[dayIndex], hourArray[hourIndex], minuteArray[minuteIndex])
            intentDate = dateString.toDate(formatter: "yyyy-MM-dd HH:mm")
            
            if intentDate.compare(minLimitDate!) == ComparisonResult.orderedAscending {
                intentDate = minLimitDate!
                resetDateShow(date: minLimitDate!)
            }
            if intentDate.compare(maxLimitDate!) == ComparisonResult.orderedDescending {
                intentDate = maxLimitDate!
                resetDateShow(date: maxLimitDate!)
            }
        }
        if pickerStyle == .singlePicker {
            singleSelectedData = singlePickerDatas[row]
        }
    }
    func yearChange(row: NSInteger) {
        monthIndex = row%12
        if (row - supMonthIndex) < 12 && (row - supMonthIndex) > 0
            && monthArray[monthIndex] < monthArray[supMonthIndex%12] {
            yearIndex += 1
        } else if (supMonthIndex - row) < 12 && (supMonthIndex - row) > 0
            && monthArray[monthIndex] > monthArray[supMonthIndex%12] {
            yearIndex -= 1
        } else {
            let interval = (row - supMonthIndex)/12
            yearIndex += interval
        }
        yearL.text = "\(yearArray[yearIndex])"
        supMonthIndex = row
    }
    // MARK: - 重置 Date 数据
    func resetDateShow(date: Date) {
        yearIndex = date.year - minYear
        monthIndex = date.month - 1
        dayIndex = date.day - 1
        hourIndex = date.hour
        minuteIndex = date.minute
        
        supMonthIndex = (intentDate.year - minYear)*12 + (intentDate.month - 1)
        
        var indexArray = [Int]()
        if datePickerStyle == .YMDHM {
            indexArray = [yearIndex, monthIndex, dayIndex, hourIndex, minuteIndex]
        }
        if datePickerStyle == .YMD {
            indexArray = [yearIndex, monthIndex, dayIndex]
        }
        if datePickerStyle == .MDHM {
            indexArray = [monthIndex, dayIndex, hourIndex, minuteIndex]
        }
        if datePickerStyle == .MD {
            indexArray = [monthIndex, dayIndex]
        }
        if datePickerStyle == .HM {
            indexArray = [hourIndex, minuteIndex]
        }
        if datePickerStyle == .HM {
            yearL.text = "\(Date().year)" + " " + "\(Date().month)" + " " + "\(Date().day)"
        } else {
            yearL.text = "\(yearArray[yearIndex])"
        }
        
        for i in 0..<indexArray.count {
            if (datePickerStyle == .MDHM || datePickerStyle == .MD) && i == 0 {
                
                pickerView.selectRow(supMonthIndex, inComponent: i, animated: true)
                pickerView(pickerView, didSelectRow: supMonthIndex, inComponent: i)
            } else {
                pickerView.selectRow(indexArray[i], inComponent: i, animated: true)
                pickerView(pickerView, didSelectRow: indexArray[i], inComponent: i)
            }
        }
    }
    
    // MARK: - 日期数据处理
    var maxLimitDate: Date?
    var minLimitDate: Date?
    let maxYear = Date().year + 60
    let minYear = Date().year - 60
    var intentDate = Date() // 设置要滚动到的意向日期,如默认日期、限制日期
    var yearArray = [Int]()
    var monthArray = [Int]()
    var dayArray = [Int]()
    var hourArray = [Int]()
    var minuteArray = [Int]()
    
    var yearIndex = 0
    var monthIndex = 0
    var dayIndex = 0
    var hourIndex = 0
    var minuteIndex = 0
    var supMonthIndex = 0 // 记录当前月所在所有年月中的位置
    func initDefaultDate() {
        for i in 0..<60 {
            if i > 0 && i <= 12 {
                monthArray.append(i)
            }
            if i < 24 {
                hourArray.append(i)
            }
            minuteArray.append(i)
        }
        for i in minYear..<maxYear {
            yearArray.append(i)
        }
        let caleddar = Calendar(identifier: Calendar.Identifier.gregorian)
        let component = Set<Calendar.Component>([.year, .month, .day, .hour, .minute])
        let comps = caleddar.dateComponents(component, from: Date())
        let year = comps.year
        if maxLimitDate == nil {
            maxLimitDate = String(format: "%ld-12-31 23:59", year! + 10).toDate(formatter: "yyyy-MM-dd HH:mm")
        }
        if minLimitDate == nil {
            minLimitDate = String(format: "%ld-01-01 00:00", year! - 10).toDate(formatter: "yyyy-MM-dd HH:mm")
        }
    }
    
    func getNumberOfRowsInComponent() -> [NSInteger] {
        dayArray = daysInTheDate(year: yearArray[yearIndex], month: monthArray[monthIndex])
        let timeInterval = maxYear - minYear
        var rows = [NSInteger]()
        switch datePickerStyle {
        case .YMDHM:
            rows = [yearArray.count, monthArray.count, dayArray.count, hourArray.count, minuteArray.count]
        case .YMD:
            rows = [yearArray.count, monthArray.count, dayArray.count]
        case .MDHM:
            rows = [monthArray.count*timeInterval, dayArray.count, hourArray.count, minuteArray.count]
        case .MD:
            rows = [monthArray.count*timeInterval, dayArray.count]
        case .HM:
            rows = [hourArray.count, minuteArray.count]
        }
        return rows
    }
    func daysInTheDate(year: NSInteger, month: NSInteger) -> [Int] {
        let isLeapYear = year%4==0 ? (year%100==0 ? (year%400==0 ? true : false) : true) : false
        var days = 0
        switch month {
        case 1, 3, 5, 7, 8, 10, 12:
            days = 31
        case 4, 6, 9, 11:
            days = 30
        case 2:
            if isLeapYear {
                days = 29
            } else {
                days = 28
            }
        default:
            break
        }
        var array = [Int]()
        for i in 1...days {
            array.append(i)
        }
        return array
    }
}
