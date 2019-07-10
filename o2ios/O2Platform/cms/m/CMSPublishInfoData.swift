//
//	CMSData.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class CMSPublishInfoData : NSObject, NSCoding, Mappable{

	var appId : String?
	var categoryId : String?
	var createTime : String?
	var creatorUid : String?
	var distributeFactor : Int?
	var documentId : String?
	var `extension` : String?
	var fileHost : String?
	var fileName : String? //后台存储的文件名称
	var filePath : String?
	var fileType : String?
	var id : String?
	var lastUpdateTime : String?
	var length : Int?
	var name : String? //原文件名称
	var sequence : String?
	var site : String?
	var storage : String?
	var updateTime : String?


	class func newInstance(map: Map) -> Mappable?{
		return CMSPublishInfoData()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		appId <- map["appId"]
		categoryId <- map["categoryId"]
		createTime <- map["createTime"]
		creatorUid <- map["creatorUid"]
		distributeFactor <- map["distributeFactor"]
		documentId <- map["documentId"]
		`extension` <- map["extension"]
		fileHost <- map["fileHost"]
		fileName <- map["fileName"]
		filePath <- map["filePath"]
		fileType <- map["fileType"]
		id <- map["id"]
		lastUpdateTime <- map["lastUpdateTime"]
		length <- map["length"]
		name <- map["name"]
		sequence <- map["sequence"]
		site <- map["site"]
		storage <- map["storage"]
		updateTime <- map["updateTime"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         appId = aDecoder.decodeObject(forKey: "appId") as? String
         categoryId = aDecoder.decodeObject(forKey: "categoryId") as? String
         createTime = aDecoder.decodeObject(forKey: "createTime") as? String
         creatorUid = aDecoder.decodeObject(forKey: "creatorUid") as? String
         distributeFactor = aDecoder.decodeObject(forKey: "distributeFactor") as? Int
         documentId = aDecoder.decodeObject(forKey: "documentId") as? String
         `extension` = aDecoder.decodeObject(forKey: "extension") as? String
         fileHost = aDecoder.decodeObject(forKey: "fileHost") as? String
         fileName = aDecoder.decodeObject(forKey: "fileName") as? String
         filePath = aDecoder.decodeObject(forKey: "filePath") as? String
         fileType = aDecoder.decodeObject(forKey: "fileType") as? String
         id = aDecoder.decodeObject(forKey: "id") as? String
         lastUpdateTime = aDecoder.decodeObject(forKey: "lastUpdateTime") as? String
         length = aDecoder.decodeObject(forKey: "length") as? Int
         name = aDecoder.decodeObject(forKey: "name") as? String
         sequence = aDecoder.decodeObject(forKey: "sequence") as? String
         site = aDecoder.decodeObject(forKey: "site") as? String
         storage = aDecoder.decodeObject(forKey: "storage") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String

	}

    /**
    * NSCoding required method.
    * Encodes mode properties into the decoder
    */
    @objc func encode(with aCoder: NSCoder)
	{
		if appId != nil{
			aCoder.encode(appId, forKey: "appId")
		}
		if categoryId != nil{
			aCoder.encode(categoryId, forKey: "categoryId")
		}
		if createTime != nil{
			aCoder.encode(createTime, forKey: "createTime")
		}
		if creatorUid != nil{
			aCoder.encode(creatorUid, forKey: "creatorUid")
		}
		if distributeFactor != nil{
			aCoder.encode(distributeFactor, forKey: "distributeFactor")
		}
		if documentId != nil{
			aCoder.encode(documentId, forKey: "documentId")
		}
		if `extension` != nil{
			aCoder.encode(`extension`, forKey: "extension")
		}
		if fileHost != nil{
			aCoder.encode(fileHost, forKey: "fileHost")
		}
		if fileName != nil{
			aCoder.encode(fileName, forKey: "fileName")
		}
		if filePath != nil{
			aCoder.encode(filePath, forKey: "filePath")
		}
		if fileType != nil{
			aCoder.encode(fileType, forKey: "fileType")
		}
		if id != nil{
			aCoder.encode(id, forKey: "id")
		}
		if lastUpdateTime != nil{
			aCoder.encode(lastUpdateTime, forKey: "lastUpdateTime")
		}
		if length != nil{
			aCoder.encode(length, forKey: "length")
		}
		if name != nil{
			aCoder.encode(name, forKey: "name")
		}
		if sequence != nil{
			aCoder.encode(sequence, forKey: "sequence")
		}
		if site != nil{
			aCoder.encode(site, forKey: "site")
		}
		if storage != nil{
			aCoder.encode(storage, forKey: "storage")
		}
		if updateTime != nil{
			aCoder.encode(updateTime, forKey: "updateTime")
		}

	}

}
