//
//  O2ReachabilityManager.swift
//  O2Platform
//
//  Created by 刘振兴 on 2017/6/2.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import Foundation
import Alamofire
import CocoaLumberjack
class O2ReachabilityManager {
    
    static let sharedInstance = O2ReachabilityManager()
    
    private let networkReachabilityManager:NetworkReachabilityManager!
    
    private init() {
        networkReachabilityManager = NetworkReachabilityManager()
        networkReachabilityManager.listener = { netStatus in
            switch netStatus {
            case .unknown:
                DDLogError("未知网络 unknown")
                break;
            case .notReachable:
                DDLogError("没有联网，请连接网络 notReachable")
                break;
            case .reachable:
                let msg = "网络是可用的，"
                if self.networkReachabilityManager.isReachableOnWWAN {
                    DDLogInfo("\(msg)现在使用的是移动网络")
                }
                if self.networkReachabilityManager.isReachableOnEthernetOrWiFi {
                    DDLogInfo("\(msg)现在使用的是WiFi")
                }
                break;
            }
        }

    }
    
    
    public func startListening() -> Void {
        self.networkReachabilityManager.startListening()
    }
    
    public func stopListening() -> Void {
        self.networkReachabilityManager.stopListening()
    }
    
 }
