//
//  O2JPushManager.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/11.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import Moya
import O2OA_Auth_SDK
import Promises
import CocoaLumberjack

class O2JPushManager {
    static let shared: O2JPushManager = {
        return O2JPushManager()
    }()
    
    private init() {
        
    }
    
    private let o2JPushAPI = {
        return OOMoyaProvider<JPushAPI>()
    }()
    
    
    
    //直连版本 连接o2oa服务器绑定设备号到个人属性中
    func o2JPushBind() {
        DDLogDebug("绑定设备号")
        if let _ = O2AuthSDK.shared.o2APIServer(context: .x_jpush_assemble_control) {
            let deviceName = O2AuthSDK.shared.getDeviceToken()
            let device = JPushDevice()
            device.deviceName = deviceName
            device.deviceType = "ios"
            self.o2JPushAPI.request(.bindDevice(device)) { (result) in
                let response = OOResult<BaseModelClass<OOCommonValueBoolModel>>(result)
                if response.isResultSuccess() {
                    let value = response.model?.data
                    DDLogInfo("绑定设备到个人属性，结果：\(value?.value ?? false)")
                }else {
                    DDLogError(response.error?.localizedDescription ?? "绑定设备到个人属性失败！")
                }
            }
        }else {
            DDLogError("绑定，没有获取到极光推送消息模块，服务器版本不支持！！！！！")
        }
    }
    
    func O2JPushUnBind() {
        DDLogDebug("解除绑定设备号")
        if let _ = O2AuthSDK.shared.o2APIServer(context: .x_jpush_assemble_control) {
            let deviceName = O2AuthSDK.shared.getDeviceToken()
            self.o2JPushAPI.request(.unBindDevice(deviceName)) { (result) in
                let response = OOResult<BaseModelClass<OOCommonValueBoolModel>>(result)
                if response.isResultSuccess() {
                    let value = response.model?.data
                    DDLogInfo("解绑设备号 ，结果：\(value?.value ?? false)")
                }else {
                    DDLogError(response.error?.localizedDescription ?? "解绑设备号 失败！")
                }
            }
        }else {
            DDLogError("解绑，没有获取到极光推送消息模块，服务器版本不支持！！！！！")
        }
    }
}
