//
//  OOBaseUIButton.swift
//  o2app
//
//  Created by 刘振兴 on 2017/9/11.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit


@IBDesignable
class OOBaseUIButton: UIButton {

    var disableBackColor:UIColor? {
        didSet {
            if !self.isEnabled  {
                self.backgroundColor = disableBackColor
            }
        }
    }
    
    @IBInspectable var layerCornerRadius:Int = 5 {
        didSet {
            configUI()
        }
    }
    
    override func awakeFromNib() {
      configUI()
    }
    
    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        configUI()
    }
    
    func configUI(){
        if layerCornerRadius >= 0 {
            self.layer.cornerRadius = CGFloat(layerCornerRadius)
            self.layer.masksToBounds = true
        } else {
            self.layer.masksToBounds = false
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
        configUI()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
        configUI()
    }
    
    private func commonInit(){
        //默认背景，字体
        self.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        self.titleLabel?.font = UIFont(name: "PingFangSC-Regular", size: 15)!
        self.setTitleColor(UIColor.white, for: .normal)
    }
    
}
