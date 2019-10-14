//
//  CreateProcessBean.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/29.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class CreateProcessBean:Mappable {
    var title:String?
    var identity:String?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        title <- map["title"]
        identity <- map["identity"]
    }
}
