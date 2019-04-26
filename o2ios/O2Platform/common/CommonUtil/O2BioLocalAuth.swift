//
//  O2BioLocalAuth.swift
//  O2BioLocalAuth
//
//  Created by FancyLou on 2019/3/8.
//  Copyright © 2019 O2OA. All rights reserved.
//

import Foundation
import LocalAuthentication
import CocoaLumberjack

class O2BioLocalAuth: NSObject {
    
    static let shared: O2BioLocalAuth = O2BioLocalAuth()
    
    private override init() {}
    
    // 检查当前设备支持的生物识别类型
    func checkBiometryType() -> O2BiometryType {
        if #available(iOS 8.0, *) {
            let context = LAContext.init()
            if(context.canEvaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics, error: nil)){
                if #available(iOS 11.0, *) {
                    switch(context.biometryType) {
                    case LABiometryType.faceID:
                        return (O2BiometryType.FaceID)
                    case LABiometryType.touchID:
                        return  (O2BiometryType.TouchID)
                    case LABiometryType.none:
                        return (O2BiometryType.None)
                    }
                }else {
                    return (O2BiometryType.TouchID)
                }
            }else{
                return (O2BiometryType.None)
            }
        }else {
            return (O2BiometryType.None)
        }
    }
    
    
    func auth(reason: String, selfAuthTitle: String, block:@escaping (O2BioEvaluateResult, String)->Void) {
        
            if #available(iOS 8.0, *) {
                let context = LAContext.init()
                context.localizedFallbackTitle = selfAuthTitle
                if(context.canEvaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics, error: nil)){
                    context.evaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics, localizedReason: reason, reply: { (success, error) in
                        if success {
                            DispatchQueue.main.async {
                                block(O2BioEvaluateResult.SUCCESS, "认证成功")
                            }
                        }else {
                            if error != nil && error is LAError {
                                let code = (error as! LAError).code
                                
//                                LAError.appCancel //应用取消
//                                LAError.authenticationFailed //认证失败
//                                LAError.biometryLockout //生物识别功能锁定，一般是验证错误次数超过5次
//                                LAError.biometryNotAvailable //设备不支持
//                                LAError.biometryNotEnrolled // 未启用生物识别功能
//                                LAError.invalidContext // context失效
//                                LAError.passcodeNotSet //系统没有设置密码，所以无法启用生物识别验证
//                                LAError.systemCancel //系统取消了验证，有可能是其他应用到前台了
//                                LAError.userCancel // 用户点击了取消按钮
//                                LAError.userFallback // 用户点击了自定义处理的按钮
                                
                                DDLogDebug("error:\(error?.localizedDescription ?? "")")
                                if #available(iOS 11.0, *) {
                                    switch code {
                                    case LAError.userFallback:
                                        DDLogDebug("用户需要自定义处理")
                                        DispatchQueue.main.async {
                                            block(O2BioEvaluateResult.FALLBACK, "用户自定义处理")
                                        }
                                        break
                                    case LAError.biometryLockout:
                                        DDLogDebug("锁定了")
                                        DispatchQueue.main.async {
                                            block(O2BioEvaluateResult.LOCKED, "无法使用，识别功能已经锁定了")
                                        }
                                        break
                                    default:
                                        DispatchQueue.main.async {
                                            block(O2BioEvaluateResult.FAILURE, error?.localizedDescription ?? "")
                                        }
                                        break;
                                    }
                                }else {
                                    switch code {
                                    case LAError.userFallback:
                                        DDLogDebug("用户需要自定义处理")
                                        DispatchQueue.main.async {
                                            block(O2BioEvaluateResult.FALLBACK, "用户自定义处理")
                                        }
                                        break
                                    case LAError.touchIDLockout:
                                        DDLogDebug("锁定了")
                                        DispatchQueue.main.async {
                                            block(O2BioEvaluateResult.LOCKED, "无法使用，识别功能已经锁定了")
                                        }
                                        break
                                    default:
                                        DispatchQueue.main.async {
                                            block(O2BioEvaluateResult.FAILURE, error?.localizedDescription ?? "")
                                        }
                                        break;
                                    }
                                }
                                
                            }else {
                                DDLogError("error: \(error?.localizedDescription ?? "")")
                                DispatchQueue.main.async {
                                    block(O2BioEvaluateResult.FAILURE, "其它异常，\(error?.localizedDescription ?? "")")
                                }
                            }
                            
                        }
                    })
                }else {
                    DispatchQueue.main.async {
                        DDLogDebug("error 设备不支持！")
                        block(O2BioEvaluateResult.FAILURE, "设备不支持")
                    }
                }
            }else {
                DispatchQueue.main.async {
                    DDLogDebug("error 设备不支持！")
                    block(O2BioEvaluateResult.FAILURE, "设备不支持")
                }
            }
        }
}

enum O2BiometryType {
    case None // 不支持
    case FaceID //面容识别
    case TouchID //指纹识别
}
// 认证结果
enum O2BioEvaluateResult {
    case SUCCESS
    case FALLBACK
    case LOCKED
    case FAILURE
}
