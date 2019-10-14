//
//  DeviceManagerViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2019/5/7.
//  Copyright © 2019 zoneland. All rights reserved.
//


import CocoaLumberjack
import Promises
import O2OA_Auth_SDK

class DeviceManagerViewModel: NSObject {
    
    /**
     * 获取设备列表
     */
    func getDeviceList() -> Promise<[O2BindDeviceModel]> {
        return Promise<[O2BindDeviceModel]>{ fulfill, reject in
            let mobile = O2AuthSDK.shared.bindDevice()?.mobile
            let token = O2AuthSDK.shared.bindDevice()?.name
            let unit = O2AuthSDK.shared.bindUnit()?.id
            if mobile == nil || unit == nil || token == nil {
                reject(OOAppError.common(type: "parameterError", message: "获取不到绑定的信息", statusCode: 1024))
            }else {
                O2AuthSDK.shared.bindDeviceList(unitId: unit!, mobile: mobile!, token: token!, callback: { (list, error) in
                    if error != nil {
                        reject(OOAppError.apiResponseError(error!))
                    }else {
                        fulfill(list)
                    }
                })
            }
        }
    }
    
    
    /**
     * 解绑设备
     */
    func unbindDevice(token: String) -> Promise<Bool> {
        return Promise<Bool> {fulfill, reject in
            O2AuthSDK.shared.unBindFromCollect(deviceId: token) { (result, error) in
                if error != nil {
                    reject(OOAppError.apiResponseError(error!))
                }else {
                    fulfill(true)
                }
            }
        }
    }
       
    
    
    
}
