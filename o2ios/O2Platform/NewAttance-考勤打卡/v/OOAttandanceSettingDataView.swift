//
//  OOAttandanceSettingDataView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/18.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttandanceSettingDataView: UIView {
    
    
    @IBOutlet weak var workPlaceNameTextField: UITextField!
    
    
    @IBOutlet weak var workAliasNameTextField: UITextField!
    
    
    @IBOutlet weak var checkErrorRangeTextField: UITextField!
    
    
    private lazy var closwBtn:UIToolbar = {
        let bar = UIToolbar.init(frame: CGRect(x: 0, y: 0, width: SCREEN_WIDTH, height: 44))
        
        let button = UIButton.init(frame: CGRect(x: 50, y: 7, width: 160, height: 30))
        button.setTitle("保存地点", for: .normal)
        button.setTitleColor(UIColor.white, for: .normal)
        button.setBackgroundColor(UIColor.hexInt(0xfb4747), forState: .normal)
        button.layer.cornerRadius = 5
        button.layer.masksToBounds = true
        button.addTarget(self, action: #selector(submitClicked(_:)), for: .touchUpInside)
        bar.addSubview(button)
        let button1 = UIButton.init(frame: CGRect(x: SCREEN_WIDTH -  60, y: 7, width: 50, height: 30))
        button1.setTitle("关闭", for: .normal)
        button1.setTitleColor(UIColor.hexInt(0xfb4747), for: .normal)
        button1.addTarget(self, action: #selector(closwBtnClick(_:)), for: .touchUpInside)
        bar.addSubview(button1)
        return bar
    }()
    
    
    override func awakeFromNib() {
        workPlaceNameTextField.inputAccessoryView = closwBtn
        workAliasNameTextField.inputAccessoryView = closwBtn
        checkErrorRangeTextField.inputAccessoryView = closwBtn
    }
    
    @objc private func submitClicked(_ sender:Any?){
        O2Logger.debug("submitClicked")
        superview?.endEditing(true)
        let someValues = (workPlaceNameTextField.text!,workAliasNameTextField.text!,checkErrorRangeTextField.text!)
        NotificationCenter.post(customeNotification: .newWorkPlace, object: someValues)
        
    }
    
    @objc private func closwBtnClick(_ sender:Any?){
        superview?.endEditing(true)
    }
    
    
}
