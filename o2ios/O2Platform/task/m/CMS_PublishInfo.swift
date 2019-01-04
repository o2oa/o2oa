//
//	CMS_PublishInfo.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class CMS_PublishInfo : NSObject, NSCoding, Mappable{

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


	class func newInstance(map: Map) -> Mappable?{
		return CMS_PublishInfo()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		appId <- map["appId"]
		appName <- map["appName"]
		attachmentList <- map["attachmentList"]
		categoryAlias <- map["categoryAlias"]
		categoryId <- map["categoryId"]
		categoryName <- map["categoryName"]
		createTime <- map["createTime"]
		creatorCompany <- map["creatorCompany"]
		creatorDepartment <- map["creatorDepartment"]
		creatorIdentity <- map["creatorIdentity"]
		creatorPerson <- map["creatorPerson"]
		distributeFactor <- map["distributeFactor"]
		docStatus <- map["docStatus"]
		form <- map["form"]
		formName <- map["formName"]
		id <- map["id"]
		publishTime <- map["publishTime"]
		readFormId <- map["readFormId"]
		readFormName <- map["readFormName"]
		sequence <- map["sequence"]
		title <- map["title"]
		updateTime <- map["updateTime"]
		viewCount <- map["viewCount"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         appId = aDecoder.decodeObject(forKey: "appId") as? String
         appName = aDecoder.decodeObject(forKey: "appName") as? String
         attachmentList = aDecoder.decodeObject(forKey: "attachmentList") as? [String]
         categoryAlias = aDecoder.decodeObject(forKey: "categoryAlias") as? String
         categoryId = aDecoder.decodeObject(forKey: "categoryId") as? String
         categoryName = aDecoder.decodeObject(forKey: "categoryName") as? String
         createTime = aDecoder.decodeObject(forKey: "createTime") as? String
         creatorCompany = aDecoder.decodeObject(forKey: "creatorCompany") as? String
         creatorDepartment = aDecoder.decodeObject(forKey: "creatorDepartment") as? String
         creatorIdentity = aDecoder.decodeObject(forKey: "creatorIdentity") as? String
         creatorPerson = aDecoder.decodeObject(forKey: "creatorPerson") as? String
         distributeFactor = aDecoder.decodeObject(forKey: "distributeFactor") as? Int
         docStatus = aDecoder.decodeObject(forKey: "docStatus") as? String
         form = aDecoder.decodeObject(forKey: "form") as? String
         formName = aDecoder.decodeObject(forKey: "formName") as? String
         id = aDecoder.decodeObject(forKey: "id") as? String
         publishTime = aDecoder.decodeObject(forKey: "publishTime") as? String
         readFormId = aDecoder.decodeObject(forKey: "readFormId") as? String
         readFormName = aDecoder.decodeObject(forKey: "readFormName") as? String
         sequence = aDecoder.decodeObject(forKey: "sequence") as? String
         title = aDecoder.decodeObject(forKey: "title") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
         viewCount = aDecoder.decodeObject(forKey: "viewCount") as? Int

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
		if appName != nil{
			aCoder.encode(appName, forKey: "appName")
		}
		if attachmentList != nil{
			aCoder.encode(attachmentList, forKey: "attachmentList")
		}
		if categoryAlias != nil{
			aCoder.encode(categoryAlias, forKey: "categoryAlias")
		}
		if categoryId != nil{
			aCoder.encode(categoryId, forKey: "categoryId")
		}
		if categoryName != nil{
			aCoder.encode(categoryName, forKey: "categoryName")
		}
		if createTime != nil{
			aCoder.encode(createTime, forKey: "createTime")
		}
		if creatorCompany != nil{
			aCoder.encode(creatorCompany, forKey: "creatorCompany")
		}
		if creatorDepartment != nil{
			aCoder.encode(creatorDepartment, forKey: "creatorDepartment")
		}
		if creatorIdentity != nil{
			aCoder.encode(creatorIdentity, forKey: "creatorIdentity")
		}
		if creatorPerson != nil{
			aCoder.encode(creatorPerson, forKey: "creatorPerson")
		}
		if distributeFactor != nil{
			aCoder.encode(distributeFactor, forKey: "distributeFactor")
		}
		if docStatus != nil{
			aCoder.encode(docStatus, forKey: "docStatus")
		}
		if form != nil{
			aCoder.encode(form, forKey: "form")
		}
		if formName != nil{
			aCoder.encode(formName, forKey: "formName")
		}
		if id != nil{
			aCoder.encode(id, forKey: "id")
		}
		if publishTime != nil{
			aCoder.encode(publishTime, forKey: "publishTime")
		}
		if readFormId != nil{
			aCoder.encode(readFormId, forKey: "readFormId")
		}
		if readFormName != nil{
			aCoder.encode(readFormName, forKey: "readFormName")
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
		if viewCount != nil{
			aCoder.encode(viewCount, forKey: "viewCount")
		}

	}

}
