//
//  WorkInfoResData.swift
//  O2Platform
//
//  Created by FancyLou on 2020/9/4.
//  Copyright © 2020 zoneland. All rights reserved.
//

import Foundation
import HandyJSON

class WorkInfoRes: NSObject,DataModel {
    @objc var id: String?
    @objc var job: String?
    @objc var title: String?
    @objc var startTime: String?
    @objc var startTimeMonth: String?
    @objc var creatorPerson: String?
    @objc var creatorIdentity: String?
    @objc var creatorUnit: String?
    @objc var application: String?
    @objc var applicationName: String?
    @objc var applicationAlias: String?
    @objc var process: String?
    @objc var processName: String?
    @objc var processAlias: String?
    @objc var activity: String?
    @objc var activityType: String?
    @objc var activityName: String?
    @objc var activityAlias: String?
    @objc var activityDescription: String?
    @objc var activityToken: String?
    @objc var activityArrivedTime: String?
    @objc var serial: String?
    @objc var workCreateType: String?
    @objc var workStatus: String?
    @objc var manualTaskIdentityText: String?
    @objc var form: String?
    @objc var destinationRoute: String?
    @objc var destinationRouteName: String?
    @objc var destinationActivityType: String?
    @objc var destinationActivity: String?
    
    
    required override init() {
        
    }
}

class WorkInfoResData: NSObject,DataModel {
    @objc var work: WorkInfoRes?
    
    required override init() {
        
    }
}
