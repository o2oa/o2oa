//
//	CMSCategoryItemData.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class CMSCategoryItemData : NSObject, NSCoding, Mappable{

	var appId : String?
	var attachmentList : [AnyObject]?
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
	var sequence : String?
	var title : String?
	var updateTime : String?
    var publishTime : String?


	class func newInstance(map: Map) -> Mappable?{
		return CMSCategoryItemData()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		appId <- map["appId"]
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
		sequence <- map["sequence"]
		title <- map["title"]
		updateTime <- map["updateTime"]
        publishTime <- map["publishTime"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         appId = aDecoder.decodeObject(forKey: "appId") as? String
         attachmentList = aDecoder.decodeObject(forKey: "attachmentList") as? [AnyObject]
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
         sequence = aDecoder.decodeObject(forKey: "sequence") as? String
         title = aDecoder.decodeObject(forKey: "title") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
         publishTime = aDecoder.decodeObject(forKey: "publishTime") as? String

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
		if sequence != nil{
			aCoder.encode(sequence, forKey: "sequence")
		}
		if title != nil{
			aCoder.encode(title, forKey: "title")
		}
		if updateTime != nil{
			aCoder.encode(updateTime, forKey: "updateTime")
		}
        if publishTime != nil{
            aCoder.encode(publishTime, forKey: "publishTime")
        }

	}

}
