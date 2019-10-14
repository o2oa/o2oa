//
//  OOMeetingInforHeaderView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/4.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import JTCalendar

protocol OOMeetingInforHeaderViewDelegate {
    func selectedTheMonth(_ theMonth:Date?)
    func selectedTheDay(_ theDay:Date?)
}

class OOMeetingInforHeaderView: UIView {
    
    @IBOutlet weak var calendarMenuView: JTCalendarMenuView!
    
    @IBOutlet weak var calendarContentView: JTHorizontalCalendarView!
    
    var calendarManager:JTCalendarManager?
    
    @IBOutlet weak var calendarContentViewHeight: NSLayoutConstraint!
    
    var delegate:OOMeetingInforHeaderViewDelegate?
    
    //public
    var eventsByDate:[String:[OOMeetingInfo]]?{
        didSet {
            calendarManager?.reload()
        }
    }
    
    private var _todayDate:Date?
    
    private var _minDate:Date?
    
    private var _maxDate:Date?
    
    private var _dateSelected:Date?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
         commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    private func commonInit(){
       self.bounds = CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 172)
    }
    
    override func awakeFromNib() {
        calendarManager =  JTCalendarManager(locale: Locale.init(identifier: "zh_CN"), andTimeZone: TimeZone.current)
        calendarManager?.settings.weekModeEnabled = true
        calendarManager?.delegate = self
        createMinAndMaxDate()
        calendarManager?.menuView = calendarMenuView
        calendarManager?.contentView = calendarContentView
        calendarManager?.setDate(_todayDate!)
    }
    
    @IBAction func configToday(_ sender: UIButton) {
        print("Today")
        calendarManager?.setDate(_todayDate!)
        calendarManager?.reload()
    }
    
    
    @IBAction func changeCalendarMode(_ sender: UIButton) {
        print("changeCalendarMode Tap")
        calendarManager?.settings.weekModeEnabled  = !(calendarManager?.settings.weekModeEnabled)!
        calendarManager?.reload()
        var containerNewHeight:CGFloat = 387.0
        var newHeight:CGFloat = 300.0
        //172
        if (calendarManager?.settings.weekModeEnabled)! {
            newHeight = 85.0
            containerNewHeight = 172.0
            sender.setImage(#imageLiteral(resourceName: "icon_arrow_down"), for: .normal)
        }else{
            sender.setImage(#imageLiteral(resourceName: "icon_arrow_up"), for: .normal)
        }
        
        UIView.transition(with: self, duration: 0.5, options: [], animations: {
            self.calendarContentViewHeight.constant = newHeight
            self.height = containerNewHeight
            self.superview?.layoutIfNeeded()
        }, completion:{
            result in
            if result {
                self.delegate?.selectedTheDay(self._dateSelected)
            }
        })
        
        
    }
    
    
    private func createMinAndMaxDate(){
        _todayDate = Date()
        _minDate = calendarManager?.dateHelper.add(to: _todayDate, months: -12)
        _maxDate = calendarManager?.dateHelper?.add(to: _todayDate, months: 12)
    }
    

}

extension OOMeetingInforHeaderView{
    
    // Used only to have a key for _eventsByDate
    func dateFormatter() -> DateFormatter {
        var dateFormatter: DateFormatter?
        if dateFormatter == nil {
            dateFormatter = DateFormatter()
            dateFormatter?.dateFormat = "yyyy-MM-dd"
        }
        return dateFormatter!
    }
    
    func haveEventForDay(_ date:Date) -> Bool{
        guard let dict = eventsByDate else {
            return false
        }
        let key =  self.dateFormatter().string(from: date)
        if dict[key] != nil && (dict[key]?.count)! > 0 {
            return true
        }
        return false
    }
}

extension OOMeetingInforHeaderView: JTCalendarDelegate {
    func calendar(_ calendar: JTCalendarManager, prepareMenuItemView menuItemView: UIView, date: Date) {
        var text: String? = nil
        var dateFormatter: DateFormatter? = nil
        if dateFormatter == nil {
            dateFormatter = calendar.dateHelper?.createDateFormatter()
        }
        dateFormatter?.dateFormat = "yyyy年MM月"
        text = dateFormatter?.string(from: date)
        (menuItemView as? UILabel)?.text = text
    }
    
    func calendar(_ calendar: JTCalendarManager!, prepareDayView dayView: (UIView & JTCalendarDay)!) {
        if dayView is JTCalendarDayView {
            let dView = dayView as! JTCalendarDayView
            // Today
            if calendar.dateHelper.date(Date(), isTheSameDayThan: dView.date) { // 今天
                dView.circleView?.isHidden = false
                dView.circleView?.backgroundColor = O2ThemeManager.color(for: "Base.base_color")?.alpha(0.5)
                dView.dotView?.backgroundColor = UIColor.white
                dView.textLabel?.textColor = UIColor.white
            }
            else if _dateSelected != nil && calendar.dateHelper.date(_dateSelected, isTheSameDayThan: dView.date) { // 选中的
                dView.circleView?.isHidden = false
                dView.circleView?.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
                dView.dotView?.backgroundColor = UIColor.white
                dView.textLabel?.textColor = UIColor.white
            }
            else if calendar.dateHelper.date(dView.date, isTheSameMonthThan: self.calendarContentView.date) { // 同一个月的
                dView.circleView?.isHidden = true
                dView.dotView?.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
                dView.textLabel?.textColor = UIColor(hex: "#666666")
            }
            else {
                dView.circleView?.isHidden = true
                dView.dotView?.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
                dView.textLabel?.textColor = UIColor(hex: "#CCCCCC")
            }
            
            if self.haveEventForDay(dView.date!){
                dView.dotView?.isHidden = false
            }
            else {
                dView.dotView?.isHidden = true
            }
        }
    }
    
    func calendar(_ calendar: JTCalendarManager!, didTouchDayView dayView: (UIView & JTCalendarDay)!) {
        if dayView is JTCalendarDayView {
            let dView = dayView as! JTCalendarDayView
            _dateSelected = dView.date
            // Animation for the circleView
            dView.circleView?.transform = CGAffineTransform.init(scaleX: 0.1, y: 0.1)
            UIView.transition(with: dView, duration: 0.3, options: [], animations: {
                dView.circleView?.transform = .identity
                self.calendarManager?.reload()
            }) { (compeleted) in
                
            }
            
            guard let block = delegate else {
                return
            }
            block.selectedTheDay(_dateSelected)
        }
    }
    
    func calendar(_ calendar: JTCalendarManager, canDisplayPageWith date: Date) -> Bool {
        return calendar.dateHelper.date(date, isEqualOrAfter: _minDate, andEqualOrBefore: _maxDate)
    }
    
    func calendarDidLoadPreviousPage(_ calendar: JTCalendarManager) {
        print("Previous Page loaded \(calendar.date().description)")
        guard let block = delegate else {
            return
        }
        block.selectedTheMonth(calendar.date())
    }
    
    func calendarDidLoadNextPage(_ calendar: JTCalendarManager) {
        print("Next Page loaded \(calendar.date().description)")
        guard let block = delegate else {
            return
        }
        block.selectedTheMonth(calendar.date())
    }
    
}
