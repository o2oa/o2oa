//
//  OOMeetingPersonSelectHeaderView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/29.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOMeetingPersonSelectHeaderView: UICollectionReusableView {
    
    @IBOutlet weak var personNumberLabel: UILabel!
    
    var personCount:Int = 0 {
        didSet {
            self.personNumberLabel.text = "已选择\(personCount)人"
            self.layoutIfNeeded()
        }
    }
    

    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
}
