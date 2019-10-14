//
//  SMessageNotiViewController.swift
//  O2Platform
//
//  Created by 刘振兴 on 2016/10/14.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import UIKit

import CocoaLumberjack
import UserNotifications


class SMessageNotiViewController: UITableViewController {
    @IBOutlet weak var messageSwitch:UISwitch!

    override func viewDidLoad() {
        super.viewDidLoad()

        if #available(iOS 10.0, *){
           let center = UNUserNotificationCenter.current()
            center.getNotificationSettings(completionHandler: { (settings) in
                let status = settings.authorizationStatus
                let soundSetting  = settings.soundSetting
                let badgeSetting = settings.badgeSetting
                let alertSetting = settings.alertSetting
                let notiSetting = settings.notificationCenterSetting
                let lockSetting = settings.lockScreenSetting
                let alertStyleSetting = settings.alertStyle
                DDLogDebug("status = \(status.rawValue),soundSetting = \(soundSetting.rawValue),badgeSetting = \(badgeSetting.rawValue),alertSetting = \(alertSetting.rawValue),notiSetting=\(notiSetting.rawValue),lockSetting = \(lockSetting.rawValue),alertStyleSetting=\(alertStyleSetting.rawValue)")
                
                DispatchQueue.main.async {
                    if status == UNAuthorizationStatus.authorized {
                        self.messageSwitch.setOn(true, animated: true)
                    }else{
                        self.messageSwitch.setOn(false, animated: true)
                    }
                }
            })
        }else{
             //UIApplication.shared.currentUserNotificationSettings?.types
            let type  = UIApplication.shared.currentUserNotificationSettings?.types
            if type != nil {
                DDLogDebug("type  = \(type!)")
            }
            if type == UIUserNotificationType.alert || type == UIUserNotificationType.badge || type == UIUserNotificationType.sound {
                self.messageSwitch.setOn(true, animated: true)
            }else{
                self.messageSwitch.setOn(false, animated: true)
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func sender(_ sender: UISwitch) {
        DDLogDebug(sender.isOn.description)
    }
    
    @IBAction func clickSwitchAction(_ sender: UISwitch) {
        DDLogDebug(sender.isOn.description)
    }

   

}
