//
//  OOConfigInfoModels.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/4/25.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import Foundation
import HandyJSON


// MARK:- OOConfigInfo
public class OOConfigInfo:NSObject,DataModel,NSCoding {
    
    @objc var images : [OOConfigImage]?
    @objc var indexPortal : String?
    @objc var indexType : String?
    @objc var nativeAppList : [OONativeApp]?
    @objc var portalList : [OOPortalInfo]?
    
    required public override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        if images != nil {
            aCoder.encode(images, forKey: "images")
        }
        if indexPortal != nil {
            aCoder.encode(indexPortal, forKey: "indexPortal")
        }
        if indexType != nil {
            aCoder.encode(indexType, forKey: "indexType")
        }
        if nativeAppList != nil {
            aCoder.encode(nativeAppList, forKey: "nativeAppList")
        }
        if portalList != nil {
            aCoder.encode(portalList, forKey: "portalList")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        images = aDecoder.decodeObject(forKey: "images") as? [OOConfigImage]
        indexPortal = aDecoder.decodeObject(forKey: "indexPortal") as? String
        indexType = aDecoder.decodeObject(forKey: "indexType") as? String
        nativeAppList = aDecoder.decodeObject(forKey: "nativeAppList") as? [OONativeApp]
        portalList = aDecoder.decodeObject(forKey: "portalList") as? [OOPortalInfo]
    }
    
    public override var description: String {
        return "OOConfigInfo"
    }
}

// MARK:- OOPortalInfo
public class OOPortalInfo:NSObject,DataModel,NSCoding {
    
    
    @objc var alias : String?
    @objc var createTime : String?
    @objc var creatorPerson : String?
    @objc var descriptionField : String?
    var enable : Bool?
    @objc var firstPage : String?
    @objc var id : String?
    @objc var lastUpdatePerson : String?
    @objc var lastUpdateTime : String?
    @objc var name : String?
    @objc var portalCategory : String?
    @objc var updateTime : String?
    
    required public override init() {
        
    }
    public func encode(with aCoder: NSCoder) {
        if alias != nil {
            aCoder.encode(alias, forKey: "alias")
        }
        if createTime != nil {
            aCoder.encode(createTime, forKey: "createTime")
        }
        if creatorPerson != nil {
            aCoder.encode(creatorPerson, forKey: "creatorPerson")
        }
        if descriptionField != nil {
            aCoder.encode(descriptionField, forKey: "descriptionField")
        }
        if enable != nil {
            aCoder.encode(enable, forKey: "enable")
        }
        if firstPage != nil {
            aCoder.encode(firstPage, forKey: "firstPage")
        }
        if id != nil {
            aCoder.encode(id, forKey: "id")
        }
        if lastUpdatePerson != nil {
            aCoder.encode(lastUpdatePerson, forKey: "lastUpdatePerson")
        }
        if lastUpdateTime != nil {
            aCoder.encode(lastUpdateTime, forKey: "lastUpdateTime")
        }
        if name != nil {
            aCoder.encode(name, forKey: "name")
        }
        if portalCategory != nil {
            aCoder.encode(portalCategory, forKey: "portalCategory")
        }
        if updateTime != nil {
            aCoder.encode(updateTime, forKey: "updateTime")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        alias = aDecoder.decodeObject(forKey: "alias") as? String
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        creatorPerson = aDecoder.decodeObject(forKey: "creatorPerson") as? String
        descriptionField = aDecoder.decodeObject(forKey: "descriptionField") as? String
        alias = aDecoder.decodeObject(forKey: "alias") as? String
        enable = aDecoder.decodeObject(forKey: "enable") as? Bool
        firstPage = aDecoder.decodeObject(forKey: "firstPage") as? String
        id = aDecoder.decodeObject(forKey: "id") as? String
        lastUpdatePerson = aDecoder.decodeObject(forKey: "lastUpdatePerson") as? String
        lastUpdateTime = aDecoder.decodeObject(forKey: "lastUpdateTime") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        portalCategory = aDecoder.decodeObject(forKey: "portalCategory") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        
    }
    
    public override var description: String {
        return "OOPortalInfo"
    }
}

// MARK:- OONativeApp
public class OONativeApp:NSObject,DataModel,NSCoding {
    
    
    var enable : Bool?
    @objc var iOS : OOiOS?
    var id : Int?
    @objc var key : String?
    @objc var name : String?
    required public override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        if enable != nil {
            aCoder.encode(enable, forKey: "enable")
        }
        if iOS != nil {
            aCoder.encode(iOS, forKey: "iOS")
        }
        if id != nil {
            aCoder.encode(id, forKey: "id")
        }
        if key != nil {
            aCoder.encode(key, forKey: "key")
        }
        if name != nil {
            aCoder.encode(name, forKey: "name")
        }
    }
    
    public required init?(coder aDecoder: NSCoder) {
        enable = aDecoder.decodeObject(forKey: "enable") as? Bool
        iOS = aDecoder.decodeObject(forKey: "iOS") as? OOiOS
        id = aDecoder.decodeObject(forKey: "id") as? Int
        key = aDecoder.decodeObject(forKey: "key") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
    }
    
    public override var description: String {
        return ""
    }
}

// MARK:- OOiOS
public class OOiOS:NSObject,DataModel,NSCoding {
    
    
    
    @objc var category : String?
    @objc var storyboard : String?
    @objc var subcategory : String?
    @objc var vcname : String?
    
    required public override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        if category != nil {
            aCoder.encode(category, forKey: "category")
        }
        
        if storyboard != nil {
            aCoder.encode(storyboard, forKey: "storyboard")
        }
        
        if subcategory != nil {
            aCoder.encode(subcategory, forKey: "subcategory")
        }
        
        if vcname != nil {
            aCoder.encode(vcname, forKey: "vcname")
        }
    }
    
    public  required init?(coder aDecoder: NSCoder) {
        
        category = aDecoder.decodeObject(forKey: "category") as? String
    
        storyboard = aDecoder.decodeObject(forKey: "storyboard") as? String
        
        subcategory = aDecoder.decodeObject(forKey: "subcategory") as? String
        
        vcname = aDecoder.decodeObject(forKey: "vcname") as? String
    }
    
    public override var description: String {
        return "OOiOS"
    }
    
}


// MARK:- OOConfigImage
public class OOConfigImage:NSObject,DataModel,NSCoding {
    
    @objc var name : String?
    
    @objc var value : String?
    
    public required init?(coder aDecoder: NSCoder) {
        
        name = aDecoder.decodeObject(forKey: "name") as? String
        
        value = aDecoder.decodeObject(forKey: "value") as? String
    }
    
    required public override init() {
        
    }
    
    public func encode(with aCoder: NSCoder) {
        
        if name != nil {
            aCoder.encode(name, forKey: "name")
        }
        
        if value != nil {
            aCoder.encode(value, forKey: "value")
        }
    }
    
    
    
    public override var description: String {
        return "OOConfigImage"
    }
    
}


