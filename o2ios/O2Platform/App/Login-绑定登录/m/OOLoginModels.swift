//
//  OOLoginModels.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/5.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import HandyJSON

// MARK: - 手机注册节点请求Model
public class OONodeReqModel:DataModel {
    
    @objc var mobile:String?
    
    @objc var value:String?
    
    @objc var meta:String?
    
    required public init() {
        
    }
    
    public var description: String {
        return "mobile = \(String(describing: mobile)),value=\(String(describing: value)),meta=\(String(describing: meta))"
    }
}



// MARK:- 手机注册请求返回Model
public class OONodeRespModel:DataModel {
    
    var value:Bool?
    
    required public init() {
        
    }
    
    public var description: String {
        return "value=\(String(describing: value))"
    }
}

// MARK: - 中心服务器节点Model
public class OONodeModel:NSObject,DataModel,NSCoding {
    
    @objc var id:String?
    
    @objc var pinyin:String?
    
    @objc var pinyinInitial:String?
    
    @objc var httpProtocol:String?
    
    @objc var name:String?
    
    @objc var centerHost:String?
    
    @objc var centerContext:String = ""
    
    var centerPort:Int?
    
    override public required  init() {
        
    }
    
    
    public func encode(with aCoder: NSCoder) {
        if id != nil {
            aCoder.encode(id, forKey: "id")
        }
        
        if pinyin != nil {
            aCoder.encode(pinyin, forKey: "pinyin")
        }
        
        if httpProtocol != nil {
            aCoder.encode(pinyin, forKey: "httpProtocol")
        }
        
        if pinyinInitial != nil {
            aCoder.encode(pinyinInitial, forKey: "pinyinInitial")
        }
        
        if name != nil {
            aCoder.encode(name, forKey: "name")
        }
        
        if centerHost != nil {
            aCoder.encode(centerHost, forKey: "centerHost")
        }
        
        aCoder.encode(centerContext, forKey: "centerContext")
        
        if centerPort != nil {
            aCoder.encode(centerPort, forKey: "centerPort")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        id = aDecoder.decodeObject(forKey: "id") as? String
        pinyin = aDecoder.decodeObject(forKey: "pinyin") as? String
        httpProtocol = aDecoder.decodeObject(forKey: "httpProtocol") as? String
        pinyinInitial = aDecoder.decodeObject(forKey: "pinyinInitial") as? String
        name = aDecoder.decodeObject(forKey: "name")  as? String
        centerHost = aDecoder.decodeObject(forKey: "centerHost") as? String
        centerContext = aDecoder.decodeObject(forKey: "centerContext") as! String
        centerPort = aDecoder.decodeObject(forKey: "centerPort") as?  Int
    }
    
    public  override var description: String {
        return "id=\(String(describing: id)),pinyin=\(String(describing: pinyin)),pinyinInitial=\(String(describing: pinyinInitial)),name = \(String(describing: name)),centerHost=\(String(describing: centerHost)),centerContext=\(centerContext),centerPort=\(String(describing: centerPort))"
    }
    
    
}


// MARK:- 绑定单元结点Model
public class OOUnitNodeModel:NSObject, DataModel,NSCoding {
    
    
    //选择的节点名称
    @objc var unit:String?
    //手机号码
    @objc  var mobile:String?
    //验证码
    @objc  var code:String?
    //设备推送的token
    @objc var name:String?
    //设备类型
    @objc var deviceType:String = "iOS"
    
    required public override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        if unit != nil {
            aCoder.encode(unit, forKey: "unit")
        }
        
        if mobile != nil {
            aCoder.encode(mobile, forKey: "mobile")
        }
        
        if code != nil {
            aCoder.encode(code, forKey: "code")
        }
        
        if name != nil {
            aCoder.encode(name, forKey: "name")
        }
        
        aCoder.encode(deviceType, forKey: "deviceType")
    }
    
    
    public required init?(coder aDecoder: NSCoder) {
        unit = aDecoder.decodeObject(forKey: "unit") as? String
        mobile = aDecoder.decodeObject(forKey: "mobile") as? String
        code = aDecoder.decodeObject(forKey: "code") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        deviceType = aDecoder.decodeObject(forKey: "deviceType") as! String
    }
    
    public init(unit:String,mobile:String,code:String,name:String) {
        self.unit = unit
        self.mobile = mobile
        self.code = code
        self.name = name
    }
    
    public override var description: String {
        return "unit=\(String(describing: unit)),mobile=\(String(describing: mobile)),code=\(String(describing: code)),name=\(String(describing: name)),deviceType=\(deviceType)"
    }
}

// MARK:- 模块结点模型
public class OOModuleAPI:NSObject,DataModel,NSCoding {
    
    @objc var httpProtocol:String?
    
    @objc var host:String?
    
    @objc var name:String?
    
    @objc var context:String?
    
    var port:Int?
    
    public required override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        
        if httpProtocol != nil {
            aCoder.encode(httpProtocol, forKey: "httpProtocol")
        }
        
        if name != nil {
            aCoder.encode(name, forKey: "name")
        }
        
        if host != nil {
            aCoder.encode(host, forKey: "host")
        }
        
        if context != nil {
            aCoder.encode(context, forKey: "context")
        }
        
        if port != nil {
            aCoder.encode(port, forKey: "port")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        httpProtocol = aDecoder.decodeObject(forKey: "httpProtocol") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        host = aDecoder.decodeObject(forKey: "host") as? String
        context = aDecoder.decodeObject(forKey: "context") as? String
        port = aDecoder.decodeObject(forKey: "port") as? Int
    }
    
    public override var description: String {
        return "\(name ?? "")模块配置信息\(host ?? ""),\(context ?? ""),\(port ?? 0)"
    }
    
    
}

public class OOWebModuleAPI:NSObject,DataModel,NSCoding {
    
    @objc var httpProtocol:String?
    
    @objc var host:String?
    
    var port:Int?
    
    public required override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        
        if httpProtocol != nil {
            aCoder.encode(httpProtocol, forKey: "httpProtocol")
        }
        
        if host !=  nil {
            aCoder.encode(host, forKey: "host")
        }
        
        if port != nil {
            aCoder.encode(port, forKey: "port")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        httpProtocol = aDecoder.decodeObject(forKey: "httpProtocol") as? String
        host = aDecoder.decodeObject(forKey: "host") as? String
        port = aDecoder.decodeObject(forKey: "port") as? Int
    }
}


public class OONodeAPI:NSObject,DataModel,NSCoding {
    
    @objc var assembles:[String:OOModuleAPI]?
    
    @objc var webServer:OOWebModuleAPI?
    
    public required override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        if assembles != nil {
            aCoder.encode(assembles, forKey: "assembles")
        }
        
        if webServer != nil {
            aCoder.encode(webServer, forKey: "webServer")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        assembles = aDecoder.decodeObject(forKey: "assembles") as? [String : OOModuleAPI]
        webServer = aDecoder.decodeObject(forKey: "webServer") as? OOWebModuleAPI
    }
    
    
}

// MARK:- 帐号模型
class OOLoginAccountModel:NSObject,DataModel,NSCoding {
    @objc var changePasswordTime : String?
    @objc var controllerList : [AnyObject]?
    @objc var createTime : String?
    @objc var distinguishedName : String?
    @objc var employee : String?
    @objc var genderType : String?
    @objc var icon : String?
    @objc var id : String?
    @objc var lastLoginAddress : String?
    @objc var lastLoginClient : String?
    @objc var lastLoginTime : String?
    @objc var mail : String?
    @objc var mobile : String?
    @objc var name : String?
    var passwordExpired : Bool?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var qq : String?
    @objc var roleList : [AnyObject]?
    @objc var token : String?
    @objc var tokenType : String?
    @objc var unique : String?
    @objc var updateTime : String?
    @objc var weixin : String?
    
    
    
    public override required init(){}
    
    
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        changePasswordTime = aDecoder.decodeObject(forKey: "changePasswordTime") as? String
        controllerList = aDecoder.decodeObject(forKey: "controllerList") as? [AnyObject]
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        distinguishedName = aDecoder.decodeObject(forKey: "distinguishedName") as? String
        employee = aDecoder.decodeObject(forKey: "employee") as? String
        genderType = aDecoder.decodeObject(forKey: "genderType") as? String
        icon = aDecoder.decodeObject(forKey: "icon") as? String
        id = aDecoder.decodeObject(forKey: "id") as? String
        lastLoginAddress = aDecoder.decodeObject(forKey: "lastLoginAddress") as? String
        lastLoginClient = aDecoder.decodeObject(forKey: "lastLoginClient") as? String
        lastLoginTime = aDecoder.decodeObject(forKey: "lastLoginTime") as? String
        mail = aDecoder.decodeObject(forKey: "mail") as? String
        mobile = aDecoder.decodeObject(forKey: "mobile") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        passwordExpired = aDecoder.decodeObject(forKey: "passwordExpired") as? Bool
        pinyin = aDecoder.decodeObject(forKey: "pinyin") as? String
        pinyinInitial = aDecoder.decodeObject(forKey: "pinyinInitial") as? String
        qq = aDecoder.decodeObject(forKey: "qq") as? String
        roleList = aDecoder.decodeObject(forKey: "roleList") as? [AnyObject]
        token = aDecoder.decodeObject(forKey: "token") as? String
        tokenType = aDecoder.decodeObject(forKey: "tokenType") as? String
        unique = aDecoder.decodeObject(forKey: "unique") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        weixin = aDecoder.decodeObject(forKey: "weixin") as? String
        
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if changePasswordTime != nil{
            aCoder.encode(changePasswordTime, forKey: "changePasswordTime")
        }
        if controllerList != nil{
            aCoder.encode(controllerList, forKey: "controllerList")
        }
        if createTime != nil{
            aCoder.encode(createTime, forKey: "createTime")
        }
        if distinguishedName != nil{
            aCoder.encode(distinguishedName, forKey: "distinguishedName")
        }
        if employee != nil{
            aCoder.encode(employee, forKey: "employee")
        }
        if genderType != nil{
            aCoder.encode(genderType, forKey: "genderType")
        }
        if icon != nil{
            aCoder.encode(icon, forKey: "icon")
        }
        if id != nil{
            aCoder.encode(id, forKey: "id")
        }
        if lastLoginAddress != nil{
            aCoder.encode(lastLoginAddress, forKey: "lastLoginAddress")
        }
        if lastLoginClient != nil{
            aCoder.encode(lastLoginClient, forKey: "lastLoginClient")
        }
        if lastLoginTime != nil{
            aCoder.encode(lastLoginTime, forKey: "lastLoginTime")
        }
        if mail != nil{
            aCoder.encode(mail, forKey: "mail")
        }
        if mobile != nil{
            aCoder.encode(mobile, forKey: "mobile")
        }
        if name != nil{
            aCoder.encode(name, forKey: "name")
        }
        if passwordExpired != nil{
            aCoder.encode(passwordExpired, forKey: "passwordExpired")
        }
        if pinyin != nil{
            aCoder.encode(pinyin, forKey: "pinyin")
        }
        if pinyinInitial != nil{
            aCoder.encode(pinyinInitial, forKey: "pinyinInitial")
        }
        if qq != nil{
            aCoder.encode(qq, forKey: "qq")
        }
        if roleList != nil{
            aCoder.encode(roleList, forKey: "roleList")
        }
        if token != nil{
            aCoder.encode(token, forKey: "token")
        }
        if tokenType != nil{
            aCoder.encode(tokenType, forKey: "tokenType")
        }
        if unique != nil{
            aCoder.encode(unique, forKey: "unique")
        }
        if updateTime != nil{
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        if weixin != nil{
            aCoder.encode(weixin, forKey: "weixin")
        }
        
    }
    
}




