//
//	O2Data.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class O2Application : NSObject, NSCoding, Mappable{

	var alias : String?
	var applicationCategory : String?
	var availableCompanyList : [AnyObject]?
	var availableDepartmentList : [String]?
	var availableIdentityList : [AnyObject]?
	var controllerList : [AnyObject]?
	var createTime : String?
	var creatorPerson : String?
	var descriptionField : String?
	var icon : String?
	var id : String?
	var lastUpdatePerson : String?
	var lastUpdateTime : String?
	var name : String?
	var updateTime : String?


	class func newInstance(map: Map) -> Mappable?{
		return O2Application()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		alias <- map["alias"]
		applicationCategory <- map["applicationCategory"]
		availableCompanyList <- map["availableCompanyList"]
		availableDepartmentList <- map["availableDepartmentList"]
		availableIdentityList <- map["availableIdentityList"]
		controllerList <- map["controllerList"]
		createTime <- map["createTime"]
		creatorPerson <- map["creatorPerson"]
		descriptionField <- map["description"]
		icon <- map["icon"]
		id <- map["id"]
		lastUpdatePerson <- map["lastUpdatePerson"]
		lastUpdateTime <- map["lastUpdateTime"]
		name <- map["name"]
		updateTime <- map["updateTime"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         alias = aDecoder.decodeObject(forKey: "alias") as? String
         applicationCategory = aDecoder.decodeObject(forKey: "applicationCategory") as? String
         availableCompanyList = aDecoder.decodeObject(forKey: "availableCompanyList") as? [AnyObject]
         availableDepartmentList = aDecoder.decodeObject(forKey: "availableDepartmentList") as? [String]
         availableIdentityList = aDecoder.decodeObject(forKey: "availableIdentityList") as? [AnyObject]
         controllerList = aDecoder.decodeObject(forKey: "controllerList") as? [AnyObject]
         createTime = aDecoder.decodeObject(forKey: "createTime") as? String
         creatorPerson = aDecoder.decodeObject(forKey: "creatorPerson") as? String
         descriptionField = aDecoder.decodeObject(forKey: "description") as? String
         icon = aDecoder.decodeObject(forKey: "icon") as? String
         id = aDecoder.decodeObject(forKey: "id") as? String
         lastUpdatePerson = aDecoder.decodeObject(forKey: "lastUpdatePerson") as? String
         lastUpdateTime = aDecoder.decodeObject(forKey: "lastUpdateTime") as? String
         name = aDecoder.decodeObject(forKey: "name") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String

	}

    /**
    * NSCoding required method.
    * Encodes mode properties into the decoder
    */
    @objc func encode(with aCoder: NSCoder)
	{
		if alias != nil{
			aCoder.encode(alias, forKey: "alias")
		}
		if applicationCategory != nil{
			aCoder.encode(applicationCategory, forKey: "applicationCategory")
		}
		if availableCompanyList != nil{
			aCoder.encode(availableCompanyList, forKey: "availableCompanyList")
		}
		if availableDepartmentList != nil{
			aCoder.encode(availableDepartmentList, forKey: "availableDepartmentList")
		}
		if availableIdentityList != nil{
			aCoder.encode(availableIdentityList, forKey: "availableIdentityList")
		}
		if controllerList != nil{
			aCoder.encode(controllerList, forKey: "controllerList")
		}
		if createTime != nil{
			aCoder.encode(createTime, forKey: "createTime")
		}
		if creatorPerson != nil{
			aCoder.encode(creatorPerson, forKey: "creatorPerson")
		}
		if descriptionField != nil{
			aCoder.encode(descriptionField, forKey: "description")
		}
		if icon != nil{
			aCoder.encode(icon, forKey: "icon")
		}
		if id != nil{
			aCoder.encode(id, forKey: "id")
		}
		if lastUpdatePerson != nil{
			aCoder.encode(lastUpdatePerson, forKey: "lastUpdatePerson")
		}
		if lastUpdateTime != nil{
			aCoder.encode(lastUpdateTime, forKey: "lastUpdateTime")
		}
		if name != nil{
			aCoder.encode(name, forKey: "name")
		}
		if updateTime != nil{
			aCoder.encode(updateTime, forKey: "updateTime")
		}

	}

}
