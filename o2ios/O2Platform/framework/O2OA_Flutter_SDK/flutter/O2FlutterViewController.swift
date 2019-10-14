//
//  O2FlutterViewController.swift
//  O2Platform
//
//  Created by FancyLou on 2019/3/14.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import Flutter
import O2OA_Auth_SDK
import CocoaLumberjack


class O2FlutterViewController: FlutterViewController {

    let channelName = "net.o2oa.flutter/native_get" //flutter通道名称
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let flutterEngine = self.engine
        GeneratedPluginRegistrant.register(with: flutterEngine)
        processFlutterMethod(flutterEngine!)
    }
    
    /**
     处理Native和Flutter通道
     **/
    private func processFlutterMethod(_ engine: FlutterEngine) {
        
        let messagechannel = FlutterMethodChannel.init(name: channelName, binaryMessenger: engine)
        messagechannel.setMethodCallHandler { (call, result) in
            DDLogDebug("call method........\(call.method)")
            if call.method == "o2Config" {
                //初始化flutter的时候传递给flutter端的参数 o2Theme、o2UserInfo、o2UnitInfo、o2WebServerInfo、o2AssembleServerInfo
                var dic = [String:String]()
                dic["o2Theme"] = "red"
                let user = O2AuthSDK.shared.myInfo()
                let jsonUser = user?.toJSONString()
                dic["o2UserInfo"] = jsonUser
                let unit = O2AuthSDK.shared.bindUnit()
                let jsonUnit = unit?.toJSONString()
                dic["o2UnitInfo"] = jsonUnit
                let center = O2AuthSDK.shared.centerServerInfo()
                let jsonCenter = center?.toJSONString()
                dic["o2CenterServerInfo"] = jsonCenter
                result(dic)
            }
            if call.method == "closeSelf" {
                self.closeSelf()
            }
        }
    }
    
    @objc private func closeSelf() {
        DDLogDebug("close lalalalaalalalal ...........")
        self.dismiss(animated: false, completion: nil)
    }
    
    
    
    
    
}
