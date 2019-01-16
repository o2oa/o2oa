//
//  Company.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Company:Mappable {
    var name:String?
    var display:String?
    var superior:String?
    var companyCount:Int?
    var departmentCount:Int?
    var companyList:[String]?
    var departmentList:[String]?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        display <- map["display"]
        superior <- map["superior"]
        companyCount <- map["compnayCount"]
        departmentCount <- map["departmentCount"]
        companyList <- map["companyList"]
        departmentList <- map["departmentList"]
    }
    
}
