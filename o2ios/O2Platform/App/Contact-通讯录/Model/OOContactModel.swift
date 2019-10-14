//
//  OOContactModel.swift
//  o2app
//
//  Created by 刘振兴 on 2017/11/20.
//  Copyright © 2017年 zone. All rights reserved.
//

import Foundation
import HandyJSON



class OOControl : NSObject, NSCoding, DataModel{
    
    var allowDelete : Bool?
    var allowEdit : Bool?
    
    required override init() {
        
    }
    
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        allowDelete = aDecoder.decodeObject(forKey: "allowDelete") as? Bool
        allowEdit = aDecoder.decodeObject(forKey: "allowEdit") as? Bool
        
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if allowDelete != nil{
            aCoder.encode(allowDelete, forKey: "allowDelete")
        }
        if allowEdit != nil{
            aCoder.encode(allowEdit, forKey: "allowEdit")
        }
        
    }
    
}

// 面包屑导航对象
struct ContactBreadcrumbBean {
    var key: String = ""
    var name: String = ""
    var level: Int = 0
}


// 选择器使用的对象
//组织对象
class O2UnitPickerItem: NSObject, DataModel {
    
    @objc open var id : String?
    @objc open  var name : String?
    @objc open var unique : String?
    @objc open var distinguishedName : String?
    @objc open  var typeList : [String]?
    @objc open var shortName : String?
    var level : Int?
    @objc open var levelName : String?
    
    override required init(){}
    
    func copyFromUnitModel(dept: OOUnitModel) {
        self.id = dept.id
        self.name = dept.name
        self.unique = dept.unique
        self.distinguishedName = dept.distinguishedName
        self.typeList = dept.typeList
        self.shortName = dept.shortName
        self.level = dept.level
        self.levelName = dept.levelName
    }
    
}
//身份对象
class O2IdentityPickerItem: NSObject, DataModel {
    
    @objc open var id : String?
    @objc open var name : String?
    @objc open var unique : String?
    @objc open var distinguishedName : String?
    @objc open var person : String?
    @objc open var unit : String?
    @objc open var unitName : String?
    @objc open var personName : String?
    @objc open var personUnique : String?
    @objc open var personDn : String?
    var unitLevel : Int?
    @objc open var unitLevelName : String?
    
    override required init(){}
    
    func copyFromIdentityModel(identity: OOIdentityModel) {
        self.id = identity.id
        self.name = identity.name
        self.unique = identity.unique
        self.distinguishedName = identity.distinguishedName
        self.unit = identity.unit
        self.unitName = identity.unitName
        self.unitLevel = identity.unitLevel
        self.unitLevelName = identity.unitLevelName
        self.person = identity.person
        //person相关信息等获取用户信息后再填充
    }
    //更新用户相关的信息
    func updatePersonInfo(person: OOPersonModel) {
        self.person = person.id
        self.personUnique = person.unique
        self.personName = person.name
        self.personDn = person.distinguishedName
    }
    
}
//群组对象
class O2GroupPickerItem: NSObject, DataModel {
    
    @objc open var id : String?
    @objc open var name : String?
    @objc open var unique : String?
    @objc open var distinguishedName : String?
   
    override required init(){}
    
    func copyFromGroupModel(group: OOGroupModel) {
        self.id = group.id
        self.name = group.name
        self.unique = group.unique
        self.distinguishedName = group.distinguishedName
    }
    
}
//人员对象
class O2PersonPickerItem: NSObject, DataModel {
    
    @objc open var id : String?
    @objc open var name : String?
    @objc open var unique : String?
    @objc open var distinguishedName : String?
    @objc open var genderType : String?
    @objc open var employee : String?
    @objc open var mail : String?
    @objc open var weixin : String?
    @objc open var qq : String?
    @objc open var mobile : String?
    @objc open var officePhone : String?
    
    override required init(){}
    
    func copyFromPersonModel(person: OOPersonModel) {
        self.id = person.id
        self.distinguishedName = person.distinguishedName
        self.name = person.name
        self.unique = person.unique
        self.genderType = person.genderType
        self.employee = person.employee
        self.mail = person.mail
        self.weixin = person.weixin
        self.qq = person.qq
        self.mobile = person.mobile
        self.officePhone = person.officePhone
    }
}


// MARK: - Unit Model

class OOUnitModel : NSObject, NSCoding, DataModel{
    
    @objc open var control : OOControl?
    @objc open var controllerList : [AnyObject]?
    @objc open var createTime : String?
    @objc open var desc : String?
    @objc open var descriptionField : String?
    @objc open var distinguishedName : String?
    @objc open var id : String?
    @objc open var inheritedControllerList : [AnyObject]?
    var level : Int?
    @objc open var levelName : String?
   @objc open  var name : String?
    var orderNumber : Int?
    @objc open var pinyin : String?
    @objc open var pinyinInitial : String?
    @objc open var shortName : String?
    var subDirectIdentityCount : Int?
    var subDirectUnitCount : Int?
    @objc open var superior : String?
   @objc open  var typeList : [String]?
    @objc open var unique : String?
    @objc open var updateTime : String?
    @objc open var woSubDirectIdentityList:[OOIdentityModel]?
    
    override required init(){}
    
     func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
    
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        control = aDecoder.decodeObject(forKey: "control") as? OOControl
        controllerList = aDecoder.decodeObject(forKey: "controllerList") as? [AnyObject]
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        descriptionField = aDecoder.decodeObject(forKey: "description") as? String
        distinguishedName = aDecoder.decodeObject(forKey: "distinguishedName") as? String
        id = aDecoder.decodeObject(forKey: "id") as? String
        inheritedControllerList = aDecoder.decodeObject(forKey: "inheritedControllerList") as? [AnyObject]
        level = aDecoder.decodeObject(forKey: "level") as? Int
        levelName = aDecoder.decodeObject(forKey: "levelName") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        orderNumber = aDecoder.decodeObject(forKey: "orderNumber") as? Int
        pinyin = aDecoder.decodeObject(forKey: "pinyin") as? String
        pinyinInitial = aDecoder.decodeObject(forKey: "pinyinInitial") as? String
        shortName = aDecoder.decodeObject(forKey: "shortName") as? String
        subDirectIdentityCount = aDecoder.decodeObject(forKey: "subDirectIdentityCount") as? Int
        subDirectUnitCount = aDecoder.decodeObject(forKey: "subDirectUnitCount") as? Int
        superior = aDecoder.decodeObject(forKey: "superior") as? String
        typeList = aDecoder.decodeObject(forKey: "typeList") as? [String]
        unique = aDecoder.decodeObject(forKey: "unique") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        woSubDirectIdentityList = aDecoder.decodeObject(forKey: "woSubDirectIdentityList") as? [OOIdentityModel]
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if control != nil{
            aCoder.encode(control, forKey: "control")
        }
        if controllerList != nil{
            aCoder.encode(controllerList, forKey: "controllerList")
        }
        if createTime != nil{
            aCoder.encode(createTime, forKey: "createTime")
        }
        if descriptionField != nil{
            aCoder.encode(descriptionField, forKey: "description")
        }
        if distinguishedName != nil{
            aCoder.encode(distinguishedName, forKey: "distinguishedName")
        }
        if id != nil{
            aCoder.encode(id, forKey: "id")
        }
        if inheritedControllerList != nil{
            aCoder.encode(inheritedControllerList, forKey: "inheritedControllerList")
        }
        if level != nil{
            aCoder.encode(level, forKey: "level")
        }
        if levelName != nil{
            aCoder.encode(levelName, forKey: "levelName")
        }
        if name != nil{
            aCoder.encode(name, forKey: "name")
        }
        if orderNumber != nil{
            aCoder.encode(orderNumber, forKey: "orderNumber")
        }
        if pinyin != nil{
            aCoder.encode(pinyin, forKey: "pinyin")
        }
        if pinyinInitial != nil{
            aCoder.encode(pinyinInitial, forKey: "pinyinInitial")
        }
        if shortName != nil{
            aCoder.encode(shortName, forKey: "shortName")
        }
        if subDirectIdentityCount != nil{
            aCoder.encode(subDirectIdentityCount, forKey: "subDirectIdentityCount")
        }
        if subDirectUnitCount != nil{
            aCoder.encode(subDirectUnitCount, forKey: "subDirectUnitCount")
        }
        if superior != nil{
            aCoder.encode(superior, forKey: "superior")
        }
        if typeList != nil{
            aCoder.encode(typeList, forKey: "typeList")
        }
        if unique != nil{
            aCoder.encode(unique, forKey: "unique")
        }
        if updateTime != nil{
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        if woSubDirectIdentityList != nil{
            aCoder.encode(woSubDirectIdentityList, forKey: "woSubDirectIdentityList")
        }
        
    }
    
}


class OOPersonDNModel: NSObject, NSCoding, DataModel {
    public override required init() {
    }
    
    @objc var personList:[String]?
    
    
    func encode(with aCoder: NSCoder) {
        if personList != nil{
            aCoder.encode(personList, forKey: "personList")
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
         personList = aDecoder.decodeObject(forKey: "personList") as? [String]
    }
    
    
    
}


// MARK: - OO Person Model
class OOPersonModel : NSObject, NSCoding, DataModel{
    
    var age : Int?
    @objc var changePasswordTime : String?
    @objc var controllerList : [String]?
    @objc var createTime : String?
    @objc var descriptionField : String?
    @objc var distinguishedName : String?
    @objc var employee : String?
    @objc var genderType : String?
    @objc var id : String?
    @objc var mail : String?
    @objc var mobile : String?
    @objc var name : String?
    @objc var officePhone : String?
    var orderNumber : Int?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var qq : String?
    @objc var signature : String?
    @objc var superior : String?
    @objc var unique : String?
    @objc var updateTime : String?
    @objc var weixin : String?
    @objc var woIdentityList:[OOIdentityModel]?
    @objc var woGroupList:[OOGroupModel]?
    @objc var desc : String?
    
    public override required init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
   
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        age = aDecoder.decodeObject(forKey: "age") as? Int
        changePasswordTime = aDecoder.decodeObject(forKey: "changePasswordTime") as? String
        controllerList = aDecoder.decodeObject(forKey: "controllerList") as? [String]
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        descriptionField = aDecoder.decodeObject(forKey: "description") as? String
        distinguishedName = aDecoder.decodeObject(forKey: "distinguishedName") as? String
        employee = aDecoder.decodeObject(forKey: "employee") as? String
        genderType = aDecoder.decodeObject(forKey: "genderType") as? String
        id = aDecoder.decodeObject(forKey: "id") as? String
        mail = aDecoder.decodeObject(forKey: "mail") as? String
        mobile = aDecoder.decodeObject(forKey: "mobile") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        officePhone = aDecoder.decodeObject(forKey: "officePhone") as? String
        orderNumber = aDecoder.decodeObject(forKey: "orderNumber") as? Int
        pinyin = aDecoder.decodeObject(forKey: "pinyin") as? String
        pinyinInitial = aDecoder.decodeObject(forKey: "pinyinInitial") as? String
        qq = aDecoder.decodeObject(forKey: "qq") as? String
        signature = aDecoder.decodeObject(forKey: "signature") as? String
        superior = aDecoder.decodeObject(forKey: "superior") as? String
        unique = aDecoder.decodeObject(forKey: "unique") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        weixin = aDecoder.decodeObject(forKey: "weixin") as? String
        
        
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if age != nil{
            aCoder.encode(age, forKey: "age")
        }
        if changePasswordTime != nil{
            aCoder.encode(changePasswordTime, forKey: "changePasswordTime")
        }
        if controllerList != nil{
            aCoder.encode(controllerList, forKey: "controllerList")
        }
        if createTime != nil{
            aCoder.encode(createTime, forKey: "createTime")
        }
        if descriptionField != nil{
            aCoder.encode(descriptionField, forKey: "description")
        }
        if distinguishedName != nil{
            aCoder.encode(distinguishedName, forKey: "distinguishedName")
        }
        if employee != nil{
            aCoder.encode(employee, forKey: "employee")
        }
        if genderType != nil{
            aCoder.encode(genderType, forKey: "genderType")
        }
        if id != nil{
            aCoder.encode(id, forKey: "id")
        }
        if mail != nil{
            aCoder.encode(mail, forKey: "mail")
        }
        if mobile != nil{
            aCoder.encode(mobile, forKey: "mobile")
        }
        if name != nil{
            aCoder.encode(name, forKey: "name")
        }
        if officePhone != nil{
            aCoder.encode(officePhone, forKey: "officePhone")
        }
        if orderNumber != nil{
            aCoder.encode(orderNumber, forKey: "orderNumber")
        }
        if pinyin != nil{
            aCoder.encode(pinyin, forKey: "pinyin")
        }
        if pinyinInitial != nil{
            aCoder.encode(pinyinInitial, forKey: "pinyinInitial")
        }
        if qq != nil{
            aCoder.encode(qq, forKey: "qq")
        }
        if signature != nil{
            aCoder.encode(signature, forKey: "signature")
        }
        if superior != nil{
            aCoder.encode(superior, forKey: "superior")
        }
        if unique != nil{
            aCoder.encode(unique, forKey: "unique")
        }
        if updateTime != nil{
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        if weixin != nil{
            aCoder.encode(weixin, forKey: "weixin")
        }
        
    }
    
}

// MARK: - OO Identity Model
class OOIdentityModel : NSObject, NSCoding, DataModel{
    
    @objc var createTime : String?
    @objc var descriptionField : String?
    @objc var distinguishedName : String?
    @objc var id : String?
    @objc var name : String?
    var orderNumber : Int?
    @objc var person : String?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var unique : String?
    @objc var unit : String?
    var unitLevel : Int?
    @objc var unitLevelName : String?
    @objc var unitName : String?
    @objc var updateTime : String?
    @objc var woPerson : OOPersonModel?
    @objc var woUnit:OOUnitModel?
    @objc var desc : String?
    public override required init() {
        
    }
    
    override var description: String {
        return "\(self.name!)(\(self.unitName!))"
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }
    
   
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        descriptionField = aDecoder.decodeObject(forKey: "description") as? String
        distinguishedName = aDecoder.decodeObject(forKey: "distinguishedName") as? String
        id = aDecoder.decodeObject(forKey: "id") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        orderNumber = aDecoder.decodeObject(forKey: "orderNumber") as? Int
        person = aDecoder.decodeObject(forKey: "person") as? String
        pinyin = aDecoder.decodeObject(forKey: "pinyin") as? String
        pinyinInitial = aDecoder.decodeObject(forKey: "pinyinInitial") as? String
        unique = aDecoder.decodeObject(forKey: "unique") as? String
        unit = aDecoder.decodeObject(forKey: "unit") as? String
        unitLevel = aDecoder.decodeObject(forKey: "unitLevel") as? Int
        unitLevelName = aDecoder.decodeObject(forKey: "unitLevelName") as? String
        unitName = aDecoder.decodeObject(forKey: "unitName") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        woPerson = aDecoder.decodeObject(forKey: "woPerson") as? OOPersonModel
        woUnit = aDecoder.decodeObject(forKey: "woUnit") as? OOUnitModel
        
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if createTime != nil{
            aCoder.encode(createTime, forKey: "createTime")
        }
        if descriptionField != nil{
            aCoder.encode(descriptionField, forKey: "description")
        }
        if distinguishedName != nil{
            aCoder.encode(distinguishedName, forKey: "distinguishedName")
        }
        if id != nil{
            aCoder.encode(id, forKey: "id")
        }
        if name != nil{
            aCoder.encode(name, forKey: "name")
        }
        if orderNumber != nil{
            aCoder.encode(orderNumber, forKey: "orderNumber")
        }
        if person != nil{
            aCoder.encode(person, forKey: "person")
        }
        if pinyin != nil{
            aCoder.encode(pinyin, forKey: "pinyin")
        }
        if pinyinInitial != nil{
            aCoder.encode(pinyinInitial, forKey: "pinyinInitial")
        }
        if unique != nil{
            aCoder.encode(unique, forKey: "unique")
        }
        if unit != nil{
            aCoder.encode(unit, forKey: "unit")
        }
        if unitLevel != nil{
            aCoder.encode(unitLevel, forKey: "unitLevel")
        }
        if unitLevelName != nil{
            aCoder.encode(unitLevelName, forKey: "unitLevelName")
        }
        if unitName != nil{
            aCoder.encode(unitName, forKey: "unitName")
        }
        if updateTime != nil{
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        if woPerson != nil{
            aCoder.encode(woPerson, forKey: "woPerson")
        }
        
        if woUnit != nil{
            aCoder.encode(woUnit, forKey: "woUnit")
        }
        
    }
    
}

// MARK: - OO Group Model

class OOGroupModel : NSObject, NSCoding, DataModel{
    
    @objc var control : OOControl?
    @objc var createTime : String?
    @objc var descriptionField : String?
    @objc var distinguishedName : String?
    @objc var groupList : [String]?
    @objc var id : String?
    @objc var name : String?
    @objc var personList : [String]?
    @objc var pinyin : String?
    @objc var pinyinInitial : String?
    @objc var unique : String?
    @objc var updateTime : String?
    @objc var woGroupList:[OOGroupModel]?
    @objc var woPersonList:[OOPersonModel]?
    @objc var desc : String?
    
    public override required init() {
        
    }
    
    func mapping(mapper: HelpingMapper) {
        mapper <<< self.desc <-- "description"
    }

    
    /**
     * NSCoding required initializer.
     * Fills the data from the passed decoder
     */
    @objc required init(coder aDecoder: NSCoder)
    {
        control = aDecoder.decodeObject(forKey: "control") as? OOControl
        createTime = aDecoder.decodeObject(forKey: "createTime") as? String
        descriptionField = aDecoder.decodeObject(forKey: "description") as? String
        distinguishedName = aDecoder.decodeObject(forKey: "distinguishedName") as? String
        groupList = aDecoder.decodeObject(forKey: "groupList") as? [String]
        id = aDecoder.decodeObject(forKey: "id") as? String
        name = aDecoder.decodeObject(forKey: "name") as? String
        personList = aDecoder.decodeObject(forKey: "personList") as? [String]
        pinyin = aDecoder.decodeObject(forKey: "pinyin") as? String
        pinyinInitial = aDecoder.decodeObject(forKey: "pinyinInitial") as? String
        unique = aDecoder.decodeObject(forKey: "unique") as? String
        updateTime = aDecoder.decodeObject(forKey: "updateTime") as? String
        
    }
    
    /**
     * NSCoding required method.
     * Encodes mode properties into the decoder
     */
    @objc func encode(with aCoder: NSCoder)
    {
        if control != nil{
            aCoder.encode(control, forKey: "control")
        }
        if createTime != nil{
            aCoder.encode(createTime, forKey: "createTime")
        }
        if descriptionField != nil{
            aCoder.encode(descriptionField, forKey: "description")
        }
        if distinguishedName != nil{
            aCoder.encode(distinguishedName, forKey: "distinguishedName")
        }
        if groupList != nil{
            aCoder.encode(groupList, forKey: "groupList")
        }
        if id != nil{
            aCoder.encode(id, forKey: "id")
        }
        if name != nil{
            aCoder.encode(name, forKey: "name")
        }
        if personList != nil{
            aCoder.encode(personList, forKey: "personList")
        }
        if pinyin != nil{
            aCoder.encode(pinyin, forKey: "pinyin")
        }
        if pinyinInitial != nil{
            aCoder.encode(pinyinInitial, forKey: "pinyinInitial")
        }
        if unique != nil{
            aCoder.encode(unique, forKey: "unique")
        }
        if updateTime != nil{
            aCoder.encode(updateTime, forKey: "updateTime")
        }
        
    }
    
}
