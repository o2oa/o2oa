//
//  OOMeetingRoomMainCell.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/18.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOMeetingRoomMainCell: UITableViewCell,Configurable {
    
    @IBOutlet weak var roomNameLabel: UILabel!
    
    @IBOutlet weak var meetingStatusImageView: UIImageView!
    
    @IBOutlet weak var locationLabel: UILabel!
    
    @IBOutlet weak var capacityLabel: UILabel!
    
    @IBOutlet weak var deviceListView: OOMeetingRoomDeviceListView!
    
    @IBOutlet weak var meetingCountLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
    
    func config(withItem item: Any?) {
        guard let model = item as? OOMeetingRoomInfo else {
            return
        }
        roomNameLabel.text = model.name
        if model.available == false {
            meetingStatusImageView.image = UIImage(named: "pic_guanbi")
             roomNameLabel.textColor = UIColor(hex: "#999999")
        }else{
            if model.idle! {
                meetingStatusImageView.image = UIImage(named: "pic_kongxian")
                roomNameLabel.textColor = UIColor(hex: "#66CC80")
            }else{
                meetingStatusImageView.image = UIImage(named: "pic_yuyue")
                 roomNameLabel.textColor = UIColor(hex: "#FB4747")
            }
        }
        
        //楼层：6，房间：6012
        locationLabel.text = "楼层:\(model.floor!),房间:\(model.roomNumber!)"
        //容纳人数：10人
        capacityLabel.text = "容纳人数：\(model.capacity!)人"
        //deviceViewList
//        let dView = OOMeetingRoomDeviceListView()
//        dView.deviceNameList = model.device
        deviceListView.deviceNameList = model.device
        //meetingCount
        meetingCountLabel.text = "\(model.meetingList?.count ?? 0)个"
    }
    
}
