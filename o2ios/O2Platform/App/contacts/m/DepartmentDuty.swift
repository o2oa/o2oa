//
//  DepartmentDuty.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

class DepartmentDuty:Mappable {
    var name:String?
    var identityList:[String]?
    var department:String?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        identityList <- map["identityList"]
        department <- map["department"]
    }
}
