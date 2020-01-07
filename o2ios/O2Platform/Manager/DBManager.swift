//
//  DBManager.swift
//  O2OA
//
//  Created by FancyLou on 2019/9/16.
//  Copyright © 2019 O2OA. All rights reserved.
//

import FMDB
import CocoaLumberjack
import Promises

class DBManager {
    static let DB_NAME = "O2_FMDB.db"

    static let shared: DBManager = {
        return DBManager()
    }()

    var queue: FMDatabaseQueue

    private init() {
        var docPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first
        docPath?.append("/\(DBManager.DB_NAME)")
        self.queue = FMDatabaseQueue(path: docPath)
        let tableCreate = CreateTables(dbQueue: self.queue)
        tableCreate.createTables()
    }


    // MARK: - o2 CloudFile 的操作

    func queryCloudFile(fileId: String) -> Promise<O2CloudFileInfo> {
        return Promise { fulfill, reject in
            let sql = "SELECT * FROM \(CreateTables.TBALE_NAME_O2_CLOUD_FILE) WHERE fileid = '\(fileId)'"
            self.queue.inDatabase({ (db) in
                if let resultSet = db?.executeQuery(sql, withArgumentsIn: nil) {
                    if resultSet.next() {
                        let fileInfo = O2CloudFileInfo()
                        fileInfo.fileId = resultSet.string(forColumn: "fileid")
                        fileInfo.fileName = resultSet.string(forColumn: "filename")
                        fileInfo.filePath = resultSet.string(forColumn: "filepath")
                        fileInfo.fileExt = resultSet.string(forColumn: "fileext")
                        fulfill(fileInfo)
                    } else {
                        reject(O2DBError.EmptyResultError)
                    }
                    resultSet.close()
                } else {
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }

    func insertCloudFile(info: O2CloudFileInfo) -> Promise<Bool> {
        return Promise { fulfill, reject in
            let sql = "insert into \(CreateTables.TBALE_NAME_O2_CLOUD_FILE) (fileid, filename, filepath, fileext) values (?, ?, ?, ?)"
            self.queue.inDatabase { (db) in
                if let result = db?.executeUpdate(sql, withArgumentsIn: [info.fileId!, info.fileName!, info.filePath!, info.fileExt ?? ""]) {
                    fulfill(result)
                } else {
                    reject(O2DBError.ExecuteError)
                }
            }
        }
    }
    
    // MARK: - o2 通讯录收藏相关的操作
    
    func insertContactData(_ person:PersonV2,_ ownerid:String) {
        let sSql = "select count(*) from  \(CreateTables.TABLE_NAME_O2_CONTACT_FAV) where `id` = \(String(describing: person.id!)) and `ownerid` = '\(ownerid)'"
        let sql = "insert into \(CreateTables.TABLE_NAME_O2_CONTACT_FAV) (`id`,`distinguishedName`,`employee`,`genderType`,`lastLoginAddress`,`lastLoginClient`,`lastLoginTime`,`mail`,`mobile`,`name`,`changePasswordTime`,`superior`,`signature`,`pinyin`,`pinyinInitial`,`qq`,`unique`,`updateTime`,`weixin`,`officePhone`,`boardDate`,`birthday`,`orderNumber`,`ownerid`) values ('\(String(describing: person.id!))','\(String(describing: person.distinguishedName!))','\(String(describing: person.employee ?? ""))','\(String(describing: person.genderType ?? ""))','\(String(describing: person.lastLoginAddress ?? ""))','\(String(describing: person.lastLoginClient ?? ""))','\(String(describing: person.lastLoginTime ?? ""))','\(String(describing: person.mail ?? ""))','\(String(describing: person.mobile ?? ""))','\(String(describing: person.name ?? ""))','\(String(describing: person.changePasswordTime ?? ""))','\(String(describing: person.superior ?? ""))','\(String(describing: person.signature ?? ""))','\(String(describing: person.pinyin ?? ""))','\(String(describing: person.pinyinInitial ?? ""))','\(String(describing: person.qq ?? ""))','\(String(describing: person.unique ?? ""))','\(String(describing: person.updateTime ?? ""))','\(String(describing: person.weixin ?? ""))','\(String(describing: person.officePhone ?? ""))','\(String(describing: person.boardDate ?? ""))','\(String(describing: person.birthday ?? ""))',\(String(describing: person.orderNumber ?? 0)),'\(ownerid)')"
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sSql, withArgumentsIn: nil)
            if let _  = (resultSet?.next()) {
                DDLogError("已存在，无法收藏。。。")
            }else{
                db?.executeUpdate(sql, withArgumentsIn: nil)
            }
        })
         
    }
    
    
    func isCollect(_ person:PersonV2,_ ownerid:String) -> Bool{
        var returnValue = false
        let sql = "SELECT COUNT(*) as collectNum FROM \(CreateTables.TABLE_NAME_O2_CONTACT_FAV) WHERE `id` = '\(String(describing: person.id!))' AND `ownerid` = '\(ownerid)' "
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            if let _  = (resultSet?.next()) {
                if Int(resultSet?.int(forColumn: "collectNum") ?? 0) > 0 {
                    returnValue = true
                }else {
                    returnValue = false
                }
            }else{
                returnValue = false
            }
        })
        return returnValue
    }
    
    func deleteContactData(_ person:PersonV2,_ ownerid:String){
        let sql = "delete from \(CreateTables.TABLE_NAME_O2_CONTACT_FAV) where `id` = '\(String(describing: person.id!))' and `ownerid` = '\(ownerid)'"
        self.queue.inDatabase({ (db) in
            db?.executeUpdate(sql, withArgumentsIn: nil)
        })
    }
    
    func queryContactData(_ ownerid:String) -> [PersonV2] {
        DDLogDebug("queryContactData .....")
        var persons:[PersonV2] = []
        let sql = "SELECT * FROM \(CreateTables.TABLE_NAME_O2_CONTACT_FAV) WHERE `ownerid` = '\(ownerid)'"
        self.queue.inDatabase({ (db) in
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
                DDLogDebug("person name \(p.name ?? "")")
            }
        })
        return persons
    }
    


    // MARK: - o2app相关的操作

    func updateData(_ info: O2App, _ categoryType: Int) {
        let sql = "UPDATE \(CreateTables.TBALE_NAME_O2_APP) SET title=?,storyboard=?,vcname=?,segueidentifier=?,normalicon=?,selectedicon=?,`order`=?,mainorder=?,categorytype=? where appid=?"
        let argumentsArray = [(info.title ?? ""), (info.storyBoard ?? ""), (info.vcName ?? ""), (info.segueIdentifier ?? ""), (info.normalIcon ?? ""), (info.selectedIcon ?? ""), info.order, info.mainOrder, categoryType, info.appId!] as [Any]
        self.queue.inDatabase({ (db) in
            db?.executeUpdate(sql, withArgumentsIn: argumentsArray)
        })
    }

    func insertData(_ info: O2App) {
        let sSql = "select count(*) as appnumber,`categorytype` from \(CreateTables.TBALE_NAME_O2_APP) where appid = '\(String(describing: info.appId!))'"
        let insertSql = "INSERT INTO \(CreateTables.TBALE_NAME_O2_APP)(appid,title,storyboard,vcname,segueidentifier,normalicon,selectedicon,`order`,mainOrder,categorytype) values (?,?,?,?,?,?,?,?,?,?)"
        let argumentsArray1 = [info.appId!, (info.title ?? ""), (info.storyBoard ?? ""), (info.vcName ?? ""), (info.segueIdentifier ?? ""), (info.normalIcon ?? ""), (info.selectedIcon ?? ""), info.order, info.mainOrder, 0] as [Any]
        var isUpdate = false
        var categoryType = 0
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sSql, withArgumentsIn: nil)
            if let _ = (resultSet?.next()) {
                if Int(resultSet?.int(forColumn: "appnumber") ?? 0) > 0 {
                    isUpdate = true
                    categoryType = Int(resultSet?.int(forColumn: "categorytype") ?? 0)
                } else {
                    isUpdate = false
                    db?.executeUpdate(insertSql, withArgumentsIn: argumentsArray1)
                }
            } else {
                db?.executeUpdate(insertSql, withArgumentsIn: argumentsArray1)
            }
        })
        if isUpdate == true {
            updateData(info, categoryType)
        }
    }

    func removeAll() {
        let delSql = "DELETE FROM \(CreateTables.TBALE_NAME_O2_APP) WHERE 1=1 "
        self.queue.inDatabase({ (db) in
            db?.executeUpdate(delSql, withArgumentsIn: nil)
        })
    }

    func deleteNotExistApp(_ exitApps: [String]) {
        var infos: [O2App] = []
        let sql = "select * from \(CreateTables.TBALE_NAME_O2_APP) ORDER BY `order` ASC"
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())! {
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
                    if !exitApps.contains(appId) { // 不存在就删除
                        let delSql = "delete from \(CreateTables.TBALE_NAME_O2_APP) where appid = '\(appId)'"
                        db?.executeUpdate(delSql, withArgumentsIn: nil)
                    }
                }
            })
        })

    }

    func delete(_ info: O2App) {
        let delSql = "delete from \(CreateTables.TBALE_NAME_O2_APP) where appid = '\(String(describing: info.appId!))'"
        self.queue.inDatabase({ (db) in
            db?.executeUpdate(delSql, withArgumentsIn: nil)
        })
    }

    func queryData(_ appId: String) -> O2App? {
        var homeApp: O2App?
        let sql = "SELECT * FROM \(CreateTables.TBALE_NAME_O2_APP) WHERE appid = '\(appId)'"
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while(resultSet?.next())! {
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
        var infos: [O2App] = []
        let sql = "select * from \(CreateTables.TBALE_NAME_O2_APP) ORDER BY `order` ASC"
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())! {
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
        var infos: [O2App] = []
        let sql = "select * from \(CreateTables.TBALE_NAME_O2_APP) where categorytype = 0 ORDER BY `order`"
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())! {
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
        var infos: [O2App] = []
        let sql = "select * from \(CreateTables.TBALE_NAME_O2_APP) where categorytype = 1 ORDER BY `mainorder`"
        self.queue.inDatabase({ (db) in
            let resultSet = db?.executeQuery(sql, withArgumentsIn: nil)
            while (resultSet?.next())! {
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







    // MARK: - Run 相关的操作
    //创建跑步用户
    func initRunUserInfo(userDN: String) -> Promise<Bool> {
        return Promise { fulfill, reject in
            let sql = "insert into \(CreateTables.TBALE_NAME_RUN_USER) (userDN, weight, totalDistance, totalRunTimes, totalUseTime, totalCalories, updateTime) values (?, ?, ?, ?, ?, ?, ?)"
            self.queue.inDatabase { (db) in
                let updateTime = Date().timeIntervalSince1970
                if let result = db?.executeUpdate(sql, withArgumentsIn: [userDN, 0.0, 0.0, 0, 0, 0, updateTime]) {
                    fulfill(result)
                } else {
                    reject(O2DBError.ExecuteError)
                }
            }
        }
    }

    //更新用户体重信息
    func updateRunUserWeight(userDN: String, weight: Double) -> Promise<Bool> {
        DDLogDebug("userDN:\(userDN) weight:\(weight)")
        return Promise { fulfill, reject in
            let updateTime = Date().timeIntervalSince1970
            let updateSql = "update \(CreateTables.TBALE_NAME_RUN_USER)  set weight = ?,  updateTime = ? where userDN = \"\(userDN)\" "
            self.queue.inDatabase({ (db) in
                if let result = db?.executeUpdate(updateSql, withArgumentsIn: [weight, updateTime]) {
                    if result {
                        DDLogDebug("更新个人体重数据成功！")
                    } else {
                        DDLogError("更新个人体重数据失败！")
                    }
                    fulfill(result)
                } else {
                    DDLogError("更新个人跑步数据异常！")
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }

    //将某一次跑步数据更新到用户的总表中
    func updateRunUserWithRunTrackInfo(userDN: String, distance: Double, useTime: Int, calories: Int) -> Promise<Bool> {
        return Promise { fulfill, reject in
            let sql = "select *  from \(CreateTables.TBALE_NAME_RUN_USER) where userDN = \"\(userDN)\" "
            let updateSql = "update \(CreateTables.TBALE_NAME_RUN_USER)  set totalDistance = ? , totalRunTimes = ?, totalUseTime = ?, totalCalories = ?,  updateTime = ? where userDN = \"\(userDN)\" "
            self.queue.inDatabase({ (db) in
                if let queryResult = db?.executeQuery(sql, withArgumentsIn: []) {
                    if queryResult.next() {
                        let totalDistance = queryResult.double(forColumn: "totalDistance")
                        let totalRunTimes = queryResult.long(forColumn: "totalRunTimes")
                        let totalUseTime = queryResult.long(forColumn: "totalUseTime")
                        let totalCalories = queryResult.long(forColumn: "totalCalories")
                        let updateTime = Date().timeIntervalSince1970
                        if let result = db?.executeUpdate(updateSql, withArgumentsIn: [totalDistance + distance, totalRunTimes + 1, totalUseTime + useTime, totalCalories + calories, updateTime]) {
                            if result {
                                DDLogDebug("更新个人跑步数据成功！")
                            } else {
                                DDLogError("更新个人跑步数据失败！")
                            }
                            fulfill(result)
                        } else {
                            DDLogError("更新个人跑步数据异常！")
                            reject(O2DBError.ExecuteError)
                        }
                    } else {
                        DDLogError("没有查询到用户对象，id:\(userDN)")
                        reject(O2DBError.EmptyResultError)
                    }
                    queryResult.close()
                } else {
                    DDLogError("查询个人跑步数据异常！")
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }
    //根据dn查询用户对象
    func queryRunUserInfoByDN(userDN: String) -> Promise<O2RunUserInfo> {
        return Promise { fulfill, reject in
            let sql = "select *  from \(CreateTables.TBALE_NAME_RUN_USER) where userDN = ? "
            self.queue.inDatabase({ (db) in
                if let result = db?.executeQuery(sql, withArgumentsIn: [userDN]) {
                    if result.next() {
                        let user = O2RunUserInfo()
                        user.updateTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "updateTime")))
                        user.weight = result.double(forColumn: "weight")
                        user.totalCalories = result.long(forColumn: "totalCalories")
                        user.totalDistance = result.double(forColumn: "totalDistance")
                        user.totalUseTime = result.long(forColumn: "totalUseTime")
                        user.totalRunTimes = result.long(forColumn: "totalRunTimes")
                        user.userDN = result.string(forColumn: "userDN")
                        fulfill(user)
                    } else {
                        DDLogDebug("用户\(userDN) 不存在！")
                        let insertSql = "insert into \(CreateTables.TBALE_NAME_RUN_USER) (userDN, weight, totalDistance, totalRunTimes, totalUseTime, totalCalories, updateTime) values (?, ?, ?, ?, ?, ?, ?)"
                        let updateTime = Date().timeIntervalSince1970
                        if let insertResult = db?.executeUpdate(insertSql, withArgumentsIn: [userDN, 0.0, 0.0, 0, 0, 0, updateTime]) {
                            if insertResult {
                                DDLogDebug("创建一个新用户！")
                                if let newResult = db?.executeQuery(sql, withArgumentsIn: [userDN]) {
                                    if newResult.next() {
                                        let user = O2RunUserInfo()
                                        user.updateTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "updateTime")))
                                        user.weight = result.double(forColumn: "weight")
                                        user.totalCalories = result.long(forColumn: "totalCalories")
                                        user.totalDistance = result.double(forColumn: "totalDistance")
                                        user.totalUseTime = result.long(forColumn: "totalUseTime")
                                        user.totalRunTimes = result.long(forColumn: "totalRunTimes")
                                        user.userDN = result.string(forColumn: "userDN")
                                        fulfill(user)
                                    } else {
                                        reject(O2DBError.EmptyResultError)
                                    }
                                } else {
                                    reject(O2DBError.ExecuteError)
                                }
                            } else {
                                DDLogDebug("创建新用户失败！")
                                reject(O2DBError.ExecuteError)
                            }
                        } else {
                            reject(O2DBError.ExecuteError)
                        }
                    }
                    result.close()
                } else {
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }


    //插入一次跑步数据
    func insertRunTrackInfo(runInfo: O2RunTrackInfo) -> Promise<String> {
        return Promise { fulfill, reject in
            let sql = "insert into \(CreateTables.TBALE_NAME_RUN_TRACK) (id, userDN, runDate, startTime, endTime, useTime, distance, calories, stepNumber, speed, entityName, createTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            let id = UUID().uuidString
            let createTime = Date()
            let array: [Any] = [
                id,
                runInfo.userDN ?? "",
                runInfo.runDate?.timeIntervalSince1970 ?? 0,
                runInfo.startTime?.timeIntervalSince1970 ?? 0,
                runInfo.endTime?.timeIntervalSince1970 ?? 0,
                runInfo.useTime ?? 0,
                runInfo.distance ?? 0.0,
                runInfo.calories ?? 0,
                runInfo.stepNumber ?? 0,
                runInfo.speed ?? 0.0,
                runInfo.entityName ?? "",
                createTime.timeIntervalSince1970
            ]
            self.queue.inDatabase({ (db) in
                if let result = db?.executeUpdate(sql, withArgumentsIn: array) {
                    if result {
                        fulfill(id)
                    } else {
                        reject(O2DBError.ExecuteError)
                    }
                } else {
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }

    //插入跑步的时候的位置点集合
    func insertRunTrackPoints(points: [O2RunTrackPointInfo]) -> Promise<[O2RunTrackPointInfo]> {
        return Promise { fulfill, reject in
            let sql = "insert into \(CreateTables.TBALE_NAME_RUN_TRACK_POINT) (id, runId, latitude, longitude, pointTime, createTime) values (?, ?, ?, ?, ?, ?)"
            var errorInsert: [O2RunTrackPointInfo] = []
            self.queue.inDatabase { (db) in
                let createTime = Date()
                for point in points {
                    let id = UUID().uuidString
                    let pArray: [Any] = [
                        id,
                        point.runId ?? "",
                        point.latitude ?? 0.0,
                        point.longitude ?? 0.0,
                        point.pointTime?.timeIntervalSince1970 ?? 0,
                        createTime.timeIntervalSince1970
                    ]
                    if let result = db?.executeUpdate(sql, withArgumentsIn: pArray) {
                        if !result {
                            errorInsert.append(point)
                        }
                    } else {
                        errorInsert.append(point)
                    }
                }
                fulfill(errorInsert)
            }
        }
    }

    //查询当个跑步对象 包含位置信息
    func queryRunTrackWithPoints(id: String) -> Promise<O2RunTrackFullData> {
        return Promise { fulfill, reject in
            let sql = "select * from \(CreateTables.TBALE_NAME_RUN_TRACK) where id = ? "
            let pointsSql = "select * from \(CreateTables.TBALE_NAME_RUN_TRACK_POINT) where runId = ?  order by pointTime "
            self.queue.inDatabase({ (db) in
                if let result = db?.executeQuery(sql, withArgumentsIn: [id]) {
                    if result.next() {
                        let data = O2RunTrackFullData()
                        data.id = result.string(forColumn: "id")
                        data.userDN = result.string(forColumn: "userDN")
                        data.createTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "createTime")))
                        data.entityName = result.string(forColumn: "entityName")
                        data.runDate = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "runDate")))
                        data.startTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "startTime")))
                        data.endTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "endTime")))
                        data.distance = result.double(forColumn: "distance")
                        data.speed = result.double(forColumn: "speed")
                        data.calories = result.long(forColumn: "calories")
                        data.useTime = result.long(forColumn: "useTime")
                        data.stepNumber = result.long(forColumn: "stepNumber")

                        if let pointsResult = db?.executeQuery(pointsSql, withArgumentsIn: [id]) {
                            var points: [O2RunTrackPointInfo] = []
                            while(pointsResult.next()) {
                                let point = O2RunTrackPointInfo()
                                point.id = pointsResult.string(forColumn: "id")
                                point.runId = id
                                point.pointTime = Date.init(timeIntervalSince1970: Double(pointsResult.long(forColumn: "pointTime")))
                                point.createTime = Date.init(timeIntervalSince1970: Double(pointsResult.long(forColumn: "createTime")))
                                point.latitude = pointsResult.double(forColumn: "latitude")
                                point.longitude = pointsResult.double(forColumn: "longitude")
                                points.append(point)
                            }
                            data.points = points
                            pointsResult.close()
                        } else {
                            DDLogError("没有查询到位置信息列表")
                        }
                        fulfill(data)
                        result.close()
                    } else {
                        DDLogDebug("没有查询到跑步对象。。。。")
                        reject(O2DBError.EmptyResultError)
                    }
                } else {
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }
    //查询全部的的跑步记录
    func queryRunTrackAllList() -> Promise<[O2RunTrackInfo]> {
        return Promise { fulfill, reject in
            let sql = "select * from \(CreateTables.TBALE_NAME_RUN_TRACK)  order by  createTime desc "
            self.queue.inDatabase({ (db) in
                if let result = db?.executeQuery(sql, withArgumentsIn: []) {
                    var array: [O2RunTrackInfo] = []
                    while result.next() {
                        let data = O2RunTrackInfo()
                        data.id = result.string(forColumn: "id")
                        data.userDN = result.string(forColumn: "userDN")
                        data.createTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "createTime")))
                        data.entityName = result.string(forColumn: "entityName")
                        data.runDate = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "runDate")))
                        data.startTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "startTime")))
                        data.endTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "endTime")))
                        data.distance = result.double(forColumn: "distance")
                        data.speed = result.double(forColumn: "speed")
                        data.calories = result.long(forColumn: "calories")
                        data.useTime = result.long(forColumn: "useTime")
                        data.stepNumber = result.long(forColumn: "stepNumber")
                        array.append(data)
                    }
                    fulfill(array)
                    result.close()
                } else {
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }

    //查询时间段内 跑步列表数据
    func queryRunTrackListByTime(startTime: Date, endTime: Date) -> Promise<[O2RunTrackInfo]> {
        return Promise { fulfill, reject in
            let sql = "select * from \(CreateTables.TBALE_NAME_RUN_TRACK) where runDate > ? and runDate < ? order by  createTime desc  "
            self.queue.inDatabase({ (db) in
                let start = Int(startTime.timeIntervalSince1970)
                let end = Int(endTime.timeIntervalSince1970)
                if let result = db?.executeQuery(sql, withArgumentsIn: [start, end]) {
                    var array: [O2RunTrackInfo] = []
                    while result.next() {
                        let data = O2RunTrackInfo()
                        data.id = result.string(forColumn: "id")
                        data.userDN = result.string(forColumn: "userDN")
                        data.createTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "createTime")))
                        data.entityName = result.string(forColumn: "entityName")
                        data.runDate = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "runDate")))
                        data.startTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "startTime")))
                        data.endTime = Date.init(timeIntervalSince1970: Double(result.long(forColumn: "endTime")))
                        data.distance = result.double(forColumn: "distance")
                        data.speed = result.double(forColumn: "speed")
                        data.calories = result.long(forColumn: "calories")
                        data.useTime = result.long(forColumn: "useTime")
                        data.stepNumber = result.long(forColumn: "stepNumber")
                        array.append(data)
                    }
                    fulfill(array)
                    result.close()
                } else {
                    reject(O2DBError.ExecuteError)
                }
            })
        }
    }
}

enum O2DBError: Error {
    case ExecuteError
    case EmptyResultError
    case EmptyRowIdError
    case UnkownError
}

extension O2DBError: LocalizedError {
    var errorDescription: String? {
        get {
            switch self {
            case .ExecuteError:
                return "执行sql语言出错！"
            case .EmptyResultError:
                return "查询数据结果为空！"
            case .EmptyRowIdError:
                return "没有获取到自增ID！"
            case .UnkownError:
                return "未知错误"
            }
        }
    }

}
