//
//  CollectUnitData.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class CollectUnitData:NSObject,NSCoding,Mappable{
    var id:String?
    var pinyin:String?
    var pinyinInitial:String?
    var name:String?//公司名称
    var centerHost:String?//对应服务端host  如 dev.platform.tech
    var centerContext:String = "jaxrs/distribute/webserver/assemble" //对应的服务端上下文  如 x_program_center
    var centerPort:Int? //对应的服务器port  如30080
    
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("CollectionUnitData")
    
    
    struct PropertyKey {
        static let idKey = "id"
        static let pinyinKey = "pinyin"
        static let pinyinInitialKey = "pinyinInitial"
        static let nameKey = "name"
        static let centerHostKey = "centerHost"
        static let centerContextKey = "centerContext"
        static let centerPortKey = "centerPort"
    }
    
    override init() {
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        pinyin <- map["pinyin"]
        pinyinInitial <- map["pinyinInitial"]
        name <- map["name"]
        centerHost <- map["centerHost"]
        centerContext <- map["centerContext"]
        centerPort <- map["centerPort"]
    }
    
    init?(id:String?,pinyin:String?,pinyinInitial:String?,name:String?,centerHost:String?,centerContext:String,centerPort:Int?){
        self.id = id
        self.pinyinInitial = pinyinInitial
        self.pinyin = pinyin
        self.name = name
        self.centerHost = centerHost
        self.centerContext = centerContext
        self.centerPort = centerPort
    }
    
    
    required  convenience init?(coder aDecoder: NSCoder) {
        let id = aDecoder.decodeObject(forKey: PropertyKey.idKey) as! String
        let pinyin = aDecoder.decodeObject(forKey: PropertyKey.pinyinKey) as! String
        let pinyinInitial = aDecoder.decodeObject(forKey: PropertyKey.pinyinInitialKey) as! String
        let name = aDecoder.decodeObject(forKey: PropertyKey.nameKey) as! String
        let centerHost = aDecoder.decodeObject(forKey: PropertyKey.centerHostKey) as! String
        let centerContext = aDecoder.decodeObject(forKey: PropertyKey.centerContextKey) as! String
        let centerPort = aDecoder.decodeObject(forKey: PropertyKey.centerPortKey) as! Int
        self.init(id:id,pinyin:pinyin,pinyinInitial:pinyinInitial,name:name,centerHost:centerHost,centerContext:centerContext,centerPort:centerPort)
        
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id,forKey:PropertyKey.idKey)
        aCoder.encode(pinyin,forKey: PropertyKey.pinyinKey)
        aCoder.encode(pinyinInitial,forKey: PropertyKey.pinyinInitialKey)
        aCoder.encode(name,forKey: PropertyKey.nameKey)
        aCoder.encode(centerHost,forKey:PropertyKey.centerHostKey)
        aCoder.encode(centerContext,forKey: PropertyKey.centerContextKey)
        aCoder.encode(centerPort,forKey: PropertyKey.centerPortKey)
    }
    
    static func saveCollectUnitData(_ unitData:CollectUnitData){
        let saveData = NSKeyedArchiver.archiveRootObject(unitData, toFile:CollectUnitData.ArchiveURL.path)
        if !saveData {
            print("save Collect Unit Data Fail")
        }
    }
    
    static func currentCollectUnitData() -> CollectUnitData? {
        return NSKeyedUnarchiver.unarchiveObject(withFile: CollectUnitData.ArchiveURL.path) as? CollectUnitData
    }
}
