//
//  OOMeetingRoomTableHeaderView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/18.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit


protocol OOMeetingRoomTableHeaderViewDelegate {
    //选择了指定的日期
    func setTheDate(_ startDate:String,_ endDate:String)
}

class OOMeetingRoomTableHeaderView: UIView {
    
    var delegate:OOMeetingRoomTableHeaderViewDelegate?
    
    @IBOutlet weak var selectedDateLabel: UILabel!
    
    @IBOutlet weak var theDateField: UITextField!
    
    @IBOutlet weak var theTimeField: UITextField!
    
    
    
    var startDate:String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd hh:mm"
        return formatter.string(from: currentDate)
    }
    
    var completedDate:String {
        let endDate = Calendar.current.dateComponents([.hour,.minute], from: currentTime)
        var dateComp = DateComponents()
        dateComp.hour = endDate.hour
        dateComp.minute = endDate.minute
        let eDate = Calendar.current.date(byAdding: dateComp, to: currentDate)
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd hh:mm"
        return formatter.string(from: eDate!)
        
    }
    
    var setDelegate:OOMeetingRoomTableHeaderViewDelegate?
    
    private var currentDate = Date()
    
    private var currentTime = Date()
    
    private let formatter = DateFormatter()
    
    private let dateFormat = "yyyy年MM月dd日hh时mm分"
    
    private let timeFormat = "hh时mm分"
    
    
    
    override func awakeFromNib() {
        formatter.dateFormat = dateFormat
        theDateField.text = formatter.string(from: currentDate)
        formatter.dateFormat = timeFormat
        currentTime = Calendar.current.date(bySettingHour: 1, minute: 0, second: 0, of: currentTime)!
        theTimeField.text = formatter.string(from: currentTime)
        theDateField.delegate = self
        theTimeField.delegate = self
    }
    
    func callbackDelegate(){
        guard  let block = setDelegate else {
            return
        }
        block.setTheDate(startDate, completedDate)
    }
    
    func datePickerTapped(_ title:String,_ dateType:UIDatePicker.Mode,_ format:String,_ textField:UITextField) {
        let locale = Locale(identifier: "zh")
        var theDate = currentDate
        if textField == theTimeField {
            theDate = currentTime
        }
        var dateComponents = DateComponents()
        dateComponents.month = -12
        let threeMonthAgo = Calendar.current.date(byAdding: dateComponents, to: theDate)
        dateComponents.month = 12
        let nextYearMonthAgo = Calendar.current.date(byAdding: dateComponents, to: theDate)
        
        let datePicker = LWDatePickerDialog(textColor: .red,
                                            buttonColor: .red,
                                            font: UIFont.boldSystemFont(ofSize: 17),
                                            locale:locale ,
                                            showCancelButton: true)
        datePicker.show(title,
                        doneButtonTitle: "确定",
                        cancelButtonTitle: "取消",
                        defaultDate: theDate,
                        minimumDate: threeMonthAgo,
                        maximumDate: nextYearMonthAgo,
                        datePickerMode: dateType) { (date) in
                            if let dt = date {
                                let formatter = DateFormatter()
                                formatter.dateFormat = format
                                textField.text = formatter.string(from: dt)
                                if textField == self.theDateField {
                                    self.currentDate = dt
                                }else{
                                    self.currentTime = dt
                                }
                                self.callbackDelegate()
                            }
        }
    }
}


extension OOMeetingRoomTableHeaderView:UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        
        if textField == theDateField {
            self.datePickerTapped("选择日期", .dateAndTime, dateFormat, textField)
            return false
        }else if textField == theTimeField {
            self.datePickerTapped("选择持续时间", .countDownTimer, timeFormat, textField)
            return false
        }
        return true
    }
}
