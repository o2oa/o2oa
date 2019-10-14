//
//  LogFileTableViewCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/6/2.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import UIKit

class LogFileTableViewCell: UITableViewCell {
    
    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var fileNameLabel: UILabel!

    @IBOutlet weak var fileSizeLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
