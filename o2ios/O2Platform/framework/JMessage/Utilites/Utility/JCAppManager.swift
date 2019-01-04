//
//  JCAppManager.swift
//  JChat
//
//  Created by deng on 2017/6/23.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCAppManager {
    
    static func openAppSetter() {
        let url = URL(string: UIApplication.openSettingsURLString)
        if UIApplication.shared.canOpenURL(url!) {
            UIApplication.shared.openURL(url!)
        }
    }

}


var isIPhoneX: Bool {
    if UIScreen.main.bounds.height > 736 {
        return true
    } else {
        return false
    }
}
