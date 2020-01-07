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
    @objc var contentType : String?
    @objc var createTime : String?
    @objc var editorList : [String]?
    @objc var `extension` : String?
    @objc var folder : String?
    @objc var id : String?
    @objc var lastUpdatePerson : String?
    @objc var lastUpdateTime : String?
    var length : Int?
    @objc var name : String?
    @objc var person : String?
    @objc var shareList : [String]?
    @objc var storage : String?
    @objc var updateTime : String?
    @objc var type: String?
    
    @objc var fileId: String? //分享对象的时候这个代表文件原始id
    
    override required init() {
        
    }
}

// MARK:- 文件夹model
class OOFolder:NSObject,DataModel {
    
    @objc var createTime : String?
    @objc var id : String?
    @objc var name : String?
    @objc var person : String?
    @objc var superior : String?
    @objc var updateTime : String?
    var attachmentCount: Int?
    var size: Int?
    var folderCount: Int?
    @objc var status: String?
    
    @objc var fileId: String? //分享对象的时候这个代表文件原始id
    
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

//分享提交对象
class OOShareForm: NSObject,DataModel {
    @objc var shareType : String? //分享类型 member
    @objc var fileId : String? //分享的文档id或者文件夹id
    @objc var shareUserList : [String]? //分享给的用户列表
    @objc var shareOrgList : [String]? //分享给的组织列表
    
    required override init() {
    }
}

