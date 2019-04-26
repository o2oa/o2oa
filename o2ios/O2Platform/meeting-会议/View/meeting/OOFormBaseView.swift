//
//  OOFormBaseView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/26.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit


protocol OOFormBaseUpdateViewProtocol {
    func updateViewModel(_ item:Any)
}

class OOFormBaseView: UIView,OOFormBaseUpdateViewProtocol {
   
    
    var model:OOFormBaseModel?
    
    private var theDate = Date()
    
    private let format = "yyyy年MM月dd日"
    
    func updateViewModel(_ item: Any) {
        
    }
    
    func datePickerTapped(_ title:String,_ dateType:UIDatePicker.Mode,_ format:String,_ textField:UITextField,callBackResult:((_ result:Date) -> Void)?) {
        let locale = Locale(identifier: "zh")
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
                                self.theDate = dt
                                let formatter = DateFormatter()
                                formatter.dateFormat = format
                                textField.text = formatter.string(from: dt)
                                callBackResult!(dt)
                            }
        }
    }
}
