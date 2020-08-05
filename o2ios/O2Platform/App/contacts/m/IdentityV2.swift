//
//  IdentityV2.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/9.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class IdentityV2:NSObject,Mappable{
    var createTime : String?
    var department : String?
    var descriptionField : String?
    var distinguishedName : String?
    var id : String?
    var name : String?
    var person : String?
    var pinyin : String?
    var pinyinInitial : String?
    var unique : String?
    var unit : String?
    var unitLevel : Int?
    var unitLevelName : String?
    var major: Bool? //是否主身份
    var unitName : String?
    var updateTime : String?
    var orderNumber : Int?
    var woUnit : OrgUnit?
    //var woPerson:[AnyObject]?
    //var woUnitDutyList : [AnyObject]?
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        createTime <- map["createTime"]
        department <- map["department"]
        descriptionField <- map["description"]
        distinguishedName <- map["distinguishedName"]
        id <- map["id"]
        name <- map["name"]
        person <- map["person"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        unique <- map["unique"]
        unit <- map["unit"]
        unitLevel <- map["unitLevel"]
        unitLevelName <- map["unitLevelName"]
        major <- map["major"]
        unitName <- map["unitName"]
        updateTime <- map["updateTime"]
        orderNumber <- map["orderNumber"]
        woUnit <- map["woUnit"]
    }
    
    public static func ==(lhs: IdentityV2, rhs: IdentityV2) -> Bool {
        return lhs.distinguishedName == rhs.distinguishedName
    }
    
    override var description: String {
        return "\(name!)(\(unitName!))"
    }
    
    
}
