//
//  Module.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class Module:Mappable{
    var createTime:String?
    var updateTime:String?
    var id:String?
    var name:String?
    var alias:String?
    var description:String?
    var application:String?
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        createTime <- map["createTime"]
        updateTime <- map["updateTime"]
        id <- map["id"]
        name <- map["name"]
        alias <- map["alias"]
        description <- map["description"]
        application <- map["application"]
    }
}



struct HotpicEntity:Mappable{
    var application:String = "CMS|BBS"
    var infoId:String!
    var title:String!
    init?(map: Map) {
        
    }
    
    mutating func mapping(map: Map) {
        application<-map["application"]
        infoId<-map["infoId"]
        title<-map["title"]
    }
}

//
//	TaskImageshowEntity.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport
//滚动图片实体

class TaskImageshowEntity : NSObject, NSCoding, Mappable{
    
    var application : String?
    var createTime : String?
    var creator : String?
    var distributeFactor : Int?
    var id : String?
    var infoId : String?
    var picUrl : String?
    var sequence : String?
    var title : String?
    var updateTime : String?
    var url : String?
    var picId:String?
    
    
    class func newInstance(map: Map) -> Mappable?{
        return TaskImageshowEntity()
    }
    required init?(map: Map){}
    private override init(){}
    
    func mapping(map: Map)
    {
        application <- map["application"]
        createTime <- map["createTime"]
        creator <- map["creator"]
        distributeFactor <- map["distributeFactor"]
        id <- map["id"]
        infoId <- map["infoId"]
        picUrl <- map["picUrl"]
        sequence <- map["sequence"]
        title <- map["title"]
        updateTime <- map["updateTime"]
        url <- map["url"]
        picId <- map["picId"]
        
    }
    
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        application = aDecoder.decodeObject(forKey: "application") as? String
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        creator = aDecoder.decodeObject(forKey: "creator") as? String
        distributeFactor = aDecoder.decodeObject(forKey: "distributeFactor") as? Int
        id = aDecoder.decodeObject(forKey: "id") as? String
        infoId = aDecoder.decodeObject(forKey: "infoId") as? String
        picUrl = aDecoder.decodeObject(forKey: "picUrl") as? String
        sequence = aDecoder.decodeObject(forKey: "sequence") as? String
        title = aDecoder.decodeObject(forKey: "title") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        url = aDecoder.decodeObject(forKey: "url") as? String
        picId = aDecoder.decodeBool(forKey: "picId") as? String
        
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if application != nil{
            aCoder.encode(application, forKey: "application")
        }
        if createTime != nil{
            aCoder.encode(createTime, forKey: "createTime")
        }
        if creator != nil{
            aCoder.encode(creator, forKey: "creator")
        }
        if distributeFactor != nil{
            aCoder.encode(distributeFactor, forKey: "distributeFactor")
        }
        if id != nil{
            aCoder.encode(id, forKey: "id")
        }
        if infoId != nil{
            aCoder.encode(infoId, forKey: "infoId")
        }
        if picUrl != nil{
            aCoder.encode(picUrl, forKey: "picUrl")
        }
        if sequence != nil{
            aCoder.encode(sequence, forKey: "sequence")
        }
        if title != nil{
            aCoder.encode(title, forKey: "title")
        }
        if updateTime != nil{
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        if url != nil{
            aCoder.encode(url, forKey: "url")
        }
        if picId != nil{
            aCoder.encode(picId, forKey: "picId")
        }
        
    }
    
}

//任务分页分页模型
struct CommonPageModel {
    var pageNumber:Int = 0
    var pageSize:Int = 20
    var pageTotal:Int = 0
    var nextPageId:String = "(0)"
    
    init() {
        
    }
    
    init(_ pageSize:Int) {
        self.pageSize = pageSize
    }
    
    mutating func setPageTotal(_ pageTotal:Int){
        self.pageTotal = pageTotal
    }
    
    mutating func nextPage(){
        if pageTotal > 0 && pageNumber * pageSize < pageTotal {
            pageNumber += 1
        }else{
            
        }
    }
    
    func toDictionary()->Dictionary<String,String>{
        return ["##id##":nextPageId,"##count##":pageSize.toString]
    }
    
    func isLast() -> Bool {
        return  pageNumber * pageSize >= pageTotal
    }
}
