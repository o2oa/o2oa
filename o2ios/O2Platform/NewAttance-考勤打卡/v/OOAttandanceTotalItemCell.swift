//
//  OOAttandanceTotalItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/23.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttandanceTotalItemCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var iconLabel: UILabel!
    
    @IBOutlet weak var checkinDateLabel: UILabel!
    
    @IBOutlet weak var startTimeLabel: UILabel!
    
    @IBOutlet weak var endTimeLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        iconLabel.layer.cornerRadius = 10
        iconLabel.layer.masksToBounds = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOAttandanceCheckinTotal else {
            return
        }
        checkinDateLabel.text = model.recordDateString
        startTimeLabel.text = model.onDutyTime
        endTimeLabel.text = model.offDutyTime
    }
    
}
