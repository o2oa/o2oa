//
//	CMSWrapOutCatagoryList.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class CMSWrapOutCategoryList : NSObject, NSCoding, Mappable{

    var appName : String?
	var appId : String?
	var categoryAlias : String?
	var categoryName : String?
	var categorySeq : String?
	var createTime : String?
	var creatorCompany : String?
	var creatorDepartment : String?
	var creatorIdentity : String?
	var creatorPerson : String?
	var descriptionField : String?
	var distributeFactor : Int?
	var formId : String?
	var formName : String?
	var id : String?
	var readFormId : String?
	var readFormName : String?
	var sequence : String?
	var updateTime : String?
    var workflowFlag: String? //流程id 绑定流程的时候会有


	class func newInstance(map: Map) -> Mappable?{
		return CMSWrapOutCategoryList()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
        appName <- map["appName"]
		appId <- map["appId"]
		categoryAlias <- map["categoryAlias"]
		categoryName <- map["categoryName"]
		categorySeq <- map["categorySeq"]
		createTime <- map["createTime"]
		creatorCompany <- map["creatorCompany"]
		creatorDepartment <- map["creatorDepartment"]
		creatorIdentity <- map["creatorIdentity"]
		creatorPerson <- map["creatorPerson"]
		descriptionField <- map["description"]
		distributeFactor <- map["distributeFactor"]
		formId <- map["formId"]
		formName <- map["formName"]
		id <- map["id"]
		readFormId <- map["readFormId"]
		readFormName <- map["readFormName"]
		sequence <- map["sequence"]
		updateTime <- map["updateTime"]
        workflowFlag <- map["workflowFlag"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         appId = aDecoder.decodeObject(forKey: "appId") as? String
         categoryAlias = aDecoder.decodeObject(forKey: "categoryAlias") as? String
         categoryName = aDecoder.decodeObject(forKey: "categoryName") as? String
         categorySeq = aDecoder.decodeObject(forKey: "categorySeq") as? String
         createTime = aDecoder.decodeObject(forKey: "createTime") as? String
         creatorCompany = aDecoder.decodeObject(forKey: "creatorCompany") as? String
         creatorDepartment = aDecoder.decodeObject(forKey: "creatorDepartment") as? String
         creatorIdentity = aDecoder.decodeObject(forKey: "creatorIdentity") as? String
         creatorPerson = aDecoder.decodeObject(forKey: "creatorPerson") as? String
         descriptionField = aDecoder.decodeObject(forKey: "description") as? String
         distributeFactor = aDecoder.decodeObject(forKey: "distributeFactor") as? Int
         formId = aDecoder.decodeObject(forKey: "formId") as? String
         formName = aDecoder.decodeObject(forKey: "formName") as? String
         id = aDecoder.decodeObject(forKey: "id") as? String
         readFormId = aDecoder.decodeObject(forKey: "readFormId") as? String
         readFormName = aDecoder.decodeObject(forKey: "readFormName") as? String
         sequence = aDecoder.decodeObject(forKey: "sequence") as? String
         updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
         workflowFlag = aDecoder.decodeObject(forKey: "workflowFlag") as? String

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
		if categoryAlias != nil{
			aCoder.encode(categoryAlias, forKey: "categoryAlias")
		}
		if categoryName != nil{
			aCoder.encode(categoryName, forKey: "categoryName")
		}
		if categorySeq != nil{
			aCoder.encode(categorySeq, forKey: "categorySeq")
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
		if formId != nil{
			aCoder.encode(formId, forKey: "formId")
		}
		if formName != nil{
			aCoder.encode(formName, forKey: "formName")
		}
		if id != nil{
			aCoder.encode(id, forKey: "id")
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
		if updateTime != nil{
			aCoder.encode(updateTime, forKey: "updateTime")
		}
        if workflowFlag != nil {
            aCoder.encode(workflowFlag, forKey: "workflowFlag")
        }

	}

}
