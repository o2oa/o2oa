//
//  AppProcess.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
import HandyJSON


class AppProcess: NSObject, DataModel  {
    @objc var id:String?
    @objc var name:String?
    @objc var alias:String?
    @objc var desc:String?
    @objc var creatorPerson:String?
    @objc var application:String?
    @objc var icon:String?
    @objc var defaultStartMode: String?
    
    required override init(){}
    
    func mapping(map: Map) {
        id <- map["id"]
        name <- map["name"]
        alias <- map["alias"]
        desc <- map["description"]
        creatorPerson <- map["creatorPerson"]
        application <- map["application"]
        icon <- map["icon"]
        defaultStartMode <- map["defaultStartMode"]
    }

}
