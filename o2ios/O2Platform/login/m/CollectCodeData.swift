//
//  CollectCodeData.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
//手机验证码提交参数
class CollectCodeData:Mappable {
    var mobile:String?
    var value:String?
    var meta:String?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        mobile <- map["mobile"]
        value <- map["value"]
        meta <- map["meta"]
    }
}
