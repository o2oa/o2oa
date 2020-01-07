//
//  O2VersionInfoModel.swift
//  O2Platform
//
//  Created by FancyLou on 2019/11/15.
//  Copyright © 2019 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

//app 版本管理对象
class O2VersionInfoModel: Mappable {
    required init?(map: Map) {}
    
    func mapping(map: Map) {
        versionName <- map["versionName"]
        buildNo <- map["buildNo"]
        downloadUrl <- map["downloadUrl"]
        content <- map["content"]
    }
    
    // app 显示版本号
    var versionName : String?
    // app build号 更新主要根据这个数字判断的
    var buildNo: String?
    // app 更新地址
    var downloadUrl: String?
    // 更新内容
    var content: String?
    
    
    
    
}
