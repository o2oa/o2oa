//
//  CalendarTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2018/8/3.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit

protocol CalendarCellSwithOnDelegate {
    func click(isOn: Bool, calendar: OOCalendarInfo?)
}

class CalendarTableViewCell: UITableViewCell {
    
    var calendarCellDelegate: CalendarCellSwithOnDelegate?
    var calendarInfo: OOCalendarInfo?
    
    @IBOutlet weak var calendarColorView: UIView!
    @IBOutlet weak var calendarNameView: UILabel!
    @IBOutlet weak var calendarSwitch: UISwitch!
    @IBAction func calendarSwitchTap(_ sender: UISwitch) {
        calendarCellDelegate?.click(isOn: sender.isOn, calendar: self.calendarInfo)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func renderCalendar(info: OOCalendarInfo?, _ calendarIds:[String] = []) {
        self.calendarInfo = info
        if let color = info?.color {
            calendarColorView.backgroundColor = UIColor.init(hex: color)
        }else {
            calendarColorView.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        }
        calendarNameView.text = info?.name
        if !calendarIds.isEmpty {
            if let id = info?.id, calendarIds.contains(id) {
                self.calendarSwitch.isOn = true
            } else {
                self.calendarSwitch.isOn = false
            }
        }
    }

}
