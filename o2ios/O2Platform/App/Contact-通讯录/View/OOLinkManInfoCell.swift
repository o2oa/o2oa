//
//  OOLinkManInfoCell.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/23.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class OOLinkManInfoCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var valueLabel: UILabel!
    
    @IBOutlet weak var actionButton: UIButton!
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        let model = item as! OOPersonCellModel
        titleLabel.text = model.title
        valueLabel.text = model.value
        if model.actionIconName != nil {
            self.actionButton.setImage(UIImage(named:model.actionIconName!), for: .normal)
        }
    }
    
    

}
