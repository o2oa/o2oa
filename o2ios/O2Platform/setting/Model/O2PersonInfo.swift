//
//  O2PersonInfo.swift
//  O2Platform
//
//  Created by FancyLou on 2018/11/21.
//  Copyright © 2018 zoneland. All rights reserved.
//

import HandyJSON


class O2PersonInfo: NSObject, DataModel  {
    @objc var changePasswordTime : String?
    @objc var controllerList : [String]?
    @objc var createTime : String?
    @objc var distinguishedName : String?
    @objc var employee : String?
    @objc var genderType : String?
    @objc var id : String?
    @objc var lastLoginAddress : String?
    @objc var lastLoginClient : String?
    @objc var lastLoginTime : String?
    @objc var mail : String?
    @objc var mobile : String?
    @objc var name : String?
    @objc var superior : String?
    @objc var signature : String?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var qq : String?
    @objc var unique : String?
    @objc var updateTime : String?
    @objc var weixin : String?
    @objc var boardDate : String?
    @objc var birthday : String?
//    var woGroupList : [AnyObject]?
    @objc var woIdentityList : [O2IdentityInfo]?
//    var woPersonAttributeList : [AnyObject]?
//    var woRoleList : [AnyObject]?
    
    required override init(){}
    
     
}

class O2IdentityInfo: NSObject, DataModel {
    @objc var createTime : String?
    @objc var distinguishedName : String?
    @objc var id : String?
    @objc var name : String?
    @objc var person : String?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var unique : String?
    @objc var unit : String?
    var unitLevel : Int?
    @objc var unitLevelName : String?
    @objc var unitName : String?
    @objc var updateTime : String?
    var orderNumber : Int?
    @objc var woUnit : O2OrgUnit?
    required override init(){}
}

class O2OrgUnit: NSObject, DataModel {
    @objc var createTime : String?
    @objc var distinguishedName : String?
    @objc var id : String?
    var level : Int?
    @objc var levelName : String?
    @objc var name : String?
    var orderNumber : Int?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var superior : String?
    @objc var typeList : [String]?
    @objc var unique : String?
    @objc var updateTime : String?
    required override init(){}
}
