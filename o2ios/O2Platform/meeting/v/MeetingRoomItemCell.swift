//
//  MeetingRoomItemCell.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class MeetingRoomItemCell: UITableViewCell {
    
    @IBOutlet weak var deviceListView: MeetingRoomDeviceListView!
    
    @IBOutlet weak var meetingRoomTitleLabel: UILabel!
    
    @IBOutlet weak var personLabel: UILabel!
    
    var meetingRoom:Room?{
        didSet {
            meetingRoomTitleLabel.text = meetingRoom?.name
            personLabel.text = "\((meetingRoom?.capacity)!)人"
            self.deviceListView.deviceList = meetingRoom?.device
            self.layoutIfNeeded()
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
