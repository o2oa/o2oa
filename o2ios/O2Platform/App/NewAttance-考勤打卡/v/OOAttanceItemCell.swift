//
//  OOAttanceItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/15.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit

class OOAttanceItemCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var topLineImageView: UIImageView!
    
    @IBOutlet weak var bottomLineImageView: UIImageView!
    
    @IBOutlet weak var checkTimeLabel: UILabel!
    
    @IBOutlet weak var checkLocationLabel: UILabel!
    
    @IBOutlet weak var checkStatusImageView: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOAttandanceMobileDetail else {
            return
        }
        var showTime = ""
        if let time = model.signTime {
            if time.length > 5 {
                showTime = time.subString(from: 0, to: 5)
            }else {
                showTime = time
            }
        }
        if let type = model.checkin_type {
            checkTimeLabel.text = "\(type): \(showTime)"
        }else {
            checkTimeLabel.text = "打开时间: \(showTime)"
        }
        checkLocationLabel.text = "\(model.recordAddress ?? "")"
    }
    
}
