//
//  Identity.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/8.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Identity:Mappable{
    var name:String?
    var person:String?
    var display:String?
    var department:String?
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        person <- map["person"]
        display <- map["display"]
        department <- map["department"]
    }
}
