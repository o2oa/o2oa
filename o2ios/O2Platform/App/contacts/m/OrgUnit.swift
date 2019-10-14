//
//  OrgUnit.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/10.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class OrgUnit:Mappable{
    
    var controllerList : [AnyObject]?
    var createTime : String?
    var descriptionField : String?
    var distinguishedName : String?
    var id : String?
    var inheritedControllerList : [AnyObject]?
    var level : Int?
    var levelName : String?
    var name : String?
    var orderNumber : Int?
    var pinyin : String?
    var pinyinInitial : String?
    var shortName : String?
    var subDirectIdentityCount : Int = 0
    var subDirectUnitCount : Int = 0
    var superior : String?
    var typeList : [String]?
    var unique : String?
    var updateTime : String?
    
    required init?(map: Map){}
    
    func mapping(map: Map)
    {
        controllerList <- map["controllerList"]
        createTime <- map["createTime"]
        descriptionField <- map["description"]
        distinguishedName <- map["distinguishedName"]
        id <- map["id"]
        inheritedControllerList <- map["inheritedControllerList"]
        level <- map["level"]
        levelName <- map["levelName"]
        name <- map["name"]
        orderNumber <- map["orderNumber"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        shortName <- map["shortName"]
        subDirectIdentityCount <- map["subDirectIdentityCount"]
        subDirectUnitCount <- map["subDirectUnitCount"]
        superior <- map["superior"]
        typeList <- map["typeList"]
        unique <- map["unique"]
        updateTime <- map["updateTime"]
        
    }
}
