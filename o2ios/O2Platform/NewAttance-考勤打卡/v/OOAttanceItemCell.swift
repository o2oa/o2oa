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
        checkTimeLabel.text = "打卡时间:\(model.recordDateString ?? "") \(model.signTime ?? "")"
        checkLocationLabel.text = "\(model.recordAddress ?? "")"
    }
    
}
