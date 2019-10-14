//
//  Person.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/7.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

class Person:Mappable {
    
    var name:String?
    var id:String?
    var pinyin:String?
    var pinyinInitial:String?
    var employee:String?
    var unique:String?
    var genderType:String?
    var controllerList:[String]?
    var display:String?
    var mail:String?
    var weixin:String?
    var qq:String?
    var weibo:String?
    var mobile:String?
    var icon:String?
    
    init () {
        
    }
    
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        id <- map["id"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        unique <- map["unique"]
        genderType <- map["genderType"]
        display <- map["display"]
        mail <- map["mail"]
        weixin <- map["weixin"]
        qq <- map["qq"]
        weibo <- map["weibo"]
        mobile <- map["mobile"]
        icon <- map["icon"]
        controllerList <- map["controllerList"]
    }

}
