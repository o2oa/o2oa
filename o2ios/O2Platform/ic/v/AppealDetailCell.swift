//
//  AppealDetailCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/25.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

protocol AppealDetailCellDelegate {
    func appealDetailAction(_ cell:AppealDetailCell)
}

class AppealDetailCell: UITableViewCell {
    
    @IBOutlet weak var aDate: UILabel!
    
    @IBOutlet weak var aTimeInterval: UILabel!

    @IBOutlet weak var aWorkDateType: UILabel!
    
    @IBOutlet weak var aAppealType: UILabel!
    
    @IBOutlet weak var appealButton: UIButton!
    
    var delegate:AppealDetailCellDelegate?
    
    
    var entry:AttendanceDetailEntry? {
        didSet {
            self.aDate.text = entry?.aDate
            self.aTimeInterval.text = entry?.aTimeInterval
            self.aWorkDateType.text = entry?.aWorkType
            self.aAppealType.text = entry?.aStatusType
            if entry?.isAppeal  == true && entry?.appealStatus == 0 {
                self.appealButton.addTarget(self, action: #selector(self.executeActionDelegate(sender:)), for: .touchUpInside)
                self.appealButton.isHidden = false
            }else {
                self.appealButton.isHidden = true
            }
        }
    }
    
    
    @objc private func executeActionDelegate(sender:UIButton) {
        if let _ = self.delegate {
            self.delegate?.appealDetailAction(self)
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
