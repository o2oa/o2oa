//
//  Department.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Department:Mappable {
    var name:String?
    var display:String?
    var company:String?
    var superior:String?
    var departmentCount:Int?
    var identityCount:Int?
    var departmentList:[String]?
    var identityList:[String]?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        display <- map["display"]
        company <- map["company"]
        superior <- map["superior"]
        departmentCount <- map["departmentCount"]
        identityCount <- map["identityCount"]
        departmentList <- map["departmentList"]
        identityList <- map["identityList"]
    }
}
