//
//  OOAppsInfoDB.swift
//  O2Platform
//
//  Created by 刘振兴 on 2018/5/9.
//  Copyright © 2018年 zoneland. All rights reserved.
//

import UIKit
import FMDB


final class OOAppsInfoDB: NSObject {
    
    static let shareInstance = OOAppsInfoDB()
    
    var queue:FMDatabaseQueue?
    
    private func _createDB() {
        var path = "\(NSHomeDirectory())/Documents/O2"
        if createPath(path) {
            path = path + "/OOAppsInfo.db"
            O2Logger.debug("OOAppsInfoDBWithPath:\(path)")
            queue = FMDatabaseQueue(path: path)
            let sql = "create table IF NOT EXISTS OOAppsInfo(`id` integer primary key autoincrement,`title` text not null,`appid` text not null,`storyboard` text,`vcname` text,`segueidentifier` text,`normalicon` text,`selectedicon` text,`order` integer,`mainorder` integer,`categorytype` integer)"
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
        let sql = "drop table if exists OOAppsInfo"
        queue?.inDatabase({ (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        })
    }
    
    
    func updateData(_ info:O2App,_ categoryType:Int){
        if queue == nil {
            _createDB()
            updateData(info,categoryType)
        }
        let sql = "UPDATE OOAppsInfo SET title=?,storyboard=?,vcname=?,segueidentifier=?,normalicon=?,selectedicon=?,`order`=?,mainorder=?,categorytype=? where appid=?"
        let argumentsArray = [(info.title ?? ""), (info.storyBoard ?? ""), (info.vcName ?? ""),(info.segueIdentifier ?? ""), (info.normalIcon ?? ""), (info.selectedIcon ?? ""), info.order, info.mainOrder, categoryType, info.appId] as [Any]
        queue?.inDatabase({ (db) in
            db?.executeUpdate(sql,withArgumentsIn:argumentsArray)
        })
    }
    
    func insertData(_ info:O2App){
        if queue == nil {
            _createDB()
            insertData(info)
        }
        let sSql = "select count(*) as appnumber,`categorytype` from OOAppsInfo where appid = '\(String(describing: info.appId!))'"
        let insertSql = "INSERT INTO OOAppsInfo(appid,title,storyboard,vcname,segueidentifier,normalicon,selectedicon,`order`,mainOrder,categorytype) values (?,?,?,?,?,?,?,?,?,?)"
        let argumentsArray1 = [info.appId , (info.title ?? ""), (info.storyBoard ?? ""),(info.vcName ?? ""), (info.segueIdentifier ?? ""), (info.normalIcon ?? ""), (info.selectedIcon ?? ""), info.order, info.mainOrder, 0] as [Any]
        var isUpdate = false
        var categoryType = 0
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sSql, withArgumentsIn: nil)
            if let _ = (resultSet?.next()) {
                if Int(resultSet?.int(forColumn:"appnumber") ?? 0 ) > 0 {
                    isUpdate = true
                    categoryType = Int(resultSet?.int(forColumn: "categorytype") ?? 0)
                }else{
                    isUpdate = false
                    db?.executeUpdate(insertSql, withArgumentsIn: argumentsArray1)
                }
            }else{
                db?.executeUpdate(insertSql, withArgumentsIn: argumentsArray1)
            }
        })
        if isUpdate == true {
            updateData(info, categoryType)
        }
    }
    
    func removeAll() {
        if queue == nil {
            _createDB()
            removeAll()
        }
        let delSql = "DELETE FROM OOAppsInfo WHERE 1=1"
        queue?.inDatabase({ (db) in
            db?.executeUpdate(delSql, withArgumentsIn: nil)
        })
    }
    
    func deleteNotExistApp(_ exitApps: [String]) {
        if queue == nil {
            _createDB()
            deleteNotExistApp(exitApps)
        }
        
        var infos:[O2App] = []
        let sql = "select * from OOAppsInfo ORDER BY `order` ASC"
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())!{
                let app = O2App()
                app.appId = resultSet?.string(forColumn: "appid")
                app.title = resultSet?.string(forColumn: "title")
                app.storyBoard = resultSet?.string(forColumn: "storyboard")
                app.vcName = resultSet?.string(forColumn: "vcname")
                app.segueIdentifier = resultSet?.string(forColumn: "segueidentifier")
                app.normalIcon = resultSet?.string(forColumn: "normalicon")
                app.selectedIcon = resultSet?.string(forColumn: "selectedicon")
                app.order = Int((resultSet?.int(forColumn: "order"))!)
                app.mainOrder = Int((resultSet?.int(forColumn: "mainorder"))!)
                infos.append(app)
            }
            
            infos.forEachEnumerated({ (index, app) in
                if let appId = app.appId {
                    if !exitApps.contains(appId) {// 不存在就删除
                        let delSql = "delete from OOAppsInfo where appid = '\(appId)'"
                        db?.executeUpdate(delSql, withArgumentsIn: nil)
                    }
                }
            })
            
            
        })
        
    }
    
    func delete(_ info:O2App){
        if queue == nil {
            _createDB()
            delete(info)
        }
        let delSql = "delete from OOAppsInfo where appid = '\(String(describing: info.appId!))'"
        queue?.inDatabase({ (db) in
            db?.executeUpdate(delSql, withArgumentsIn: nil)
        })
    }
    
    func queryData(_ appId:String) -> O2App? {
        if queue == nil {
            _createDB()
            let _ = queryData(appId)
        }
        var homeApp:O2App?
        let sql = "SELECT * FROM OOAppsInfo WHERE appid = '\(appId)'"
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while(resultSet?.next())!{
                homeApp = O2App()
                homeApp?.appId = resultSet?.string(forColumn: "appid")
                homeApp?.title = resultSet?.string(forColumn: "title")
                homeApp?.storyBoard = resultSet?.string(forColumn: "storyboard")
                homeApp?.vcName = resultSet?.string(forColumn: "vcname")
                homeApp?.segueIdentifier = resultSet?.string(forColumn: "segueidentifier")
                homeApp?.normalIcon = resultSet?.string(forColumn: "normalicon")
                homeApp?.selectedIcon = resultSet?.string(forColumn: "selectedicon")
                homeApp?.order = Int((resultSet?.int(forColumn: "order"))!)
                homeApp?.mainOrder = Int((resultSet?.int(forColumn: "mainorder"))!)
            }
        })
        return homeApp
    }
    
    
    func queryData() -> [O2App] {
        if queue == nil {
            _createDB()
            let _ = queryData()
        }
        var infos:[O2App] = []
        let sql = "select * from OOAppsInfo ORDER BY `order` ASC"
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())!{
                let app = O2App()
                app.appId = resultSet?.string(forColumn: "appid")
                app.title = resultSet?.string(forColumn: "title")
                app.storyBoard = resultSet?.string(forColumn: "storyboard")
                app.vcName = resultSet?.string(forColumn: "vcname")
                app.segueIdentifier = resultSet?.string(forColumn: "segueidentifier")
                app.normalIcon = resultSet?.string(forColumn: "normalicon")
                app.selectedIcon = resultSet?.string(forColumn: "selectedicon")
                app.order = Int((resultSet?.int(forColumn: "order"))!)
                app.mainOrder = Int((resultSet?.int(forColumn: "mainorder"))!)
                infos.append(app)
            }
        })
        return infos
    }
    
    func queryNoMainData() -> [O2App] {
        if queue == nil {
            _createDB()
            let _ = queryNoMainData()
        }
        var infos:[O2App] = []
        let sql = "select * from OOAppsInfo where categorytype = 0 ORDER BY `order`"
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())!{
                let app = O2App()
                app.appId = resultSet?.string(forColumn: "appid")
                app.title = resultSet?.string(forColumn: "title")
                app.storyBoard = resultSet?.string(forColumn: "storyboard")
                app.vcName = resultSet?.string(forColumn: "vcname")
                app.segueIdentifier = resultSet?.string(forColumn: "segueidentifier")
                app.normalIcon = resultSet?.string(forColumn: "normalicon")
                app.selectedIcon = resultSet?.string(forColumn: "selectedicon")
                app.order = Int((resultSet?.int(forColumn: "order"))!)
                app.mainOrder = Int((resultSet?.int(forColumn: "mainorder"))!)
                infos.append(app)
            }
        })
        return infos
    }
    
    func queryMainData() -> [O2App] {
        if queue == nil {
            _createDB()
            let _ = queryMainData()
        }
        var infos:[O2App] = []
        let sql = "select * from OOAppsInfo where categorytype = 1 ORDER BY `mainorder`"
        queue?.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())!{
                let app = O2App()
                app.appId = resultSet?.string(forColumn: "appid")
                app.title = resultSet?.string(forColumn: "title")
                app.storyBoard = resultSet?.string(forColumn: "storyboard")
                app.vcName = resultSet?.string(forColumn: "vcname")
                app.segueIdentifier = resultSet?.string(forColumn: "segueidentifier")
                app.normalIcon = resultSet?.string(forColumn: "normalicon")
                app.selectedIcon = resultSet?.string(forColumn: "selectedicon")
                app.order = Int((resultSet?.int(forColumn: "order"))!)
                app.mainOrder = Int((resultSet?.int(forColumn: "mainorder"))!)
                infos.append(app)
            }
        })
        return infos
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
