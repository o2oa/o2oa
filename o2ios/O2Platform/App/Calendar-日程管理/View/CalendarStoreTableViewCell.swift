//
//  CalendarStoreTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2020/9/22.
//  Copyright © 2020 zoneland. All rights reserved.
//

import UIKit


protocol CalendarStoreCellFollowDelegate {
    func follow(calendar: OOCalendarInfo?)
}

class CalendarStoreTableViewCell: UITableViewCell {

    @IBOutlet weak var calendarColorView: UIView!
    
    @IBOutlet weak var calendarTitleLable: UILabel!
    
    @IBOutlet weak var calendarOwnerLabel: UILabel!
    
    @IBOutlet weak var calendarFllowBtn: UIButton!
    
    @IBAction func tapFollowBtn(_ sender: UIButton) {
        self.delegate?.follow(calendar: self.info)
    }
    
    var delegate: CalendarStoreCellFollowDelegate?
    private var info: OOCalendarInfo?
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
    
    func setOOCalendarInfo(calendar: OOCalendarInfo) {
        self.info = calendar
        if let color = calendar.color {
            self.calendarColorView.backgroundColor = UIColor.init(hex: color)
        }else {
            self.calendarColorView.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        }
        self.calendarTitleLable.text = calendar.name ?? ""
        self.calendarOwnerLabel.text = "创建人: \(calendar.createor?.getChinaName() ?? "")"
        if calendar.followed == true {
            self.calendarFllowBtn.setTitle("已关注", for: .normal)
            self.calendarFllowBtn.setTitleColor(toolbar_text_color, for: .normal)
        }else {
            self.calendarFllowBtn.setTitle("关注", for: .normal)
            self.calendarFllowBtn.setTitleColor(base_color, for: .normal)
        }
    }

}
