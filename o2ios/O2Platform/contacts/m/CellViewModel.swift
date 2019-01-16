//
//  CellViewModel.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/13.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper

enum ContactDataType {
    case company(AnyObject)
    case depart(AnyObject)
    case group(AnyObject)
    case person(AnyObject)
    case identity(AnyObject)
    case title(AnyObject)
}

class CellViewModel {
    
    var name:String?
    
    var openFlag = true
    
    var dataType:ContactDataType
    
    init(name:String?,sourceObject:AnyObject){
        self.name = name
        if sourceObject.isKind(of: Company.self) {
            self.dataType = .company(sourceObject)
        }else if sourceObject.isKind(of: Department.self){
            self.dataType = .depart(sourceObject)
        }else if sourceObject.isKind(of: OrgUnit.self) {
            self.dataType = .depart(sourceObject)
            let unit = sourceObject as! OrgUnit
            if (unit.subDirectUnitCount + unit.subDirectIdentityCount) == 0 {
                openFlag = false
            }
        }else if sourceObject.isKind(of: Person.self) {
            self.dataType = .person(sourceObject)
        }else if sourceObject.isKind(of: PersonV2.self) {
            self.dataType = .person(sourceObject)
        }else if sourceObject.isKind(of: Identity.self) {
            self.dataType = .identity(sourceObject)
        }else if sourceObject.isKind(of: IdentityV2.self) {
            self.dataType = .identity(sourceObject)
        }else if sourceObject.isKind(of: HeadTitle.self) {
            self.dataType = .title(sourceObject)
            openFlag = false
        }else{
            self.dataType = .group(sourceObject)
        }
    }
    
}
