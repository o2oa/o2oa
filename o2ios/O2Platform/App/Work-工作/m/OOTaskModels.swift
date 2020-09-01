//
//  OOTaskModels.swift
//  o2app
//
//  Created by 刘振兴 on 2018/3/5.
//  Copyright © 2018年 zone. All rights reserved.
//

import Foundation
import HandyJSON
class O2TaskImageshowEntity:NSObject,DataModel {
    
    var application : String?
    var createTime : String?
    var creator : String?
    var distributeFactor : Int?
    var id : String?
    var infoId : String?
    var picUrl : String?
    var sequence : String?
    var title : String?
    var updateTime : String?
    var url : String?
    var picId:String?
    
    override required init(){}
    
}

class O2TodoTask:NSObject,DataModel {
    @objc var id:String?
    @objc var updateTime:String?
    @objc var job:String?
    @objc var title:String?
    @objc var startTime:String?
    @objc var work:String?
    @objc var application:String?
    @objc var applicationName:String?
    @objc var process:String?
    @objc var processName:String?
    @objc var person:String?
    @objc var identity:String?
    @objc var department:String?
    var completed:Bool?
    @objc var workCompleted:String?
    @objc var company:String?
    @objc var activity:String?
    @objc var activityName:String?
    @objc var activityType:String?
    @objc var activityToken:String?
    @objc var routeList:[String]?
    @objc var routeNameList:[String]?
    
    required override init() {
        
    }
}

class  O2TodoTaskNeural:NSObject,DataModel {
    @objc var routeName: String?
    //var workLogList
    required override init() {
        
    }
}

class O2WorkPostResult:NSObject,DataModel {
    required override init() {
           
       }
    
    @objc var id: String?
    @objc var application: String?
    @objc var process: String?
    @objc var job: String?
    @objc var work: String?
    var completed: Bool?
    @objc var fromActivity: String?
    @objc var fromActivityType: String?
    @objc var fromActivityName: String?
    @objc var fromActivityAlias: String?
    @objc var fromActivityToken: String?
    @objc var recordTime: String?
    @objc var person: String?
    @objc var identity: String?
    @objc var unit: String?
    @objc var type: String?
}

class O2AppProcess: NSObject,DataModel {
    @objc var id:String?
    @objc var name:String?
    @objc var alias:String?
    @objc var desc:String?
    @objc var creatorPerson:String?
    @objc var application:String?
    @objc var icon:String?
    
    func mapping(mapper: HelpingMapper) {
        mapper <<<
         self.desc <-- "description"
    }
    
    required override init() {
        
    }
}

struct O2TaskProcess {
    
    var taskDict:[String:AnyObject]?
    var workDict:[String:AnyObject]?
    var businessDataDict:[String:AnyObject]?
    var taskId:String?
    var workId:String?
    var processData:String?
    var decisonList:[String]?
    var decisionRoute:String?
    var decisionIdea:String?
    
    init(){
        
    }
}

//class  O2Application: NSObject,DataModel {
//    var id:String?
//    var name:String?
//    var alias:String?
//    var applicationCategory:String?
//    var icon:String?
//    var processList:[O2AppProcess]?
//    
//    required override init() {
//        
//    }
//}

