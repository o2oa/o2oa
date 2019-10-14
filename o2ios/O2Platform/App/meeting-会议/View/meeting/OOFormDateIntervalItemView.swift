//
//  OOFormDateIntervalItemView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/26.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit


class OOFormDateIntervalItemView: OOFormBaseView,OOFormConfigEnable {
    
    @IBOutlet weak var showValueLabel: UILabel!
    
    @IBOutlet weak var titleNameLabel: UILabel!
    
    @IBOutlet weak var value1TextField: UITextField!
    
    @IBOutlet weak var value2TextField: UITextField!
    
    override func awakeFromNib() {
        showValueLabel.isHidden = true
        value1TextField.delegate = self
        value2TextField.delegate = self
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
            showValueLabel.text = (self.model?.callbackValue ?? "") as? String
            value1TextField.isHidden = true
            value2TextField.isHidden = true
        }else{
            showValueLabel.isHidden = true
            showValueLabel.text = (self.model?.callbackValue ?? "") as? String
            value1TextField.isHidden = false
            value2TextField.isHidden = false
        }
    }
    
}

extension OOFormDateIntervalItemView:UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        self.datePickerTapped("选择时间", .time, "HH时mm分", textField) {
            theDate in
            let uModel = self.model as? OOFormDateIntervalModel
            if self.value1TextField == textField {
                uModel?.value1 = theDate
            }else if self.value2TextField == textField {
                uModel?.value2 = theDate
            }
            
        }
        return false
    }
}

