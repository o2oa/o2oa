//
//  O2Collect.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/29.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON
import CocoaLumberjack
import O2OA_Auth_SDK


class O2Collect{
 
    static let T_QUERY = "#QUERY#"
    
    //变量
    var moduleWebContextDict:[String:String]?
    
    var moduleHostAndWebContextDict:[String:JSON]?
    
    var webModuleHostAndWebContextDict:[String:JSON]?
    
    var collectUnitData: O2BindUnitModel?
    
    init(){
        self.moduleWebContextDict = [:]
        self.moduleHostAndWebContextDict = [:]
        self.webModuleHostAndWebContextDict = [:]
    }

 
    /**
     生成API调用URL
     
     - parameter appContextKey: appContextKey
     - parameter scheme:        请求应用协议
     - parameter query:         查询路径
     - parameter parameter:     请求参数
     
     - returns: 生成的URL
     */
    func generateURLWithAppContextKey(_ appContextKey:String,scheme:String,query:String,parameter:[String:AnyObject]?,coverted:Bool=true,generateTime:Bool=true) -> String?{
        let nodeAPI = O2AuthSDK.shared.centerServerInfo()?.assembles![appContextKey]
        var baseURL = "".appendingFormat("%@://%@:%@%@/%@", nodeAPI?.httpProtocol ?? "http",(nodeAPI?.host)!,String((nodeAPI?.port)!),(nodeAPI?.context)!,query)
        if let t_parameter = parameter {
            for  (key,value) in t_parameter {
                baseURL = baseURL.replacingOccurrences(of: key, with:value as! String)
            }
        }
        //return self.generateTimestampWithURL(baseURL)!
        //加入时间截
        if coverted{
            if(generateTime){
                baseURL = self.generateTimestampWithURL(baseURL)!
            }
            return baseURL.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)
        }else{
            if(generateTime){
                baseURL = self.generateTimestampWithURL(baseURL)!
            }
            return baseURL
        }
        
    }
    

    
    /**
     生成API调用URL
     
     - parameter appContextKey: appContextKey
     - parameter scheme:        "http"
     - parameter query:         查询路径
     - parameter parameter:     请求参数
     
     - returns: 生成的URL
     */
    func generateURLWithAppContextKey(_ appContextKey:String,query:String,parameter:[String:AnyObject]?,coverted:Bool=true,generateTime:Bool=true) -> String?{
        return self.generateURLWithAppContextKey(appContextKey, scheme: "http", query: query, parameter: parameter,coverted: coverted,generateTime: generateTime)
    }
    
    
    /**
     前台请求H5页面路径生成方法
     
     - parameter webAppContextkey: webAppContext上下文
     - parameter query:            query路径
     - parameter parameter:        参数字典
     
     - returns: 返回请求的实际URL地址
     */
    func genrateURLWithWebContextKey(_ webAppContextkey:String,query:String,parameter:[String:AnyObject]?,covertd:Bool=true) -> String?{

        let webAPI = O2AuthSDK.shared.centerServerInfo()?.webServer
        var baseURL = "".appendingFormat("%@://%@:%@/%@/%@", webAPI?.httpProtocol ?? "http",(webAPI?.host)!,String((webAPI?.port)!),webAppContextkey,query)
        if let param = parameter {
            for (key,value) in param {
                baseURL = baseURL.replacingOccurrences(of: key, with:value as! String)
            }
        }
        if covertd {
            return self.generateTimestampWithURL(baseURL)?.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)
        }else{
            return self.generateTimestampWithURL(baseURL)
        }
    }
    
    func genrateURLWithWebContextKey2(_ query:String,parameter:[String:AnyObject]?,covertd:Bool=true) -> String?{
        let webAPI = O2AuthSDK.shared.centerServerInfo()?.webServer
        var baseURL = "".appendingFormat("%@://%@:%@/%@", webAPI?.httpProtocol ?? "http",(webAPI?.host)!,String((webAPI?.port)!),query)
        if let param = parameter {
            for (key,value) in param {
                baseURL = baseURL.replacingOccurrences(of: key, with:value as! String)
            }
        }
        if covertd {
            return self.generateTimestampWithURL(baseURL)?.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)
        }else{
            return self.generateTimestampWithURL(baseURL)
        }
    }
    
    /**
     对指定的请求路径进行替换生成新的URL
     
     - parameter baseURL:      带有query参数URL
     - parameter requestQuery: 实际的Query上下文参数
     
     - returns: 实际的URL
     */
    
    func setRequestQuery(_ baseURL:String,requestQuery:String)-> String?{
        //return [baseUrl stringByReplacingOccurrencesOfString:T_QUERY withString:requestQuery];
        return baseURL.replacingOccurrences(of: O2Collect.T_QUERY, with: requestQuery)
    }
    
    /**
     给请求的URL增加参数
     
     - parameter baseURL:          带有参数的URL
     - parameter requestParameter: 参数key.value值对
     
     - returns: 将参数设置为了实际值的URL
     */
    func setRequestParameter(_ baseURL:String,requestParameter:[String:AnyObject]?) -> String?{
        var t_url = baseURL
        if let param = requestParameter {
            for (key,value) in param {
                t_url = t_url.replacingOccurrences(of: key, with: value as! String)
            }
        }
        return t_url
    }
    
    /**
     生成时间戳
     
     - parameter baseURL: 需要加入时间戳的URL
     
     - returns: 加入时间戳的URL
     */
    func generateTimestampWithURL(_ baseURL:String) -> String? {
        var resultURL:String?
        if baseURL.contains("?") {
            resultURL = baseURL.appending("&" + RandomString.sharedInstance.getRandomStringOfLength(length: 10))
        }else{
            resultURL = baseURL.appending("?" + RandomString.sharedInstance.getRandomStringOfLength(length: 10))
        }
        return resultURL
    }
    

}
