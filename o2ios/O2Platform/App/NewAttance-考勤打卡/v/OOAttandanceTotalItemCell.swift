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
        if model.isLate == true {
            self.iconLabel.text = "迟"
            self.iconLabel.backgroundColor = UIColor(hex: "#F5A623")
        }else if model.isLeaveEarlier  == true {
            self.iconLabel.text = "早"
            self.iconLabel.backgroundColor = UIColor(hex: "#AC71E3")
        }else if model.isGetSelfHolidays  == true {
            self.iconLabel.text = "假"
            self.iconLabel.backgroundColor = UIColor(hex: "#4FB2E3")
        }else {
            self.iconLabel.text = "正"
            self.iconLabel.backgroundColor = UIColor(hex: "#FB4747")
        }
    }
    
}
