//
//  OOMeetingTextItemView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/25.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOFormTextItemView: OOFormBaseView,OOFormConfigEnable {
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var valueTextField: UITextField!
    
    @IBOutlet weak var showValueLabel: UILabel!
    
  
    
    override func awakeFromNib() {
        showValueLabel.isHidden = true
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    func configItem(_ model: OOFormBaseModel) {
        self.model = model
        titleLabel.text = self.model?.titleName
        if self.model?.itemStatus! == .read {
            self.valueTextField.isHidden = true
            self.showValueLabel.isHidden = false
            self.showValueLabel.text = self.model?.callbackValue as! String
        }else{
            self.valueTextField.isHidden = false
            self.valueTextField.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
            self.showValueLabel.isHidden = true
        }
    }
    
}

// MARK:- 监听文本变化
extension OOFormTextItemView {
    @objc func textFieldDidChange(_ textField:UITextField) {
        self.model?.callbackValue = textField.text
    }
}
