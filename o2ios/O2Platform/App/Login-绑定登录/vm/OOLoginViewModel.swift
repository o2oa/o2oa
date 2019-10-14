//
//  OOLoginViewModel.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/5.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import Moya
import Promises
import ReactiveSwift
import ReactiveCocoa
import CocoaLumberjack
import O2OA_Auth_SDK

public enum OOLoginError:Error {
    case imLoginFail(OOAppError)
    case imRegisterFail(OOAppError)
}


class OOLoginViewModel: NSObject {
    
//    private let faceRecognizeAPI = OOMoyaProvider<FaceRecognizeAPI>()
    
    //密码输入及验证码按钮可用
    let passwordIsValid = MutableProperty(true)
    // 内网密码
    let pwdIsValid = MutableProperty(true)
    
    let submitButtionIsValid = MutableProperty(false)
    
    let submitButtonCurrentColor = MutableProperty(UIColor.lightGray)
    
//    let faceRecognizeLoginButtonisValid = MutableProperty(false)

    
    override init() {
        super.init()
    }
    
}

extension OOLoginViewModel {
    
    
    
    func _saveAppConfigToDb() {
        
        //判断本地hash和服务器的hash是否一致
        let local = AppConfigSettings.shared.customStyleHash
        if let remote = O2AuthSDK.shared.customStyleHash(), remote != local {
            
            if let current = O2AuthSDK.shared.customStyle() {
                var currentIndex = 0
                var existApps:[String] = [] // 删除配置中不存在的 或者 不需要显示的应用
                if  let nativeList = current.nativeAppList {
                    nativeList.forEachEnumerated { (index, app) in
                        if app.enable == true {
                            let nApp = O2App(title: app.name, appId: app.key, storyBoard: app.iOS?.storyboard, vcName: app.iOS?.vcname, segueIdentifier: nil, normalIcon: "icon_\(app.key!)", selectedIcon: "icon_\(app.key!)", customParameter: nil, order: currentIndex, mainOrder: currentIndex)
                            OOAppsInfoDB.shareInstance.insertData(nApp)
                            currentIndex += 1
                            existApps.append(app.key!)
                        }
                    }
                }
                
                if let portalList = current.portalList {
                    portalList.forEachEnumerated { (index, o2app2) in
                        let status = "{'portalId':'"+o2app2.id!+"'}"
                        let weburl = AppDelegate.o2Collect.genrateURLWithWebContextKey(DesktopContext.DesktopContextKey, query: DesktopContext.appDetailQuery, parameter: ["##status##":status as AnyObject],covertd:false)
                        let app = O2App(title: o2app2.name!, appId:o2app2.id!, storyBoard: "webview", vcName: weburl!, segueIdentifier: "showMailSegue", normalIcon: "icon_youjian", selectedIcon: "icon_youjian", order: currentIndex, mainOrder: currentIndex)
                        OOAppsInfoDB.shareInstance.insertData(app)
                        currentIndex += 1
                        existApps.append(o2app2.id!)
                    }
                }
                
                // 删除配置中不存在的 或者 不需要显示的应用
                OOAppsInfoDB.shareInstance.deleteNotExistApp(existApps)
            }
        }else {
            DDLogError("不需要更新数据？？？？？？？")
        }
        
        
    }
    
    
    
    
    // MARK:- Login IM
    
    func loginIM() -> Promise<Bool> {
        return Promise { fulfill,reject in
            if let account = O2AuthSDK.shared.myInfo() {
                JMSGUser.login(withUsername: account.id!, password: "QazWsxEdc!@#", completionHandler: { (resultObject, errMsg) in
                    if errMsg == nil {
                        O2Logger.info("IM登录成功,user = \(String(describing: resultObject))")
                        fulfill(true)
                    }else{
                        O2Logger.error("IM登录失改,error = \(String(describing: errMsg))")
                        let loginErr = OOAppError.common(type: "im", message: "im login Fail,\(String(describing: errMsg))", statusCode: 40001)
                        reject(loginErr)
                    }
                })
            }else {
                O2Logger.error("IM登录失改,error = 当前登录用户为空！！！")
                let loginErr = OOAppError.common(type: "im", message: "im login Fail,前登录用户为空！！", statusCode: 40001)
                reject(loginErr)
            }
        }
    }
    
    // MARK:- register IM
    func registerIM() -> Promise<Bool> {
        return Promise { fulfill,reject in
            if let account = O2AuthSDK.shared.myInfo() {
                let username = account.id
                let nickname = account.name
                let gener = account.genderType
                let info = JMSGUserInfo()
                info.nickname = nickname!
                if gener == "f" {
                    info.gender = .female
                }else if gener == "m" {
                    info.gender = .male
                }else{
                    info.gender = .unknown
                }

                JMSGUser.register(withUsername: username!, password: "QazWsxEdc!@#", userInfo: info, completionHandler: { (resultObject, regError) in
                    if let error = regError {
                        let registerErr = OOAppError.common(type: "im", message: "im register Fail,\(String(describing: error))", statusCode: 40001)
                        reject(OOLoginError.imRegisterFail(registerErr))
                    }else{
                        fulfill(true)
                    }
                })
            }else {
                let registerErr = OOAppError.common(type: "im", message: "im register Fail,当前登录用户为空", statusCode: 40001)
                reject(OOLoginError.imRegisterFail(registerErr))
            }
            
        }
    }
    
   
    
    /// 人脸识别
    ///
    /// - Parameter image: 需要识别的图片
    /// - Returns: 如果成功识别 返回userId
//    func faceRecognize(image: UIImage) -> Promise<String> {
//        return Promise { fulfill, reject in
//            //TODO 这里的faceset 到时候要用collect上的unit id代替
//            self.faceRecognizeAPI.request(.search(image, "dev_o2oa_io"), completion: { (result) in
//                switch result {
//                    case .success(let data):
//                    let response = data.mapObject(FaceSearchResponse.self)
//                    var userId = ""
//                    if let ar = response?.data?.results {
//                        if ar.count > 0 {
//                            userId = ar[0].user_id ?? ""
//                            DDLogDebug("userId:\(userId)")
//                        }
//                    }
//                    if userId != "" {
//                        fulfill(userId)
//                    }else {
//                        reject(OOAppError.common(type:"faceError",message:"没有识别到用户",statusCode:1024))
//                    }
//                    break
//                    case .failure(let error):
//                    reject(error)
//                    break
//                }
//            })
//        }
//    }
    
    
    
    // MARK:- helper
    private static func cleanCookieCache(_ remoteURL:String) {
        let cstorage = HTTPCookieStorage.shared
        if let comps = URLComponents(string:remoteURL) {
            let url = "\(comps.scheme!)://\(comps.host!):\(comps.port!)"
            if let cookies = cstorage.cookies(for: URL(string: url)!){
                for cookie in cookies {
                    cstorage.deleteCookie(cookie)
                }
            }
        }
    }
    
    
}

extension OOLoginViewModel {
    
    func loginControlIsValid(_ userNameField:UITextField,_ passwordTextField:UITextField, _ isInner: Bool = false) -> Void {
        let phoneValidSingal = userNameField.reactive.continuousTextValues.map { (text) -> Bool in
            guard text.count > 0 else {
                return false
            }
            return true
        }
//        phoneValidSingal.observeValues { (res) in
//            if isInner {
//                self.pwdIsValid.value = res
//            }else {
//                self.passwordIsValid.value = res
//            }
//        }
        
        
        let passwordValidSingal = passwordTextField.reactive.continuousTextValues.map { (text) -> Bool in
            if isInner {
                guard text.count > 0 else {
                    return false
                }
            }else {
                guard text.count >= 4 else {
                    return false
                }
            }
            
            return true
        }
        
        let signUpActiveSignal = Signal.combineLatest(phoneValidSingal, passwordValidSingal).scan(into: false) { (myResult, arg) in
            myResult  = arg.0 && arg.1
        }
        
        signUpActiveSignal.observeValues { (res) in
            self.submitButtionIsValid.value = res
        }
        
        let signUpColorSignal = signUpActiveSignal.map { (param) -> UIColor in
            if param {
                return base_color
            }else{
                return UIColor.lightGray
            }
        }
        
        signUpColorSignal.observeValues { (currentColor) in
            self.submitButtonCurrentColor.value = currentColor
        }
    }
    
    
//    /// 人脸识别功能是否可用
//    func faceRecognizeValidate() {
//        // face++ 授权验证
//        MGFaceLicenseHandle.license { (result, date) in
//            DDLogInfo("face++ 验证权限完成， result:\(result)")
//            self.faceRecognizeLoginButtonisValid.value = result
//        }
//
//    }
}
