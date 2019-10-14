//
//  DeviceTableViewCell.swift
//  O2Platform
//
//  Created by FancyLou on 2019/5/7.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import O2OA_Auth_SDK
import CocoaLumberjack

class DeviceTableViewCell: UITableViewCell {

    var unbindClickDelegate: DeviceUnbindBtnClickListener?
    @IBOutlet weak var deviceTitle: UILabel!
    
    @IBOutlet weak var deviceUnbindBtn: UIButton!
    
    @IBAction func unbindClickAction(_ sender: UIButton) {
        guard let d = deviceData else {
            return
        }
        DDLogDebug("点击了 \(d.deviceType)")
        unbindClickDelegate?.onClick(device: d)
    }
    
    var deviceData: O2BindDeviceModel? {
        didSet {
            if deviceData?.deviceType == nil || deviceData!.deviceType.isBlank {
                self.deviceTitle.text = "未知设备"
            }else {
                self.deviceTitle.text = deviceData!.deviceType + " 设备"
            }
            let token = O2AuthSDK.shared.bindDevice()?.name
            DDLogDebug("本机token：\(token ?? "")")
            if token != nil && token == deviceData?.name {
                self.deviceUnbindBtn.setTitle("本机", for: .normal)
                self.deviceUnbindBtn.setTitle("本机", for: .disabled)
                self.deviceUnbindBtn.isEnabled = false
            }else {
                self.deviceUnbindBtn.setTitle("解除绑定", for: .normal)
            }
            
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

}
