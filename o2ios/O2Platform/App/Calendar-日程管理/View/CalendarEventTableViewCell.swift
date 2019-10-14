//
//  CalendarEventTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2018/7/27.
//  Copyright © 2018 zoneland. All rights reserved.
//

import UIKit

class CalendarEventTableViewCell: UITableViewCell {

    @IBOutlet weak var eventColorView: UIView!
    @IBOutlet weak var eventTitleView: UILabel!
    @IBOutlet weak var eventTimeStack: UIStackView!
    @IBOutlet weak var eventStartTime: UILabel!
    @IBOutlet weak var eventEndTime: UILabel!
    @IBOutlet weak var eventAllDay: UILabel!
    
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        eventColorView.setCornerRadius(radius: CGFloat(9))
        eventAllDay.isHidden = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func renderCell(withItem event: Any?) {
        guard let model = event as? OOCalendarEventInfo else {
            return
        }
        eventTitleView.text = model.title
        if let color = model.color {
            eventColorView.backgroundColor = UIColor.init(hex: color)
        }else {
            eventColorView.theme_backgroundColor = ThemeColorPicker(keyPath: "Base.base_color")
        }
        if model.isAllDayEvent == true {
            eventTimeStack.isHidden = true
            eventAllDay.isHidden = false
        }else {
            eventTimeStack.isHidden = false
            eventAllDay.isHidden = true
            let starttime = model.startTimeStr?.subString(from: 11, to: 16) ?? ""
            eventStartTime.text = starttime
            let endtime = model.endTimeStr?.subString(from: 11, to: 16) ?? ""
            eventEndTime.text = endtime
        }
    }

}
