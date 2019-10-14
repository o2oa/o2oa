//
//  OMeetingPersonActionCell.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/31.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

protocol OOMeetingPersonActionCellDelegate {
   func addPersonActionClick(_ sender:UIButton)
}

class OOMeetingPersonActionCell: UICollectionViewCell {
    
    var delegate:OOMeetingPersonActionCellDelegate?

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    
    @IBAction func addPersonAction(_ sender: UIButton) {
        delegate?.addPersonActionClick(sender)
    }
    
}
