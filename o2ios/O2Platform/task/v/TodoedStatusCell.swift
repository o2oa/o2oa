//
//  TodoedStatusCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/15.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class TodoedStatusCell: UITableViewCell {
    
    
    @IBOutlet weak var activityLabel: UILabel!
    
    @IBOutlet weak var identityLabel: UILabel!
    
    @IBOutlet weak var statusLabel: UILabel!

    @IBOutlet weak var timeLabel: UILabel!
    
    var statusModel:TodoedStatusModel?{
        didSet {
            self.activityLabel.text = statusModel?.activity
            self.identityLabel.text = statusModel?.identity
            self.statusLabel.text = statusModel?.status
            self.timeLabel.text = statusModel?.statusTime
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
