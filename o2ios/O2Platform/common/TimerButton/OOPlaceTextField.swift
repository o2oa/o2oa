//
//  OOPlaceTextField.swift
//  o2app
//
//  Created by 刘振兴 on 2017/10/17.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOPlaceTextField: UITextField {

    override func textRect(forBounds bounds: CGRect) -> CGRect {
        return CGRect(x: 10, y: 0, width: bounds.width - 10, height: bounds.height)
    }
    
    override func editingRect(forBounds bounds: CGRect) -> CGRect {
        return CGRect(x: 10, y: 0, width: bounds.width - 10, height: bounds.height)
    }

}
