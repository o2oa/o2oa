//
//  CheckAppealCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/25.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit
import BWSwipeRevealCell

class CheckAppealCell: BWSwipeRevealCell {
    
    
    @IBOutlet weak var identityNameLabel: UILabel!
    
    @IBOutlet weak var appealTypeLabel: UILabel!
    
    @IBOutlet weak var appealDateLabel: UILabel!
    
    @IBOutlet weak var appealDescLabel: UILabel!
    
    
    var entry:AttendanceCheckEntry? {
        didSet {
            self.identityNameLabel.text = entry?.identityName
            self.appealDateLabel.text = entry?.appealDate
            self.appealTypeLabel.text = entry?.appealReson
            self.appealDescLabel.text = entry?.appealDesc
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
