//
//  CompanyDuty.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/7.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

class CompanyDuty:Mappable {
    var name:String?
    var identityList:[String]?
    var company:String?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        identityList <- map["identityList"]
        company <- map["company"]
    }
}
