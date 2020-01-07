//
//  CreateTables.swift
//  O2OA
//
//  Created by FancyLou on 2019/9/15.
//  Copyright © 2019 O2OA. All rights reserved.
//

import FMDB
import CocoaLumberjack

class CreateTables {
    public static let TBALE_NAME_RUN_USER = "o2_runUser"
    public static let TBALE_NAME_RUN_TRACK = "o2_runTrack"
    public static let TBALE_NAME_RUN_TRACK_POINT = "o2_runTrackPoint"
    public static let TBALE_NAME_O2_APP = "o2_appInfo"
    public static let TBALE_NAME_O2_CLOUD_FILE = "o2_cloud_file"
    public static let TABLE_NAME_O2_CONTACT_FAV = "o2_contact_favorite"
    


    private var dbQueue: FMDatabaseQueue!

    init(dbQueue: FMDatabaseQueue) {
        self.dbQueue = dbQueue
    }

    func createTables() {
        DDLogDebug("begin create tables 。。。。")
        self.createTableIfNotExist(tableName: CreateTables.TBALE_NAME_RUN_USER)
        self.createTableIfNotExist(tableName: CreateTables.TBALE_NAME_RUN_TRACK)
        self.createTableIfNotExist(tableName: CreateTables.TBALE_NAME_RUN_TRACK_POINT)
        self.createTableIfNotExist(tableName: CreateTables.TBALE_NAME_O2_APP)
        self.createTableIfNotExist(tableName: CreateTables.TBALE_NAME_O2_CLOUD_FILE)
        self.createTableIfNotExist(tableName: CreateTables.TABLE_NAME_O2_CONTACT_FAV)
        DDLogDebug("end create tables 。。。。")
    }

    private func createTableIfNotExist(tableName: String) {
        self.dbQueue.inDatabase { (db) in
            if !self.checkTableExist(tableName: tableName, db: db) {
                let createSql = self.createTableSqlByTableName(tableName: tableName)
                if createSql != "" {
                    if let result = db?.executeUpdate(createSql, withArgumentsIn: []) {
                        if result {
                            DDLogDebug("创建表\(tableName) 成功！")
                        } else {
                            DDLogDebug("创建表\(tableName) 失败！")
                        }
                    } else {
                        DDLogDebug("创建表\(tableName) 失败！")
                    }

                } else {
                    DDLogDebug("创建表\(tableName) 失败 没有sql语句！")
                }
            } else {
                DDLogDebug("表\(tableName) 已存在！！")
            }
        }
    }


    private func checkTableExist(tableName: String, db: FMDatabase?) -> Bool {
        let sql = self.checkTableNameSql(tableName: tableName)
        var c = 0
        if let result = db?.executeQuery(sql, withArgumentsIn: []) {
            if result.next() {
                c = result.long(forColumnIndex: 0)
            }
            result.close()
        }
        DDLogDebug("table exist， count： \(c)")
        return c > 0
    }

    private func createTableSqlByTableName(tableName: String) -> String {
        switch tableName {
        case CreateTables.TBALE_NAME_RUN_USER:
            return self.createO2RunUserTableSql()
        case CreateTables.TBALE_NAME_RUN_TRACK:
            return self.createO2RunTrackTableSql()
        case CreateTables.TBALE_NAME_RUN_TRACK_POINT:
            return self.createO2RunTrackPointTableSql()
        case CreateTables.TBALE_NAME_O2_APP:
            return self.createO2AppTableSql()
        case CreateTables.TBALE_NAME_O2_CLOUD_FILE:
            return self.createO2CloudFileSql()
        case CreateTables.TABLE_NAME_O2_CONTACT_FAV:
            return self.createO2ContactFavoriteSql()
        default:
            return ""
        }
    }

    private func checkTableNameSql(tableName: String) -> String {
        return "SELECT COUNT(*) FROM sqlite_master where type=\"table\" and name=\"\(tableName)\""
    }

    private func createO2RunUserTableSql() -> String {
        return "CREATE TABLE IF NOT EXISTS  \(CreateTables.TBALE_NAME_RUN_USER) (\"userDN\"  TEXT PRIMARY KEY, \"weight\"  REAL,\"totalDistance\"  REAL,\"totalRunTimes\"  INTEGER,\"totalUseTime\" INTEGER,\"totalCalories\" INTEGER ,\"updateTime\"  INTEGER )"
    }

    private func createO2RunTrackTableSql() -> String {
        return "CREATE TABLE IF NOT EXISTS  \(CreateTables.TBALE_NAME_RUN_TRACK) ( \"id\"  TEXT PRIMARY KEY,  \"userDN\" TEXT NOT NULL, \"runDate\"  INTEGER NOT NULL,\"startTime\"  INTEGER NOT NULL,\"endTime\" INTEGER NOT NULL,\"useTime\" INTEGER ,\"createTime\" INTEGER ,\"distance\"  REAL,\"calories\"  INTEGER,\"stepNumber\"  INTEGER,\"speed\"  REAL,\"entityName\" TEXT)"
    }

    private func createO2RunTrackPointTableSql() -> String {
        return "CREATE TABLE IF NOT EXISTS  \(CreateTables.TBALE_NAME_RUN_TRACK_POINT) ( \"id\"  TEXT PRIMARY KEY ,  \"runId\" TEXT NOT NULL,\"pointTime\"  INTEGER ,\"createTime\"  INTEGER ,\"longitude\"  REAL, \"latitude\"  REAL )"
    }
    
    private func createO2AppTableSql() -> String {
        return "create table IF NOT EXISTS \(CreateTables.TBALE_NAME_O2_APP) (`id` integer primary key autoincrement,`title` text not null,`appid` text not null,`storyboard` text,`vcname` text,`segueidentifier` text,`normalicon` text,`selectedicon` text,`order` integer,`mainorder` integer,`categorytype` integer)"
    }
    
    private func createO2CloudFileSql() -> String {
        return "create table IF NOT EXISTS \(CreateTables.TBALE_NAME_O2_CLOUD_FILE) (`fileid` TEXT PRIMARY KEY,`filename` text not null,`filepath` text not null,`fileext` text)"
    }
    
    private func createO2ContactFavoriteSql() -> String {
        return "create table IF NOT EXISTS \(CreateTables.TABLE_NAME_O2_CONTACT_FAV) ( `id` TEXT NOT NULL, `distinguishedName` TEXT NOT NULL, `employee` TEXT, `genderType` TEXT, `lastLoginAddress` TEXT, `lastLoginClient` TEXT, `lastLoginTime` TEXT, `mail` TEXT, `mobile` TEXT, `name` TEXT, `changePasswordTime` TEXT, `superior` TEXT, `signature` TEXT, `pinyin` TEXT, `pinyinInitial` TEXT, `qq` TEXT, `unique` TEXT, `updateTime` TEXT, `weixin` TEXT, `officePhone` TEXT, `boardDate` TEXT, `birthday` TEXT, `orderNumber` INTEGER , `ownerid` TEXT NOT NULL)"
    }
}
