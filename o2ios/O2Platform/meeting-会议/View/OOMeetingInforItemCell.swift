//
//  OOMeetingInforItemCell.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/4.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit
import JTCalendar

class OOMeetingInforItemCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var statusImageView: UIImageView!
    
    @IBOutlet weak var timeInternal: UILabel!
    
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var meetingroomLabel: UILabel!
    
    @IBOutlet weak var meetingPersonsLabel: UILabel!
    
    @IBOutlet weak var authorLabel: UILabel!
    
    private let dateHelper = JTDateHelper()
    
    var viewModel:OOMeetingMainViewModel?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOMeetingInfo else {
            return
        }
        //时间段
        let startTime = Date(fromString: model.startTime ?? "", format: "yyyy-MM-dd HH:mm:ss", timezone: TimeZone.autoupdatingCurrent, locale: Locale(identifier: "zh"))
        let endTime  = Date(fromString: model.completedTime ?? "", format: "yyyy-MM-dd HH:mm:ss", timezone: TimeZone.autoupdatingCurrent, locale: Locale(identifier: "zh"))
        if startTime != nil && endTime != nil {
            let formatter = DateFormatter()
            formatter.dateFormat = "HH:mm"
            let sTime = formatter.string(from: startTime!)
            let eTime = formatter.string(from: endTime!)
            timeInternal.text = "\(sTime)-\(eTime)"
        }
        
        if dateHelper.date(startTime!, isEqualOrBefore: Date()) {
            self.statusImageView.image = #imageLiteral(resourceName: "pic_jieshu")
            self.timeInternal.textColor = UIColor(hex: "#999999")
        }else{
            self.statusImageView.image = #imageLiteral(resourceName: "pic_yuyue")
            self.timeInternal.theme_textColor = ThemeColorPicker(keyPath: "Base.base_color")
        }
        
        titleLabel.text = model.subject
        
//        viewModel?.loadMeetingRoomById(model.room!, completed: { (roomResult) in
//            guard let itemRoom = roomResult else{
//                self.meetingroomLabel.text = "未知会议室"
//                return
//            }
//            self.meetingroomLabel.text = itemRoom.name
//        })
        
       let persons = model.invitePersonList?.map({ (p) -> String in
           return String(p.split(separator: "@")[0])
        }).joined(separator: ",")
        meetingPersonsLabel.text = persons
        
        authorLabel.text = String(model.applicant!.split(separator: "@")[0])
    }
    
}


