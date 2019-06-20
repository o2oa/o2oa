//
//  OOUIDownButtonTextField.swift
//  o2app
//
//  Created by 刘振兴 on 2017/9/11.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

protocol OOUIDownButtonTextFieldDelegate:class {
    func viewButtonClicked(_ textField:OOUIDownButtonTextField,_ sender:OOTimerButton)
}

@IBDesignable
class OOUIDownButtonTextField: OOUITextField {
    
    //按钮文字
    @IBInspectable open var buttonTitle:String = "发送验证码"
    //倒计时时长
    @IBInspectable open var countDown = 60
    
    //按钮文字颜色
    @IBInspectable open var buttonTitleColor:UIColor = O2ThemeManager.color(for: "Base.base_color")!
    
    //标签文本颜色
    @IBInspectable open var labelTextColor:UIColor = UIColor.hexInt(0x999999)
    
    
    public var downButton:OOTimerButton?
    
    var buttonDelegate:OOUIDownButtonTextFieldDelegate?
    
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        rightViewMode = .always
        
        //竖线
        let vLineView = UIView(frame:CGRect(x: 0, y: 12.5, width: 1, height: 25))
        vLineView.backgroundColor = UIColor.hexInt(0xDEDEDE)
        
        downButton = OOTimerButton(countDown, buttonTitle, buttonTitleColor, labelTextColor)
        downButton?.frame = CGRect(x: 0, y: 0, width: 100, height: 50)
        downButton?.addSubview(vLineView)
        downButton?.addTarget(self, action: #selector(downCountClick(_:)), for: .touchUpInside)
        rightView = downButton
    }
    
    open func themeUpdate(buttonTitleColor: UIColor) {
        self.buttonTitleColor = buttonTitleColor
        let dbtn = self.rightView as? OOTimerButton
        dbtn?.theme_setButtonTextColor(buttonTextColor: buttonTitleColor)
    }
    
    @objc func downCountClick(_ sender:OOTimerButton){
        sender.startTiming()
        guard let btnDelegate = buttonDelegate else {
            return
        }
        btnDelegate.viewButtonClicked(self, sender)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
    }
    
    func stopTimerButton() {
        downButton?.stopTiming()
    }
    
    
    
    
    
    
    
    
    
    
}
