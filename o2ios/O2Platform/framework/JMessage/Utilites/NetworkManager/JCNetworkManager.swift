//
//  JCNetworkManager.swift
//  JChat
//
//  Created by deng on 2017/6/16.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit

class JCNetworkManager: NSObject {
    
    static var isNotReachable: Bool {
        get {
            let reachability = Reachability.forInternetConnection()
            if let status = reachability?.currentReachabilityStatus() {
                switch status {
                case NotReachable:
                    return true
                default :
                    return false
                }
            }
            return false
        }
    }

}
