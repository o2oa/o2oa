//
//  O2InformationModels.swift
//  o2app
//
//  Created by 刘振兴 on 2017/12/27.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import HandyJSON

class O2CMSAppInfo:NSObject,DataModel {
    var appAlias : String?
    var appIcon : String?
    var appInfoSeq : String?
    var appName : String?
    var categoryList : [String]?
    var creatorIdentity : String?
    var creatorPerson : String?
    var creatorTopUnitName : String?
    var creatorUnitName : String?
    var descriptionField : String?
    var id : String?
    var updateTime : String?
    var wrapOutCategoryList : [O2CMSCategoryInfo]?
    required override init() {
        
    }

}

class O2CMSCategoryInfo:NSObject,DataModel {
    var appId : String?
    var appName : String?
    var categoryAlias : String?
    var categoryName : String?
    var categorySeq : String?
    var createTime : String?
    var creatorIdentity : String?
    var creatorPerson : String?
    var formId : String?
    var formName : String?
    var id : String?
    var readFormId : String?
    var readFormName : String?
    var updateTime : String?
    var workflowType : String?
    required override init() {
        
    }
}

class O2CMSPublishInfo:NSObject,DataModel {
    var appId : String?
    var appName : String?
    var attachmentList : [String]?
    var categoryAlias : String?
    var categoryId : String?
    var categoryName : String?
    var createTime : String?
    var creatorCompany : String?
    var creatorDepartment : String?
    var creatorIdentity : String?
    var creatorPerson : String?
    var distributeFactor : Int?
    var docStatus : String?
    var form : String?
    var formName : String?
    var id : String?
    var publishTime : String?
    var readFormId : String?
    var readFormName : String?
    var sequence : String?
    var title : String?
    var updateTime : String?
    var viewCount : Int?
    
    required override init() {
        
    }
}

class O2CMSSubjectInfo:NSObject,DataModel {
    var categoryAlias : String?
    var categoryName : String?
    var createTime : String?
    var creatorIdentity : String?
    var creatorPerson : String?
    var creatorPersonShort : String?
    var creatorTopUnitName : String?
    var creatorTopUnitNameShort : String?
    var creatorUnitName : String?
    var creatorUnitNameShort : String?
    var docStatus : String?
    var hasIndexPic : Bool?
    var id : String?
    var pictureList : [String]?
    var publishTime : String?
    var summary : String?
    var title : String?
    var updateTime : String?
    var viewCount : Int?
    required override init() {
        
    }
}

class O2CMSAttachmentInfo:NSObject,DataModel {
    var appId : String?
    var categoryId : String?
    var createTime : String?
    var creatorUid : String?
    var distributeFactor : Int?
    var documentId : String?
    var `extension` : String?
    var fileHost : String?
    var fileName : String?
    var filePath : String?
    var fileType : String?
    var id : String?
    var lastUpdateTime : String?
    var length : Int?
    var name : String?
    var sequence : String?
    var site : String?
    var storage : String?
    var updateTime : String?
    required override init() {
        
    }
}
