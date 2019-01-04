//
//  Group.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Group:Mappable{
    
    var name:String?
    var display:String?
    var groupList:[String]?
    var personList:[String]?
    
    required init?(map: Map) {
        
    }
    
    
    func mapping(map: Map) {
        name <- map["name"]
        display <- map["display"]
        groupList <- map["groupList"]
        personList <- map["personList"]
    }
}
