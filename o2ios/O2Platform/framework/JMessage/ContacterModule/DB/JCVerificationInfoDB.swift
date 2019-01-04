//
//  JCContacterDBManager.swift
//  JChat
//
//  Created by deng on 2017/5/4.
//  Copyright © 2017年 HXHG. All rights reserved.
//

import UIKit
import FMDB
import JMessage

final class JCVerificationInfoDB: NSObject {
    
    static let shareInstance = JCVerificationInfoDB()
    
    var queue: FMDatabaseQueue?
    
    private override init() {
        super.init()
        _createDB()
    }
    
    private func _createDB() {
        let user = JMSGUser.myInfo()
        if user.username.isEmpty {
            return
        }
        let dir = user.username + user.appKey!
        
        var path = "\(NSHomeDirectory())/Documents/\(dir)"
        
        if createPath(path) {
            path = path + "/VerificationInfo.db"
            print(path)
            queue = FMDatabaseQueue(path: path)
            
            let sql = "create table IF NOT EXISTS VerificationInfo(id integer primary key autoincrement,username text not null,appkey text not null,nickname text,resaon text,state integer)"
            queue?.inDatabase { (db) in
                db?.executeUpdate(sql, withArgumentsIn: nil)
            }
        }
    }
    
    func dropTable() {
        if queue == nil {
            _createDB()
            dropTable()
        }
        let sql = "drop table if exists VerificationInfo"
        queue?.inDatabase { (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        }
    }
    
    func updateData(_ info: JCVerificationInfo) {
        if queue == nil {
            _createDB()
            insertData(info)
        }
        let sql = "UPDATE VerificationInfo SET nickname='\(info.nickname)', state = \(String(describing: info.state))  WHERE username='\(info.username)' and appkey='\(info.appkey)' and state != 2"
        queue?.inDatabase { (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        }
    }
    
    func insertData(_ info: JCVerificationInfo) {
        if queue == nil {
            _createDB()
            insertData(info)
        }
        let delSql = "delete from VerificationInfo where username='\(info.username)' and appkey='\(info.appkey)' and state=\(String(describing: info.state))"
        queue?.inDatabase { (db) in
            db?.executeUpdate(delSql, withArgumentsIn: nil)
        }
        let sql = "insert into VerificationInfo (username,nickname,appkey,resaon,state) values ('\(info.username)','\(info.nickname)','\(info.appkey)','\(info.resaon)',\(String(describing: info.state)))"
        queue?.inDatabase { (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        }
    }
    
    func delete(_ info: JCVerificationInfo) {
        if queue == nil {
            _createDB()
            insertData(info)
        }
        let delSql = "delete from VerificationInfo where id=\(String(describing: info.id))"
        queue?.inDatabase { (db) in
            db?.executeUpdate(delSql, withArgumentsIn: nil)
        }
    }
    
    func quaryData() -> [JCVerificationInfo] {
        if queue == nil {
            _createDB()
            let _ = quaryData()
        }
        var infos: [JCVerificationInfo] = []
        let sql = "select * from VerificationInfo ORDER BY id DESC"
        queue?.inDatabase { (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())! {
                let info = JCVerificationInfo()
                info.id = Int((resultSet?.int(forColumn: "id"))!)
                info.username = (resultSet?.string(forColumn: "username"))!
                info.nickname = (resultSet?.string(forColumn: "nickname"))!
                info.appkey = (resultSet?.string(forColumn: "appkey"))!
                info.resaon = (resultSet?.string(forColumn: "resaon"))!
                info.state = Int((resultSet?.int(forColumn: "state"))!)
                infos.append(info)
            }
        }
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
