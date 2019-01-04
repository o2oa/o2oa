//
//	O2Data.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class O2TaskAttachmentInfoData : NSObject, NSCoding, Mappable{

	var activity : String?
	var activityName : String?
	var activityToken : String?
	var activityType : String?
	var application : String?
	var completed : Bool?
	var createTime : String?
	var distributeFactor : Int?
	var `extension` : String?
	var id : String?
	var job : String?
	var lastUpdatePerson : String?
	var lastUpdateTime : String?
	var length : Int?
	var name : String?
	var person : String?
	var process : String?
	var sequence : String?
	var site : String?
	var storage : String?
	var updateTime : String?
	var workCreateTime : String?


	class func newInstance(map: Map) -> Mappable?{
		return O2TaskAttachmentInfoData()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		activity <- map["activity"]
		activityName <- map["activityName"]
		activityToken <- map["activityToken"]
		activityType <- map["activityType"]
		application <- map["application"]
		completed <- map["completed"]
		createTime <- map["createTime"]
		distributeFactor <- map["distributeFactor"]
		`extension` <- map["extension"]
		id <- map["id"]
		job <- map["job"]
		lastUpdatePerson <- map["lastUpdatePerson"]
		lastUpdateTime <- map["lastUpdateTime"]
		length <- map["length"]
		name <- map["name"]
		person <- map["person"]
		process <- map["process"]
		sequence <- map["sequence"]
		site <- map["site"]
		storage <- map["storage"]
		updateTime <- map["updateTime"]
		workCreateTime <- map["workCreateTime"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         activity = aDecoder.decodeObject(forKey: "activity") as? String
         activityName = aDecoder.decodeObject(forKey: "activityName") as? String
         activityToken = aDecoder.decodeObject(forKey: "activityToken") as? String
         activityType = aDecoder.decodeObject(forKey: "activityType") as? String
         application = aDecoder.decodeObject(forKey: "application") as? String
         completed = aDecoder.decodeObject(forKey: "completed") as? Bool
         createTime = aDecoder.decodeObject(forKey: "createTime") as? String
         distributeFactor = aDecoder.decodeObject(forKey: "distributeFactor") as? Int
         `extension` = aDecoder.decodeObject(forKey: "extension") as? String
         id = aDecoder.decodeObject(forKey: "id") as? String
         job = aDecoder.decodeObject(forKey: "job") as? String
         lastUpdatePerson = aDecoder.decodeObject(forKey: "lastUpdatePerson") as? String
         lastUpdateTime = aDecoder.decodeObject(forKey: "lastUpdateTime") as? String
         length = aDecoder.decodeObject(forKey: "length") as? Int
         name = aDecoder.decodeObject(forKey: "name") as? String
         person = aDecoder.decodeObject(forKey: "person") as? String
         process = aDecoder.decodeObject(forKey: "process") as? String
         sequence = aDecoder.decodeObject(forKey: "sequence") as? String
         site = aDecoder.decodeObject(forKey: "site") as? String
         storage = aDecoder.decodeObject(forKey: "storage") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
         workCreateTime = aDecoder.decodeObject(forKey: "workCreateTime") as? String

	}

    /**
    * NSCoding required method.
    * Encodes mode properties into the decoder
    */
    @objc func encode(with aCoder: NSCoder)
	{
		if activity != nil{
			aCoder.encode(activity, forKey: "activity")
		}
		if activityName != nil{
			aCoder.encode(activityName, forKey: "activityName")
		}
		if activityToken != nil{
			aCoder.encode(activityToken, forKey: "activityToken")
		}
		if activityType != nil{
			aCoder.encode(activityType, forKey: "activityType")
		}
		if application != nil{
			aCoder.encode(application, forKey: "application")
		}
		if completed != nil{
			aCoder.encode(completed, forKey: "completed")
		}
		if createTime != nil{
			aCoder.encode(createTime, forKey: "createTime")
		}
		if distributeFactor != nil{
			aCoder.encode(distributeFactor, forKey: "distributeFactor")
		}
		if `extension` != nil{
			aCoder.encode(`extension`, forKey: "extension")
		}
		if id != nil{
			aCoder.encode(id, forKey: "id")
		}
		if job != nil{
			aCoder.encode(job, forKey: "job")
		}
		if lastUpdatePerson != nil{
			aCoder.encode(lastUpdatePerson, forKey: "lastUpdatePerson")
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
		if person != nil{
			aCoder.encode(person, forKey: "person")
		}
		if process != nil{
			aCoder.encode(process, forKey: "process")
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
		if workCreateTime != nil{
			aCoder.encode(workCreateTime, forKey: "workCreateTime")
		}

	}

}
