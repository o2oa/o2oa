//
//  OOFileModels.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/5.
//  Copyright © 2018年 zoneland. All rights reserved.
//
import Foundation
import HandyJSON

// MARK:- 附件model
class OOAttachment:NSObject,DataModel {
    var contentType : String?
    var createTime : String?
    var editorList : [String]?
    var `extension` : String?
    var folder : String?
    var id : String?
    var lastUpdatePerson : String?
    var lastUpdateTime : String?
    var length : Int?
    var name : String?
    var person : String?
    var shareList : [String]?
    var storage : String?
    var updateTime : String?
    
    override required init() {
        
    }
}

// MARK:- 文件夹model
class OOFolder:NSObject,DataModel {
    
    var createTime : String?
    var id : String?
    var name : String?
    var person : String?
    var superior : String?
    var updateTime : String?
    
    override required init() {
        
    }
    
}

// MARK:- 列表model
class OOFolderList:NSObject,DataModel {
    
    var attachmentList:[OOAttachment]?
    
    var folderList:[OOFolder]?
    
    override required init() {
        
    }
}

// MARK:- 给我的共享列表Model
class OOMyShareList:NSObject,DataModel {
    var count : Int?
    var name : String?
    var value : String?
    override required init() {
        
    }
}


