//
//  OOMeetingRoomDeviceListView.swift
//  o2app
//
//  Created by 刘振兴 on 2018/1/18.
//  Copyright © 2018年 zone. All rights reserved.
//

import UIKit

class OOMeetingRoomDeviceListView: UIView {
    
    //设备名字数组
    
    //传入的设备名字列表，以#分隔
    var deviceNameList:String? {
        didSet {
            guard let devices = deviceNameList else {
                return
            }
            self.subviews.forEach { (view) in
                view.removeFromSuperview()
            }
            devices.split(separator: "#").forEach { (device) in
                let dView = UIImageView(image: UIImage(named: "icon_meeting_\(device)"))
                self.addSubview(dView)
            }
            self.layoutIfNeeded()
        }
    }
    
    private var spaceWidth:CGFloat = 5
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        var x:CGFloat = 0
        let y:CGFloat = 0
        let width:CGFloat = 20
        let height:CGFloat = 20
        self.subviews.forEach { (view) in
            if view.isKind(of: UIImageView.self) {
                view.frame = CGRect(x: x + spaceWidth, y: y, width: width, height: height)
                x += (width + spaceWidth)
            }
        }
    }
    
}
