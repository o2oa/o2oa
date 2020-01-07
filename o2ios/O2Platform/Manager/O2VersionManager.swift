//
//  O2 App 版本管理
//  O2VersionManager.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/15.
//  Copyright © 2019 zoneland. All rights reserved.
//

import UIKit
import Alamofire
import CocoaLumberjack
import SwiftyJSON
import ObjectMapper
import Promises

class O2VersionManager {
    
    static let shared: O2VersionManager = {
        return O2VersionManager()
    }()
    
    
    private init() {}
    
    //检查版本
    func checkAppUpdate(call:@escaping (_ versionInfo: O2VersionInfoModel?, _ error :String?) -> Void)  {
        if let infoPath = Bundle.main.path(forResource: "Info", ofType: "plist"), let dic = NSDictionary(contentsOfFile: infoPath) {
            if let versionUrl = dic["o2 app version url"] as? String {
                all(self.loadLastAppVersionInfo(url: versionUrl), self.appBuild()).then { (results) in
                    let ios = results.0
                    let appBuild = results.1
                    let onlineIosBuild = Int(string: ios.buildNo ?? "0") ?? 0
                    let thisAppBuild = Int(string: appBuild) ?? 0
                    if onlineIosBuild > thisAppBuild {
                        call(ios, nil)
                    }else {
                        call(nil, "没有新版本，不需要更新！")
                    }
                }.catch { (err) in
                    DDLogError(err.localizedDescription)
                    call(nil, err.localizedDescription)
                }
            }else {
                DDLogError("没有配置版本更新地址，无法检查更新！")
                call(nil, "没有配置版本更新地址，无法检查更新！")
            }
        }else {
            DDLogError("没有Info.plist文件。。。。。是吗？？？")
            call(nil, "没有配置版本更新地址，无法检查更新！")
        }
    }
    
    func updateAppVersion(_ downloadUrl: String?) {
        if let obj = downloadUrl {
            DDLogDebug("更新app地址：\(obj)")
            if  let appURL = URL(string: obj) {
                if UIApplication.shared.canOpenURL(appURL) {
                    UIApplication.shared.openURL(appURL)
                }else {
                    DDLogError("地址不对，无法升级!")
                }
            }
        }else {
            DDLogError("download地址为空！")
        }
    }
    
    private func loadLastAppVersionInfo(url: String) -> Promise<O2VersionInfoModel> {
        return Promise { fulfill, reject in
            //获取版本信息
            if let timeUrl = AppDelegate.o2Collect.generateTimestampWithURL(url) {
                DDLogDebug("url with time : \(timeUrl)")
                Alamofire.request(timeUrl).responseJSON {
                    response in
                    switch response.result {
                    case .success(let val):
                        let ios = JSON(val)["ios"]
                        if let iosModel = Mapper<O2VersionInfoModel>().map(JSONString:ios.description) {
                            let build = iosModel.buildNo
                            let version = iosModel.versionName
                            let url = iosModel.downloadUrl
                            DDLogDebug("ios version：\(version ?? "") , build:\(build ?? ""), url:\(url ?? "")")
                            fulfill(iosModel)
                        }else {
                            DDLogError("解析版本信息对象异常")
                            reject(OOAppError.jsonMapping(message: "解析版本信息对象异常", statusCode: 1024, data: nil))
                        }
                    case .failure(let err):
                        DDLogError(err.localizedDescription)
                        reject(err)
                    }
                }
            }
        }
    }
    
    private func appBuild() -> Promise<String> {
        return Promise { fulfill, reject in
            if let info = Bundle.main.infoDictionary {
                let buildId = info["CFBundleVersion"] as? String
                let version = info["CFBundleShortVersionString"]  as? String
                DDLogDebug("build: \(buildId ?? "") , version:\(version ?? "")")
                fulfill(buildId ?? "")
            }else {
                DDLogError("没有系统 info ！！！！！！")
                reject(OOAppError.common(type: "system", message: "没有系统 info ！", statusCode: 1024))
            }
        }
    }

}
