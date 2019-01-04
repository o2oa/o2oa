//
//  OOContantsInfoDB.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/10.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import FMDB

final class OOContactsInfoDB: NSObject {
    
    static let shareInstance = OOContactsInfoDB()
    
    var queue:FMDatabaseQueue?
    
    private func _createDB() {
        var path = "\(NSHomeDirectory())/Documents/O2"
        if createPath(path) {
            path = path + "/OOAppsInfo.db"
            O2Logger.debug("OOAppsInfoDBWithPath:\(path)")
            queue = FMDatabaseQueue(path: path)
            let sql = "create table IF NOT EXISTS `OOContactPrivates` ( `id` TEXT NOT NULL, `distinguishedName` TEXT NOT NULL, `employee` TEXT, `genderType` TEXT, `lastLoginAddress` TEXT, `lastLoginClient` TEXT, `lastLoginTime` TEXT, `mail` TEXT, `mobile` TEXT, `name` TEXT, `changePasswordTime` TEXT, `superior` TEXT, `signature` TEXT, `pinyin` TEXT, `pinyinInitial` TEXT, `qq` TEXT, `unique` TEXT, `updateTime` TEXT, `weixin` TEXT, `officePhone` TEXT, `boardDate` TEXT, `birthday` TEXT, `orderNumber` INTEGER , `ownerid` TEXT NOT NULL)"
            queue?.inDatabase({ (db) in
                db?.executeUpdate(sql, withArgumentsIn: nil)
            })
        }
    }
    
    func dropTable() {
        if queue == nil {
            _createDB()
            dropTable()
        }
        let sql = "DROP TABLE IF EXISTS `OOContactPrivates`"
        queue?.inDatabase({ (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        })
    }
    
    func insertData(_ person:PersonV2,_ ownerid:String) {
        if queue == nil {
            _createDB()
            insertData(person,ownerid)
        }
        let sSql = "select count(*) from `OOContactPrivates` where `id` = \(String(describing: person.id!)) and `ownerid` = '\(ownerid)'"
        let sql = "insert into `OOContactPrivates`(`id`,`distinguishedName`,`employee`,`genderType`,`lastLoginAddress`,`lastLoginClient`,`lastLoginTime`,`mail`,`mobile`,`name`,`changePasswordTime`,`superior`,`signature`,`pinyin`,`pinyinInitial`,`qq`,`unique`,`updateTime`,`weixin`,`officePhone`,`boardDate`,`birthday`,`orderNumber`,`ownerid`) values ('\(String(describing: person.id!))','\(String(describing: person.distinguishedName!))','\(String(describing: person.employee ?? ""))','\(String(describing: person.genderType ?? ""))','\(String(describing: person.lastLoginAddress ?? ""))','\(String(describing: person.lastLoginClient ?? ""))','\(String(describing: person.lastLoginTime ?? ""))','\(String(describing: person.mail ?? ""))','\(String(describing: person.mobile ?? ""))','\(String(describing: person.name ?? ""))','\(String(describing: person.changePasswordTime ?? ""))','\(String(describing: person.superior ?? ""))','\(String(describing: person.signature ?? ""))','\(String(describing: person.pinyin ?? ""))','\(String(describing: person.pinyinInitial ?? ""))','\(String(describing: person.qq ?? ""))','\(String(describing: person.unique ?? ""))','\(String(describing: person.updateTime ?? ""))','\(String(describing: person.weixin ?? ""))','\(String(describing: person.officePhone ?? ""))','\(String(describing: person.boardDate ?? ""))','\(String(describing: person.birthday ?? ""))',\(String(describing: person.orderNumber ?? 0)),'\(ownerid)')"
        var isUpdate = false
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sSql, withArgumentsIn: nil)
            if let _  = (resultSet?.next()) {
                isUpdate = true
            }else{
                db?.executeUpdate(sql, withArgumentsIn: nil)
            }
        })
        if isUpdate == true {
            updateData(person,ownerid)
        }
    }
    
    
    func updateData(_ person:PersonV2,_ ownerid:String){
        
    }
    
    func isCollect(_ person:PersonV2,_ ownerid:String) -> Bool{
        if queue == nil {
            _createDB()
            return  isCollect(person, ownerid)
        }
        var returnValue = false
        let sql = "SELECT COUNT(*) FROM `OOContactPrivates` WHERE `id` = '\(String(describing: person.id!))' AND `ownerid` = '\(ownerid)' "
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            if let _  = (resultSet?.next()) {
                returnValue = true
            }else{
                returnValue = false
            }
        })
        return returnValue
    }
    
    func deleteData(_ person:PersonV2,_ ownerid:String){
        if queue == nil {
            _createDB()
            deleteData(person,ownerid)
        }
        let sql = "delete from `OOContactPrivates` where `id` = '\(String(describing: person.id!))' and `ownerid` = '\(ownerid)'"
        queue?.inDatabase({ (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        })
    }
    
    func queryData(_ ownerid:String) -> [PersonV2] {
        if queue == nil {
            _createDB()
            let _ = queryData(ownerid)
        }
        var persons:[PersonV2] = []
        let sql = "SELECT * FROM `OOContactPrivates` WHERE `ownerid` = '\(ownerid)'"
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())! {
                let p = PersonV2()
                p.id = resultSet?.string(forColumn: "id")
                p.name = resultSet?.string(forColumn: "name")
                p.changePasswordTime = resultSet?.string(forColumn: "changePasswordTime")
                p.distinguishedName = resultSet?.string(forColumn: "distinguishedName")
                p.employee = resultSet?.string(forColumn: "employee")
                p.genderType = resultSet?.string(forColumn: "genderType")
                p.lastLoginAddress = resultSet?.string(forColumn: "lastLoginAddress")
                p.lastLoginClient = resultSet?.string(forColumn: "lastLoginClient")
                p.lastLoginTime = resultSet?.string(forColumn: "lastLoginTime")
                p.mail = resultSet?.string(forColumn: "mail")
                p.mobile = resultSet?.string(forColumn: "mobile")
                p.orderNumber = Int(resultSet?.int(forColumn: "orderNumber") ?? 0)
                p.superior = resultSet?.string(forColumn: "superior")
                p.signature = resultSet?.string(forColumn: "pinyin")
                p.pinyin = resultSet?.string(forColumn: "id")
                p.pinyinInitial = resultSet?.string(forColumn: "pinyinInitial")
                p.qq = resultSet?.string(forColumn: "qq")
                p.unique = resultSet?.string(forColumn: "unique")
                p.updateTime = resultSet?.string(forColumn: "updateTime")
                p.officePhone = resultSet?.string(forColumn: "officePhone")
                p.boardDate = resultSet?.string(forColumn: "boardDate")
                p.birthday = resultSet?.string(forColumn: "birthday")
                p.ownerid = resultSet?.string(forColumn: "ownerid")
                persons.append(p)
            }
        })
        return persons
    }

    // MARK: - private func
    private func createPath(_ path: String) -> Bool {
        var isDir: ObjCBool = ObjCBool(false)
        
        if FileManager.default.fileExists(atPath: path, isDirectory: &isDir) && isDir.boolValue {
            return true
        } else {
            try! FileManager.default.createDirectory(atPath: path, withIntermediateDirectories: true, attributes: nil)
            return true
        }
    }


}
