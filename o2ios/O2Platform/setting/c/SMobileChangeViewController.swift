//
//  SMobileChangeViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/17.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

import Alamofire
import AlamofireImage
import AlamofireObjectMapper
import ObjectMapper
import CocoaLumberjack
import O2OA_Auth_SDK

class SMobileChangeViewController: UIViewController {

    @IBOutlet weak var modifyButton: UIButton!
    
    @IBOutlet weak var phoneNumberLabel: UILabel!
    
    override func awakeFromNib() {
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        let mobile = O2AuthSDK.shared.bindDevice()?.mobile
        
        self.phoneNumberLabel.text = mobile
        
        self.modifyButton.layer.borderWidth = 1.0
        self.modifyButton.layer.cornerRadius = 20
        self.modifyButton.layer.masksToBounds = true
        self.modifyButton.layer.borderColor = base_color.cgColor
        self.modifyButton.theme_setTitleColor(ThemeColorPicker(keyPath: "Base.base_color"), forState: .normal)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func modifyMobileAction(_ sender: UIButton) {
        
        self.showDefaultConfirm(title: "确认提示", message: "确定要解绑当前手机号码，解绑后需要重新绑定服务器后才能继续使用？") { (action) in
            let deviceId = O2AuthSDK.shared.bindDevice()?.name ?? ""
            O2AuthSDK.shared.unBindFromCollect(deviceId: deviceId, callback: { (result, error) in
                DDLogDebug("unbind callback result:\(result) , error:\(error ?? "")")
                O2AuthSDK.shared.clearAllInformationBeforeReBind(callback: { (result, msg) in
                    DDLogInfo("清空登录和绑定信息，result:\(result), msg:\(msg ?? "")")
                    OOAppsInfoDB.shareInstance.removeAll()
                    DispatchQueue.main.async {
                        self.forwardDestVC("login", "bindVC")
                    }
                })
            })
        }
        
    }

   
}
