//
//  account.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Account:NSObject,NSCoding,Mappable{
    var id:String?
    var name:String?
    var passwd:String?
    var roleList:[String]?
    var token:String?
    var authentication:Bool?
    var autoLogin:Bool?
    
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("Account")

    
    struct  AccountKey {
        static let idKey = "id"
        static let nameKey  = "name"
        static let passwdKey = "passwd"
        static let roleListKey = "roleList"
        static let tokenKey = "token"
        static let authenticationKey = "authentication"
        static let autoLoginKey = "autoLogin"
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        name <- map["name"]
        passwd <- map["passwd"]
        roleList <- map["roleList"]
        token <- map["token"]
    }
    
    required  init(id:String?,name:String?,passwd:String?,roleList:[String]?,token:String?,authentication:Bool?,autoLogin:Bool?){
        self.id = id
        self.name = name
        self.passwd = passwd
        self.roleList = roleList
        self.token = token
        self.authentication = authentication
        self.autoLogin = autoLogin
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        let id = aDecoder.decodeObject(forKey: AccountKey.idKey) as? String
        let name = aDecoder.decodeObject(forKey: AccountKey.nameKey) as? String
        let passwd = aDecoder.decodeObject(forKey: AccountKey.passwdKey) as? String
        let roleList = aDecoder.decodeObject(forKey: AccountKey.roleListKey) as? [String]
        let token = aDecoder.decodeObject(forKey: AccountKey.tokenKey) as? String
        let authentication = aDecoder.decodeBool(forKey: AccountKey.authenticationKey)
        let autoLogin = aDecoder.decodeBool(forKey: AccountKey.autoLoginKey)
        self.init(id:id,name:name,passwd:passwd,roleList:roleList,token:token,authentication:authentication,autoLogin:autoLogin)
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id,forKey: AccountKey.idKey)
        aCoder.encode(name,forKey: AccountKey.nameKey)
        aCoder.encode(passwd,forKey: AccountKey.passwdKey)
        aCoder.encode(roleList,forKey: AccountKey.roleListKey)
        aCoder.encode(token,forKey: AccountKey.tokenKey)
        aCoder.encode(authentication!,forKey: AccountKey.authenticationKey)
        aCoder.encode(autoLogin!,forKey: AccountKey.autoLoginKey)
    }
    
    static func saveAccount(_ account:Account){
        let saveData = NSKeyedArchiver.archiveRootObject(account, toFile:Account.ArchiveURL.path)
        if !saveData {
            print("save Account Fail")
        }
    }
    
    static func currentAccount() -> Account? {
        return NSKeyedUnarchiver.unarchiveObject(withFile: Account.ArchiveURL.path) as? Account
    }
    
  
    
}
