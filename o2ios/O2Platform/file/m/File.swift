//
//  File.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/9/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
import SwiftyJSON

//typedef enum : NSUInteger {
//    kFile = 0,
//    kFolder = 1
//} ZLFileType;
//
//@interface ZLFile : NSObject
//
//@property (nonatomic,copy) NSString *id;
//
//@property(nonatomic,copy) NSString *createTime;
//
//@property (nonatomic,copy) NSString *updateTime;
//
//@property (nonatomic,copy) NSString *person;
//
//@property (nonatomic,copy) NSString* name;
//
//@property (nonatomic,copy) NSString* fileName;
//
//@property (nonatomic,copy) NSString *extension;
//
//@property (nonatomic,copy) NSString *attachmentType;
//
//@property (nonatomic,copy) NSString *typeValue;
//
//@property (nonatomic,assign) long length;
//
//@property (nonatomic,copy) NSString *folder;
//
//@property (nonatomic,copy) NSString *lastUpdateTime;
//
//@property (nonatomic,copy) NSString *lastUpdatePerson;
//
//@property (nonatomic,copy) NSString* superior;
//
//@property (nonatomic,strong) NSArray *shareList;
//
//@property (nonatomic,assign) BOOL shareUpdatable;
//
//@property (nonatomic,assign) BOOL isCurrentFile;
//
//@property (nonatomic,assign) ZLFileType fileType;
//
//@end

public enum OOFileType {
    case file
    case folder
}


class OOFile:Mappable{
    var id:String?
    var createTime:String?
    var updateTime:String?
    var person:String?
    var name:String?
    var fileName:String?
    var extend:String?
    var attachmentType:String?
    var typeValue:String?
    var long:Int?
    var folder:String?
    var lastUpdateTime:String?
    var lastUpdatePerson:String?
    var superior:String?
    var shareList:[String]?
    var shareUpdatable:Bool?
    var isCurrentFile:Bool = false
    var fileType:OOFileType = .file
    
    init(){
        id = "0"
        name = "根目录"
        folder = "root"
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        createTime <- map["createTime"]
        updateTime <- map["updateTime"]
        person <- map["person"]
        name <- map["name"]
        fileName <- map["fileName"]
        extend <- map["extension"]
        attachmentType <- map["attachmentType"]
        typeValue <- map["typeValue"]
        long <- map["long"]
        folder <- map["folder"]
        lastUpdateTime <- map["lastUpdateTime"]
        lastUpdatePerson <- map["lastUpdatePerson"]
        superior <- map["superior"]
        shareList <- map["shareList"]
        shareUpdatable <- map["shareUpdatable"]
        if let _ = folder {
            fileType = .file
        }
        if let _ =  superior {
            fileType = .folder
        }
    
    }
}

class FileShare:Mappable {
    var name:String?
    var value:String?
    var count:Int?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        name <- map["name"]
        value <- map["value"]
        count <- map["count"]
    }
}



