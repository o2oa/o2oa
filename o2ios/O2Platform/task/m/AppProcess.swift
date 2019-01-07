//
//  AppProcess.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class AppProcess:Mappable {
    var id:String?
    var name:String?
    var alias:String?
    var description:String?
    var creatorPerson:String?
    var application:String?
    var icon:String?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        name <- map["name"]
        alias <- map["alias"]
        description <- map["description"]
        creatorPerson <- map["creatorPerson"]
        application <- map["application"]
        icon <- map["icon"]
    }

}
