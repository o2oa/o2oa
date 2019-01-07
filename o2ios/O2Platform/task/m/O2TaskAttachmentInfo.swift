//
//	O2TaskAttachmentInfo.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation 
import ObjectMapper


class O2TaskAttachmentInfo : NSObject, NSCoding, Mappable{

	var count : Int?
	var data : O2TaskAttachmentInfoData?
	var date : String?
	var message : String?
	var position : Int?
	var size : Int?
	var spent : Int?
	var type : String?


	class func newInstance(map: Map) -> Mappable?{
		return O2TaskAttachmentInfo()
	}
	required init?(map: Map){}
	private override init(){}

	func mapping(map: Map)
	{
		count <- map["count"]
		data <- map["data"]
		date <- map["date"]
		message <- map["message"]
		position <- map["position"]
		size <- map["size"]
		spent <- map["spent"]
		type <- map["type"]
		
	}

    /**
    * NSCoding required initializer.
    * Fills the data from the passed decoder
    */
    @objc required init(coder aDecoder: NSCoder)
	{
         count = aDecoder.decodeObject(forKey: "count") as? Int
         data = aDecoder.decodeObject(forKey: "data") as? O2TaskAttachmentInfoData
         date = aDecoder.decodeObject(forKey: "date") as? String
         message = aDecoder.decodeObject(forKey: "message") as? String
         position = aDecoder.decodeObject(forKey: "position") as? Int
         size = aDecoder.decodeObject(forKey: "size") as? Int
         spent = aDecoder.decodeObject(forKey: "spent") as? Int
         type = aDecoder.decodeObject(forKey: "type") as? String

	}

    /**
    * NSCoding required method.
    * Encodes mode properties into the decoder
    */
    @objc func encode(with aCoder: NSCoder)
	{
		if count != nil{
			aCoder.encode(count, forKey: "count")
		}
		if data != nil{
			aCoder.encode(data, forKey: "data")
		}
		if date != nil{
			aCoder.encode(date, forKey: "date")
		}
		if message != nil{
			aCoder.encode(message, forKey: "message")
		}
		if position != nil{
			aCoder.encode(position, forKey: "position")
		}
		if size != nil{
			aCoder.encode(size, forKey: "size")
		}
		if spent != nil{
			aCoder.encode(spent, forKey: "spent")
		}
		if type != nil{
			aCoder.encode(type, forKey: "type")
		}

	}

}