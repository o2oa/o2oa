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

class CmsDocData:Mappable {
    var isNewDocument:Bool? = true
    var title:String?
    var creatorIdentity:String?
    var appId:String?
    var categoryId:String?
    var docStatus:String? = "draft"
    var createTime:String?
    var categoryName:String?
    var categoryAlias:String?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        title <- map["title"]
        isNewDocument <- map["isNewDocument"]
        creatorIdentity <- map["creatorIdentity"]
        appId <- map["appId"]
        categoryId <- map["categoryId"]
        docStatus <- map["docStatus"]
        createTime <- map["createTime"]
        categoryName <- map["categoryName"]
        categoryAlias <- map["categoryAlias"]
    }
}

class CreateProcessCmsData:Mappable {
    var cmsDocument:CmsDocData?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        cmsDocument <- map["cmsDocument"]
    }
}

class CreateProcessCmsBean:Mappable {
    var title:String?
    var identity:String?
    var data:CreateProcessCmsData?
    
    init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        title <- map["title"]
        identity <- map["identity"]
        data <- map["data"]
    }
}
