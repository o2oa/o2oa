//
//  OOTimerButton.swift
//  o2app
//
//  Created by 刘振兴 on 2017/8/29.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit


/// 定时器按钮，按下后显示相应的读秒

public class OOTimerButton: UIButton {
    
    //定时时长，单位秒
    private var timeLength:Int = 60 {
        didSet {
            count = timeLength
        }
    }
    
    private var count:Int = 60
    
    //定时时显示标签
    private var timeredLabel:UILabel!
    
    //标签文本颜色
    private var labelTextColor:UIColor!
    
    //按钮文本颜色
    private var buttonTextColor:UIColor!
    
    //按钮标题
    private var buttonTtitle:String!
    
    private var buttonAttributeString:NSAttributedString!
    
    private var disableButtonAttributeString:NSAttributedString!
    //定时器
    private var timer:Timer!
    
    //定时器状态
    private var timerStatus = false

    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
   public  init(_ timeLength:Int,_ title:String,_ buttonTextColor:UIColor,_ labelTextColor:UIColor) {
    super.init(frame: .zero)
    self.buttonTtitle = title
    self.buttonTextColor = buttonTextColor
    self.labelTextColor = labelTextColor
    self.layer.cornerRadius = 4
    self.layer.masksToBounds = true
    self.buttonAttributeString = NSAttributedString(string: buttonTtitle, attributes: [NSAttributedString.Key.foregroundColor:buttonTextColor,NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 13.0)!])
    self.disableButtonAttributeString = NSAttributedString(string: buttonTtitle, attributes: [NSAttributedString.Key.foregroundColor:UIColor.lightGray,NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 13.0)!])
    setupUI()
    }
    

    public func theme_setButtonTextColor(buttonTextColor:UIColor) {
        self.buttonTextColor = buttonTextColor
        self.buttonAttributeString = NSAttributedString(string: buttonTtitle, attributes: [NSAttributedString.Key.foregroundColor:buttonTextColor,NSAttributedString.Key.font:UIFont(name: "PingFangSC-Regular", size: 13.0)!])
        self.setAttributedTitle(buttonAttributeString, for: .normal)
    }
    
    private func setupUI(){
        //设置标签
        timeredLabel = UILabel(frame:CGRect(x: 0, y: 0, width: self.frame.size.width, height: self.frame.size.height))
        timeredLabel.textAlignment = .center
        timeredLabel.textColor = labelTextColor
        timeredLabel.font = UIFont(name: "PingFangSC-Regular", size: 13.0)!
        timeredLabel.text = ""
        //设置按钮
        self.setAttributedTitle(buttonAttributeString, for: .normal)
        self.setAttributedTitle(disableButtonAttributeString, for: .disabled)
    }
    
    public override func layoutSubviews() {
        super.layoutSubviews()
    }
    
    
    public func startTiming() {
        self.timerStatus = !self.timerStatus
        self.isEnabled = false
        self.setAttributedTitle(nil, for: .disabled)
        self.setAttributedTitle(nil, for: .normal)
        timeredLabel.frame = CGRect(x: 0, y: 0, width: self.frame.size.width, height: self.frame.size.height)
        timeredLabel.text = "\(count)s后重新发送"
        self.addSubview(timeredLabel)
        if timer != nil {
            timer.invalidate()
            timer = nil
        }
        timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(timingCall(_:)), userInfo: nil, repeats: true)
        timer.fire()
    }
    
    public func stopTiming(){
        self.timerStatus = !self.timerStatus
        if timer != nil {
            timer.invalidate()
        }
        timeredLabel.removeFromSuperview()
        //恢复按钮可点击
        self.isEnabled = true
        self.setAttributedTitle(disableButtonAttributeString, for: .disabled)
        self.setAttributedTitle(buttonAttributeString, for: .normal)
        //重新计时
        count = timeLength
    }
    
    @objc private func timingCall(_ sender:Any){
        count-=1
        if count<=0 {
            self.stopTiming()
        }else{
            timeredLabel.text = "\(count)s重新发送"
            self.layoutIfNeeded()
        }
    }
    
    
}
