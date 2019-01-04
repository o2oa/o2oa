//
//  CollectDeviceData.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/6/28.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
class CollectDeviceData:NSObject,NSCoding,Mappable{
    var unit:String?//上一步 选择的公司名称
    var mobile:String? //手机号码
    var code:String? //验证码
    var name:String?//设备号 友盟的token 中心服务器推送消息需要
    var deviceType:String? //设备类型  android ios
    
    struct CollectDeviceDataKey {
        static let unitKey = "unit"
        static let mobileKey = "mobile"
        static let codeKey = "code"
        static let nameKey = "name"
        static let deviceTypeKey = "deviceType"
    }
    
    override init(){
        
    }
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        unit <- map["unit"]
        mobile <- map["mobile"]
        code <- map["code"]
        name <- map["name"]
        deviceType <- map["deviceType"]
    }
    
    init?(unit:String,mobile:String?,code:String?,name:String?,deviceType:String?){
        self.unit = unit
        self.mobile = mobile
        self.code = code
        self.name = name
        self.deviceType = deviceType
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        let unit = aDecoder.decodeObject(forKey: CollectDeviceDataKey.unitKey) as! String
        let mobile = aDecoder.decodeObject(forKey: CollectDeviceDataKey.mobileKey) as! String
        let code = aDecoder.decodeObject(forKey: CollectDeviceDataKey.codeKey) as! String
        let name = aDecoder.decodeObject(forKey: CollectDeviceDataKey.nameKey) as! String
        let deviceType = aDecoder.decodeObject(forKey: CollectDeviceDataKey.deviceTypeKey) as! String
        self.init(unit:unit,mobile: mobile,code: code,name: name,deviceType: deviceType)
        
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(unit, forKey: CollectDeviceDataKey.unitKey)
        aCoder.encode(mobile, forKey: CollectDeviceDataKey.mobileKey)
        aCoder.encode(code, forKey: CollectDeviceDataKey.codeKey)
        aCoder.encode(name, forKey: CollectDeviceDataKey.nameKey)
        aCoder.encode(deviceType, forKey: CollectDeviceDataKey.deviceTypeKey)
        
    }
    
    
    
}
