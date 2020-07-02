//
//  Application.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Application:Mappable{
    var id:String?
    var name:String?
    var alias:String?
    var applicationCategory:String?
    var icon:String?
    var processList:[AppProcess]?
    
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        name <- map["name"]
        alias <- map["alias"]
        applicationCategory <- map["applicationCategory"]
        icon <- map["icon"]
        processList <- map["processList"]
        
    }

}
