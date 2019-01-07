//
//  MeetingRoomDeviceListView.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/8/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

class MeetingRoomDeviceListView: UIView {
    
    fileprivate  let DEVICE_WIDTH = 12
    fileprivate  let DEVICE_WIDTH_SPACE  = 5
    fileprivate  let DEVICE_HEIGHT = 12
    
    var deviceList:String?{
        didSet {
            let dList = deviceList?.components(separatedBy: "#")
            let x = 0,y=0
            var index = 0
            for deviceName in dList! {
                let view = UIImageView(image: UIImage(named: "icon_room_\(deviceName)"))
                view.frame = CGRect(x: CGFloat(x) + CGFloat(DEVICE_WIDTH) * CGFloat(index) + CGFloat(DEVICE_WIDTH_SPACE), y: CGFloat(y), width: CGFloat(DEVICE_WIDTH), height: CGFloat(DEVICE_HEIGHT))
                self.addSubview(view)
                index+=1
            }
            self.layoutIfNeeded()
        }
    }

}
