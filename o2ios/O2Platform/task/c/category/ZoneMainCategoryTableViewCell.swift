//
//  ZoneMainCategoryTableViewCell.swift
//  ZoneBarManager
//
//  Created by 刘振兴 on 2017/3/16.
//  Copyright © 2017年 zone. All rights reserved.
//

import UIKit

class ZoneMainCategoryTableViewCell: UITableViewCell {
    
    @IBOutlet weak var appIconImageView: UIImageView!
    
    @IBOutlet weak var appNameLabel: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
