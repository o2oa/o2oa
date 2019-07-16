//
//  OOPasswordTextField.swift
//  o2app
//
//  Created by 刘振兴 on 2017/10/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit
import SwiftValidator

class OOPasswordTextField: UITextField {
    
    //左视图普通图片
    @IBInspectable open var customLeftViewText:String?
    //左视图高亮图片
    @IBInspectable open var customRightView:UIButton?
    //下划线普通颜色
    @IBInspectable open var lineColor:UIColor?
    //下划线高亮颜色
    @IBInspectable open var lineLightColor:UIColor?
    //下划线
    fileprivate var lineView:UIView!
    
    let validator = Validator()
    
    var rule:RegexRule? {
        didSet {
            validator.registerField(self, rules: [rule!])
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func prepareForInterfaceBuilder() {
        commonInit()
    }
    
    open override func awakeFromNib() {
        commonInit()
    }
    
    func commonInit(){
        self.borderStyle = .none
        self.isSecureTextEntry = true
        
        self.delegate = self
        
        self.textColor = UIColor.hexInt(0x666666)
        self.font = UIFont(name: "PingFangSC-Regular", size: 15)!
        
        self.attributedPlaceholder = NSMutableAttributedString(string: self.placeholder ?? "", attributes: [NSAttributedString.Key.foregroundColor:UIColor.hexInt(0x999999),NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 14)!])
        
        //leftView
        self.leftViewMode = .always
        let leftLabel =  UILabel(frame:CGRect(x: 0, y: 0, width: 120, height: 30))
        leftLabel.font = UIFont.systemFont(ofSize: 14)
        leftLabel.text = customLeftViewText
        leftLabel.textColor = UIColor.hexInt(0x333333)
        self.leftView = leftLabel
        
        //rightView
        self.rightViewMode = .always
        self.customRightView = UIButton(type: .custom)
        self.customRightView?.frame = CGRect(x: 0, y: 0, width: 20, height: 20)
        self.customRightView?.setImage(#imageLiteral(resourceName: "icon_password_read_normal"), for: .normal)
        self.customRightView?.theme_setImage(ThemeImagePicker(keyPath: "Icon.icon_password_read_selected"), forState: .selected)
        self.customRightView?.addTarget(self, action: #selector(pwdTextSwitch(_:)), for: .touchUpInside)
        self.rightView = customRightView!
        lineView = UIView(frame:CGRect(x: 0, y: frame.height - 0.5, width: frame.width, height: 0.5))
        lineView.backgroundColor = lineColor
        addSubview(lineView)
    }
    
    @objc func pwdTextSwitch(_ sender:UIButton){
        sender.isSelected = !sender.isSelected
        if sender.isSelected {
            let tempPwdStr = self.text
            self.text = ""
            self.isSecureTextEntry = false
            self.text = tempPwdStr
        } else {
            let tempPwdStr = self.text
            self.text = ""
            self.isSecureTextEntry = true
            self.text = tempPwdStr
        }
    }
    
    open override func layoutSubviews() {
        super.layoutSubviews()
        lineView.frame = CGRect(x: 0, y: self.frame.height - 0.5, width: self.frame.width, height: 0.5)
        
    }
    
    fileprivate func changeShowStatus(_ status:Bool = false) -> Void {
        if status {
            lineView.backgroundColor = lineLightColor
        }else{
            lineView.backgroundColor = lineColor
        }
    }
    
}

extension OOPasswordTextField:UITextFieldDelegate {
    
    public func textFieldDidBeginEditing(_ textField: UITextField) {
        changeShowStatus(true)
    }
    
    public func textFieldDidEndEditing(_ textField: UITextField) {
        changeShowStatus(false)
        validationRule(textField)
    }
    
    public func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        guard let _ = rule else {
            return true
        }
        validationRule(textField)
        return true
    }
    
    func validationRule(_ textField: UITextField) {
        validator.validateField(textField) { (error) in
            if error != nil  {
                
            }
        }
    }
}
