//
//  O2CountdownButton.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/12/16.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class O2CountdownButton: UIButton {
    //定时秒数
    var count:Int = 60
    //定时器
    var timer:Timer!
    //标签
    var labelTimeS:UILabel!
    //默认按钮标题
    var normalText = "获取验证码" {
        didSet {
            self.setTitle(normalText, for: .normal)
            self.layoutIfNeeded()
        }
    }
    //是否在定时状态
    var timeStatus = false
    
    //标签字颜色
    var labelTextColor = UIColor.white
    //按钮正常背景色
    var normalColor = base_color {
        didSet{
            if self.isEnabled {
            self.backgroundColor = normalColor
            self.layoutIfNeeded()
            }
        }
    }
    //按钮定时时的背景色
    var disableColor = toolbar_text_color {
        didSet {
            if !self.isEnabled {
                self.backgroundColor = disableColor
                self.layoutIfNeeded()
            }
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        labelTimeS = UILabel(frame: CGRect(x: 0, y: 0, width: self.frame.size.width, height: self.frame.size.height))
        labelTimeS.textAlignment = .center
        labelTimeS.font = UIFont(name: "PingFangSC-Regular", size: 14.0)!
        labelTimeS.text = normalText
        labelTimeS.textColor = labelTextColor
        //self.addSubview(labelTimeS)
        self.setTitle(normalText, for: .normal)
        self.setTitleColor(UIColor.white, for: .normal)
        self.backgroundColor = normalColor
    }
    
    public func startCount(){
        self.isEnabled = false
        self.timeStatus = !self.timeStatus
        //把按钮本身标题清空
        self.setTitle("", for: .normal)
        //开始把Label add Button
        labelTimeS.text = "\(self.count)s重新获取"
        self.addSubview(labelTimeS)
        //设置按钮背景色
        self.backgroundColor = disableColor
        if timer != nil {
            timer.invalidate()
            timer = nil
        }
        timer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(updateLabel), userInfo: nil, repeats: true)
        timer.fire()
        
    }
    
    ///停止更新
    public func stopCount(){
        self.timeStatus = !self.timeStatus
        timer.invalidate()
        labelTimeS.removeFromSuperview()
        self.setTitle(normalText, for: .normal)
        count = 60
        self.isEnabled = true
        self.backgroundColor = normalColor
    }
    
    ///每秒更新一次Label
    @objc fileprivate func updateLabel(){
        count-=1;
        if count <= 0 {
            self.stopCount()
        }else{
            labelTimeS.text = "\(self.count)s重新获取"
        }
    }

}
