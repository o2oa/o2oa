//
//  O2PersonalViewModel.swift
//  O2Platform
//
//  Created by FancyLou on 2018/11/21.
//  Copyright © 2018 zoneland. All rights reserved.
//

import CocoaLumberjack
import Promises
import O2OA_Auth_SDK

class O2PersonalViewModel: NSObject {
    
    private let personalAPI = OOMoyaProvider<PersonalAPI>()
    
    
    
    /// 获取个人信息
    ///
    /// - Returns: O2PersonInfo
    func loadMyInfo() -> Promise<O2PersonInfo> {
        return Promise<O2PersonInfo> { fulfill,reject in
            self.personalAPI.request(.personInfo, completion: { (result) in
                let response = OOResult<BaseModelClass<O2PersonInfo>>(result)
                if response.isResultSuccess() {
                    if let person = response.model?.data {
                        fulfill(person)
                    }else {
                        reject(OOAppError.apiResponseError("没有获取到用户信息！"))
                    }
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    /// 更新个人信息
    ///
    /// - Parameter person: 个人信息O2PersonInfo
    /// - Returns: Bool
    func updateMyInfo(person: O2PersonInfo) -> Promise<Bool> {
        return Promise<Bool> { fulfill,reject in
            self.personalAPI.request(.updatePersonInfo(person), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonIdModel>>(result)
                if response.isResultSuccess() {
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
            })
        }
    }
    
    /// 更新用户头像
    ///
    /// - Parameter icon: 用户头像
    /// - Returns: Bool
    func updateMyIcon(icon: UIImage) -> Promise<Bool>  {
        return Promise<Bool> { fulfill,reject in
            self.personalAPI.request(.updatePersonIcon(icon), completion: { (result) in
                let response = OOResult<BaseModelClass<OOCommonValueBoolModel>>(result)
                if response.isResultSuccess() {
                    fulfill(true)
                }else {
                    reject(response.error!)
                }
                
            })
        }
    }
    
    
}
