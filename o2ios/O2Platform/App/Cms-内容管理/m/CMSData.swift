//
//	CMSData.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class CMSData : NSObject, NSCoding, Mappable{

	var appAlias : String?
	var appIcon : String?
	var appInfoSeq : String?
	var appName : String?
	var categoryList : [AnyObject]?
	var createTime : String?
	var creatorCompany : String?
	var creatorDepartment : String?
	var creatorIdentity : String?
	var creatorPerson : String?
	var descriptionField : String?
	var distributeFactor : Int?
	var id : String?
	var sequence : String?
	var updateTime : String?
	var wrapOutCategoryList : [CMSWrapOutCategoryList]?


	class func newInstance(map: Map) -> Mappable?{
		return CMSData()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		appAlias <- map["appAlias"]
		appIcon <- map["appIcon"]
		appInfoSeq <- map["appInfoSeq"]
		appName <- map["appName"]
		categoryList <- map["categoryList"]
		createTime <- map["createTime"]
		creatorCompany <- map["creatorCompany"]
		creatorDepartment <- map["creatorDepartment"]
		creatorIdentity <- map["creatorIdentity"]
		creatorPerson <- map["creatorPerson"]
		descriptionField <- map["description"]
		distributeFactor <- map["distributeFactor"]
		id <- map["id"]
		sequence <- map["sequence"]
		updateTime <- map["updateTime"]
		wrapOutCategoryList <- map["wrapOutCategoryList"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         appAlias = aDecoder.decodeObject(forKey: "appAlias") as? String
         appIcon = aDecoder.decodeObject(forKey: "appIcon") as? String
         appInfoSeq = aDecoder.decodeObject(forKey: "appInfoSeq") as? String
         appName = aDecoder.decodeObject(forKey: "appName") as? String
         categoryList = aDecoder.decodeObject(forKey: "categoryList") as? [AnyObject]
         createTime = aDecoder.decodeObject(forKey: "createTime") as? String
         creatorCompany = aDecoder.decodeObject(forKey: "creatorCompany") as? String
         creatorDepartment = aDecoder.decodeObject(forKey: "creatorDepartment") as? String
         creatorIdentity = aDecoder.decodeObject(forKey: "creatorIdentity") as? String
         creatorPerson = aDecoder.decodeObject(forKey: "creatorPerson") as? String
         descriptionField = aDecoder.decodeObject(forKey: "description") as? String
         distributeFactor = aDecoder.decodeObject(forKey: "distributeFactor") as? Int
         id = aDecoder.decodeObject(forKey: "id") as? String
         sequence = aDecoder.decodeObject(forKey: "sequence") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
         wrapOutCategoryList = aDecoder.decodeObject(forKey: "wrapOutCategoryList") as? [CMSWrapOutCategoryList]

	}

    /**
    * NSCoding required method.
    * Encodes mode properties into the decoder
    */
    @objc func encode(with aCoder: NSCoder)
	{
		if appAlias != nil{
			aCoder.encode(appAlias, forKey: "appAlias")
		}
		if appIcon != nil{
			aCoder.encode(appIcon, forKey: "appIcon")
		}
		if appInfoSeq != nil{
			aCoder.encode(appInfoSeq, forKey: "appInfoSeq")
		}
		if appName != nil{
			aCoder.encode(appName, forKey: "appName")
		}
		if categoryList != nil{
			aCoder.encode(categoryList, forKey: "categoryList")
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
		if descriptionField != nil{
			aCoder.encode(descriptionField, forKey: "description")
		}
		if distributeFactor != nil{
			aCoder.encode(distributeFactor, forKey: "distributeFactor")
		}
		if id != nil{
			aCoder.encode(id, forKey: "id")
		}
		if sequence != nil{
			aCoder.encode(sequence, forKey: "sequence")
		}
		if updateTime != nil{
			aCoder.encode(updateTime, forKey: "updateTime")
		}
		if wrapOutCategoryList != nil{
			aCoder.encode(wrapOutCategoryList, forKey: "wrapOutCategoryList")
		}

	}

}
