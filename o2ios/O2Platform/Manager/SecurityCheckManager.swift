//
//  SecurityCheckManager.swift
//  O2Platform
//
//  Created by FancyLou on 2020/9/29.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Foundation


class SecurityCheckManager {
    
    static let shared : SecurityCheckManager = {
        return SecurityCheckManager()
    }()
    
    private init() {}
    
    ///判断是否存在越狱的软件
    func isJailBroken() -> Bool {
      //判断设备上是否安装了这些程序
      let apps = ["/APPlications/Cydia.app",
                  "/Library/MobileSubstrate/MobileSubstrate.dylib",
                  "/bin/bash",
                  "/usr/sbin/sshd",
                  "/etc/apt",
                  "/usr/bin/ssh",
                  "/APPlications/limera1n.app",
                  "/APPlications/greenpois0n.app",
                  "/APPlications/blackra1n.app",
                  "/APPlications/blacksn0w.app",
                  "/APPlications/redsn0w.app",
                  "/APPlications/Absinthe.app"]
       for app in apps {
           //通过文件管理器，判断在指定的目录下，是否在对应的应用程序。如果存在的话。就表示当前设备为越狱设备。
           if FileManager.default.fileExists(atPath: app){
               return true
           }
       }
       return false
   }
    
}
