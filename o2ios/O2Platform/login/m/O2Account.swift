//
//	O2Account.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport
import Foundation
import ObjectMapper


class O2Account : NSObject, NSCoding, Mappable{
    
    var autoLogin : Bool?
    var changePasswordTime : String?
    var controllerList : [AnyObject]?
    var createTime : String?
    var distinguishedName : String?
    var employee : String?
    var genderType : String?
    var icon : String?
    var id : String?
    var lastLoginAddress : String?
    var lastLoginClient : String?
    var lastLoginTime : String?
    var mail : String?
    var mobile : String?
    var name : String?
    var pinyin : String?
    var pinyinInitial : String?
    var qq : String?
    var roleList : [AnyObject]?
    var token : String?
    var tokenType : String?
    var unique : String?
    var updateTime : String?
    var weixin : String?
    
    
    class func newInstance(map: Map) -> Mappable?{
        return O2Account()
    }
    required init?(map: Map){}
    private override init(){}
    
    func mapping(map: Map)
    {
        autoLogin <- map["autoLogin"]
        changePasswordTime <- map["changePasswordTime"]
        controllerList <- map["controllerList"]
        createTime <- map["createTime"]
        distinguishedName <- map["distinguishedName"]
        employee <- map["employee"]
        genderType <- map["genderType"]
        icon <- map["icon"]
        id <- map["id"]
        lastLoginAddress <- map["lastLoginAddress"]
        lastLoginClient <- map["lastLoginClient"]
        lastLoginTime <- map["lastLoginTime"]
        mail <- map["mail"]
        mobile <- map["mobile"]
        name <- map["name"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        qq <- map["qq"]
        roleList <- map["roleList"]
        token <- map["token"]
        tokenType <- map["tokenType"]
        unique <- map["unique"]
        updateTime <- map["updateTime"]
        weixin <- map["weixin"]
        
    }
    
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        autoLogin = aDecoder.decodeObject(forKey: "autoLogin") as? Bool
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
        if autoLogin != nil{
            aCoder.encode(autoLogin, forKey: "autoLogin")
        }
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
