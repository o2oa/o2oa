//
//  Login.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Login:Mappable{
    
   internal var credential:String?
    internal var codeAnswer:String?
   internal var password:String?
   internal var code:String?
    
    init() {
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        credential <- map["credential"]
        codeAnswer <- map["codeAnswer"]
        password <- map["password"]
        code <- map["code"]
    }
    
    
    
    
}
