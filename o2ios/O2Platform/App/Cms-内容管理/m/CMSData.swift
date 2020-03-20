//
//	CMSData.swift
//	Model file generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

import Foundation
import ObjectMapper

// MARK: - Helper functions for creating encoders and decoders
func newJSONDecoder() -> JSONDecoder {
   let decoder = JSONDecoder()
   if #available(iOS 10.0, OSX 10.12, tvOS 10.0, watchOS 3.0, *) {
       decoder.dateDecodingStrategy = .iso8601
   }
   return decoder
}

func newJSONEncoder() -> JSONEncoder {
   let encoder = JSONEncoder()
   if #available(iOS 10.0, OSX 10.12, tvOS 10.0, watchOS 3.0, *) {
       encoder.dateEncodingStrategy = .iso8601
   }
   return encoder
}

//cms 栏目的配置文件 存在CMSData栏目对象中的config这个字读啊
struct CMSAppConfig: Codable {

    let ignoreTitle: Bool? //是否要填写文档标题
    let latest: Bool? //是否忽略草稿

    init(_ json: String, using encoding: String.Encoding = .utf8) throws {
        guard let data = json.data(using: encoding) else {
            throw NSError(domain: "JSONDecoding", code: 0, userInfo: nil)
        }
        try self.init(data: data)
    }
    init(data: Data) throws {
        self = try newJSONDecoder().decode(CMSAppConfig.self, from: data)
    }
    init(fromURL url: URL) throws {
        try self.init(data: try Data(contentsOf: url))
    }


    func jsonData() throws -> Data {
        return try newJSONEncoder().encode(self)
    }

    func jsonString(encoding: String.Encoding = .utf8) throws -> String? {
        return String(data: try self.jsonData(), encoding: encoding)
    }

   
}

class CMSData: NSObject, NSCoding, Mappable {

    var appAlias: String?
    var appIcon: String?
    var appInfoSeq: String?
    var appName: String?
    var categoryList: [AnyObject]?
    var createTime: String?
    var creatorCompany: String?
    var creatorDepartment: String?
    var creatorIdentity: String?
    var creatorPerson: String?
    var descriptionField: String?
    var distributeFactor: Int?
    var id: String?
    var sequence: String?
    var updateTime: String?
    var config: String? //配置参数用的 json字符串
    var wrapOutCategoryList: [CMSWrapOutCategoryList]?


    class func newInstance(map: Map) -> Mappable? {
        return CMSData()
    }
    required init?(map: Map) { }
    private override init() { }

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
        config <- map["config"]
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
        config = aDecoder.decodeObject(forKey: "config") as? String
        wrapOutCategoryList = aDecoder.decodeObject(forKey: "wrapOutCategoryList") as? [CMSWrapOutCategoryList]

    }

    /**
    * NSCoding required method.
    * Encodes mode properties into the decoder
    */
    @objc func encode(with aCoder: NSCoder)
    {
        if appAlias != nil {
            aCoder.encode(appAlias, forKey: "appAlias")
        }
        if appIcon != nil {
            aCoder.encode(appIcon, forKey: "appIcon")
        }
        if appInfoSeq != nil {
            aCoder.encode(appInfoSeq, forKey: "appInfoSeq")
        }
        if appName != nil {
            aCoder.encode(appName, forKey: "appName")
        }
        if categoryList != nil {
            aCoder.encode(categoryList, forKey: "categoryList")
        }
        if createTime != nil {
            aCoder.encode(createTime, forKey: "createTime")
        }
        if creatorCompany != nil {
            aCoder.encode(creatorCompany, forKey: "creatorCompany")
        }
        if creatorDepartment != nil {
            aCoder.encode(creatorDepartment, forKey: "creatorDepartment")
        }
        if creatorIdentity != nil {
            aCoder.encode(creatorIdentity, forKey: "creatorIdentity")
        }
        if creatorPerson != nil {
            aCoder.encode(creatorPerson, forKey: "creatorPerson")
        }
        if descriptionField != nil {
            aCoder.encode(descriptionField, forKey: "description")
        }
        if distributeFactor != nil {
            aCoder.encode(distributeFactor, forKey: "distributeFactor")
        }
        if id != nil {
            aCoder.encode(id, forKey: "id")
        }
        if sequence != nil {
            aCoder.encode(sequence, forKey: "sequence")
        }
        if updateTime != nil {
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        if config != nil {
            aCoder.encode(config, forKey: "config")
        }
        if wrapOutCategoryList != nil {
            aCoder.encode(wrapOutCategoryList, forKey: "wrapOutCategoryList")
        }

    }

}
