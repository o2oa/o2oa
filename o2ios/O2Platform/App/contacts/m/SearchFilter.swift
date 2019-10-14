//
//  SearchFilter.swift
//  O2Platform
//
//  Created by 程剑 on 2017/7/15.
//  Copyright © 2017年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class SearchFilter:Mappable {
    var key : String?
    var groupList : [String] = []
    var roleList : [String] = []
    
    init(key : String, groupList : [String] = [], roleList : [String] = []) {
        self.key = key
        self.groupList = groupList
        self.roleList = roleList
        
    }
    
    required init?(map: Map){}
    
    init(){}
    
    func mapping(map: Map)
    {
        groupList <- map["groupList"]
        key <- map["key"]
        roleList <- map["roleList"]
        
    }
}
