//
//  OOFormDateItemView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/25.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit


class OOFormDateItemView: OOFormBaseView,OOFormConfigEnable {
    
    @IBOutlet weak var titleNameLabel: UILabel!
    
    @IBOutlet weak var valueTextField: UITextField!
    
    @IBOutlet weak var showValueLabel: UILabel!
    
    override func awakeFromNib() {
        showValueLabel.isHidden = true
        valueTextField.delegate = self
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    
    func configItem(_ model: OOFormBaseModel) {
        self.model = model
        titleNameLabel.text = self.model?.titleName
        if self.model?.itemStatus! == .read {
            showValueLabel.isHidden = false
            showValueLabel.text = (self.model?.callbackValue ) as? String
            valueTextField.isHidden = true
        }else{
            showValueLabel.isHidden = true
            showValueLabel.text = (self.model?.callbackValue ) as? String
            valueTextField.isHidden = false
        }
       
    }
    
}

extension OOFormDateItemView:UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        self.datePickerTapped("选择日期", .date, "yyyy年MM月dd日", textField) {
            theDate in
            self.model?.callbackValue = theDate
        }
        return false
    }
    
    
    
    
    
}


