//
//  OOUISwitch.swift
//  o2app
//
//  Created by 刘振兴 on 2017/10/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOUISwitch: UISwitch {
    
    override func awakeFromNib() {
      
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
        
    }
    
    func commonInit(){
        self.onImage = #imageLiteral(resourceName: "pic_anniu_2")
        self.offImage  = #imageLiteral(resourceName: "pic_anniu_1")
        
    }
    
    
    
    override func prepareForInterfaceBuilder() {
        awakeFromNib()
    }

}
