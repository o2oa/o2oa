//
//  OORegisterTableView.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/3/29.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

protocol OORegisterItemViewDelegate {
    func ooRigisterItemClick(_ sender:Any?)
}

class OORegisterTableView: UIView {
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var actionButton: UIButton!
    
    var delegate:OORegisterItemViewDelegate?
    
    override func awakeFromNib() {
        actionButton.isHidden = true
    }
    
    func configTitle(title:String,actionTitle:String?)  {
        titleLabel.text = title
        if let aTitle = actionTitle {
            actionButton.isHidden = false
            actionButton.setTitle(aTitle, for: .normal)
        }
    }
    
    @IBAction func btnItemClick(_ sender: UIButton) {
        delegate?.ooRigisterItemClick(sender)
    }
    
}
