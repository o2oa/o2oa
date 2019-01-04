//
//  MBProgressHUD+JChat.swift
//  JChat
//
//  Created by deng on 2017/3/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import MBProgressHUD

class MBProgressHUD_JChat: MBProgressHUD {

    open static func showMessage(message: String, toView: UIView?) {
        DispatchQueue.main.async {
            var currentView = toView
            if toView == nil {
                currentView = UIApplication.shared.keyWindow
            }
            let hud = MBProgressHUD.showAdded(to: currentView!, animated: true)
            hud.label.text = message
            hud.label.textColor = UIColor(white: 1, alpha: 0.7)
            hud.bezelView.color = .black
            hud.activityIndicatorColor = UIColor(netHex: 0x9B9B9B)
            hud.removeFromSuperViewOnHide = true
        }
    }
    
    open static func show(text: String, view: UIView?, _ time: TimeInterval = 1.5) {
        DispatchQueue.main.async {
            var currentView = view
            if view == nil {
                currentView = UIApplication.shared.keyWindow
            }
            let hud = MBProgressHUD.showAdded(to: currentView!, animated: true)
            hud.label.text = text
            hud.bezelView.color = .black
            hud.mode = .customView
            hud.removeFromSuperViewOnHide = true
            hud.label.textColor = UIColor(white: 1, alpha: 0.7)
            hud.hide(animated: true, afterDelay: time)
        }
    }
    
    open static func hide(forView: UIView?, animated: Bool) {
        DispatchQueue.main.async {
            var currentView = forView
            if currentView == nil {
                currentView = UIApplication.shared.keyWindow
            }
            MBProgressHUD.hide(for: currentView!, animated: animated)
        }
    }

}
