package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2


/**
 * Created by fancyLou on 2020-09-04.
 * Copyright © 2020 O2. All rights reserved.
 */

data class WorkInfoResData(
        var work: WorkInfoRes? ,
        var workLogList: List<WorkLog> = ArrayList()
)

data class WorkInfoRes(
    var id: String = "",
    var job: String = "",
    var title: String = "",
    var startTime: String = "",
    var startTimeMonth: String = "",
    var creatorPerson: String = "",
    var creatorIdentity: String = "",
    var creatorUnit: String = "",
    var application: String = "",
    var applicationName: String = "",
    var applicationAlias: String = "",
    var process: String = "",
    var processName: String = "",
    var processAlias: String = "",
    var activity: String = "",
    var activityType: String = "",
    var activityName: String = "",
    var activityAlias: String = "",
    var activityDescription: String = "",
    var activityToken: String = "",
    var activityArrivedTime: String = "",
    var serial: String = "",
    var workCreateType: String = "",
    var workStatus: String = "",
    var manualTaskIdentityText: String = "",
    var form: String = "",
    var destinationRoute: String = "",
    var destinationRouteName: String = "",
    var destinationActivityType: String = "",
    var destinationActivity: String = ""
)