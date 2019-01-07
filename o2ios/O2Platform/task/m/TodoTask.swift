//
//  TodoTask.swift
//  O2Platform
//
//  Created by 刘振兴 on 16/7/27.
//  Copyright © 2016年 zoneland. All rights reserved.
//

import Foundation
import ObjectMapper
struct PageSize{
    
    var id:String="0"
    var count:String = "20"
    
    init(){
        
    }
    
    init(id:String,count:String){
        self.id = id
        self.count = count
    }
    
    mutating func nextPage(_ id:String,count:String="20"){
        self.id = id
        self.count = count
    }
    
    func toDictionary()->Dictionary<String,String>{
        return ["##id##":self.id,"##count##":self.count]
    }
}


class WorkControl: NSObject, Mappable {
    var allowVisit:Bool?
    var allowProcessing:Bool?
    var allowReadProcessing:Bool?
    var allowSave:Bool?
    var allowRetract:Bool?
    var allowDelete:Bool?
    
    private override init(){}
    
    required init?(map: Map) {
        
    }
    func mapping(map: Map) {
        allowVisit <- map["allowVisit"]
        allowProcessing <- map["allowProcessing"]
        allowReadProcessing <- map["allowReadProcessing"]
        allowSave <- map["allowSave"]
        allowRetract <- map["allowRetract"]
        allowDelete <- map["allowDelete"]
    }
}

class TodoTask:NSObject,Mappable {
    
    var id:String?
    var updateTime:String?
    var job:String?
    var title:String?
    var startTime:String?
    var work:String?
    var application:String?
    var applicationName:String?
    var process:String?
    var processName:String?
    var person:String?
    var identity:String?
    var department:String?
    var completed:Bool?
    var workCompleted:String?
    var company:String?
    var activity:String?
    var activityName:String?
    var activityType:String?
    var activityToken:String?
    var routeList:String?
    var routeNameList:String?

    private override init(){}
    
    required init?(map: Map) {
        
    }
    
    func mapping(map: Map) {
        id <- map["id"]
        updateTime <- map["updateTime"]
        job <- map["job"]
        title <- map["title"]
        startTime <- map["startTime"]
        work <- map["work"]
        application <- map["application"]
        applicationName <- map["applicationName"]
        process <- map["process"]
        processName <- map["processName"]
        person <- map["person"]
        identity <- map["identity"]
        department <- map["department"]
        completed <- map["completed"]
        workCompleted <- map["workCompleted"]
        company <- map["company"]
        activity <- map["activity"]
        activityName <- map["activityName"]
        activityType <- map["activityType"]
        activityToken <- map["activityToken"]
        routeList <- map["routeList"]
        routeNameList <- map["routeNameList"]
    }
    
    override var description: String {
        return "task:[\(id),\(title),\(job),\(work)]"
    }
    
}

/// 具体任务
class Process:NSObject,Mappable{
    var id:String?
    var identity:String?
    var allowRapid:Bool?
    var company:String?
    var work:String?
    var startTimeMonth:String?
    var createTime:String?
    var person:String?
    var routeList:[String]?
    var routeNameList:[String]?
    var department:String?
    var startTime:String?
    var updateTime:String?

    required init?(map: Map) {
        
    }
    
    private override init(){}
    
    func mapping(map: Map) {
        id <- map["id"]
        identity <- map["identity"]
        allowRapid <- map["allowRapid"]
        company <- map["company"]
        work <- map["work"]
        startTimeMonth <- map["startTimeMonth"]
        createTime <- map["createTime"]
        person <- map["person"]
        routeList <- map["routeList"]
        routeNameList <- map["routeNameList"]
        department <- map["department"]
        startTime <- map["startTime"]
        updateTime <- map["updateTime"]
    }
}

/// 活动任务
class ActivityTask:NSObject,Mappable {
    var fromActivity:String?
    var fromActivityName:String?
    var fromActivityToken:String?
    var fromActivityType:String?
    var fromTime:String?
    var arrivedActivity:String?
    var arrivedActivityName:String?
    var arrivedActivityToken:String?
    var arrivedActivityType:String?
    var route:String?
    var routeName:String?
    var arrivedTime:String?
    var completed:Bool?
    var connected:Bool?
    var duration:Int64?
    var createTime:String?
    var currentTaskIndex:Int?
    var id:String?
    var splitTokenList:[AnyObject]?
    var splitting:Int?
    var taskCompletedList:[AnyObject]?
    var taskList:[Process]?
    var updateTime:String?

    
    required init?(map: Map) {
        
    }
    
    private override init(){}
    
    func mapping(map: Map) {
        fromActivity <- map["fromActivity"]
        fromActivityName <- map["fromActivityName"]
        fromActivityToken <- map["fromActivityToken"]
        fromActivityType <- map["fromActivityType"]
        fromTime <- map["fromTime"]
        arrivedActivity <- map["arrivedActivity"]
        arrivedActivityName <- map["arrivedActivityName"]
        arrivedActivityToken <- map["arrivedActivityToken"]
        arrivedActivityType <- map["arrivedActivityType"]
        route <- map["route"]
        routeName <- map["routeName"]
        arrivedTime <- map["arrivedTime"]
        completed <- map["completed"]
        connected <- map["connected"]
        duration <- map["duration"]
        createTime <- map["createTime"]
        currentTaskIndex <- map["currentTaskIndex"]
        id <- map["id"]
        splitTokenList <- map["splitTokenList"]
        splitting <- map["splitting"]
        taskCompletedList <- map["taskCompletedList"]
        taskList <- map["taskList"]
        updateTime <- map["updateTime"]
    }
}


/**
 *  todoTaskCell数据模型
 */
struct TodoCellModel<T> {
    
    var title:String?
    var applicationName:String?
    var status:String?
    var time:String?
    var sourceObj:T?
    
}

struct TaskProcess {
    
    var taskDict:[String:AnyObject]?
    var workDict:[String:AnyObject]?
    var businessDataDict:[String:AnyObject]?
    var taskId:String?
    var workId:String?
    var processData:String?
    var decisonList:[String]?
    var decisionRoute:String?
    var decisionIdea:String?
    var opinion: String?
    
    init(){
        
    }
}

/**
 *  已办列表打开模型
 */
class TodoedActionModel {
//    @property (nonatomic,copy) NSString *destText;
//    @property (nonatomic,copy) NSString *workType;
//    @property (nonatomic,copy) NSString *work_id;
    var destText:String?
    var workType:String?
    var workId:String?
    
    init(destText:String?,workType:String?,workId:String?){
        self.destText = destText
        self.workType = workType
        self.workId = workId
    }
}

/**
 *  处理过程日志显示模型
 */
struct TodoedStatusModel {
    var activity:String?
    var identity:String?
    var status:String?
    var statusTime:String?
}


