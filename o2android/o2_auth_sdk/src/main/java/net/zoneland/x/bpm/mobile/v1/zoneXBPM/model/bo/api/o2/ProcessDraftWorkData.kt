package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2

import java.io.Serializable


/**
 * Created by fancyLou on 2020-04-21.
 * Copyright © 2020 O2. All rights reserved.
 */

/**
 *  "work": {
"id": "",
"title": "流程开发测试-无标题",
"creatorPerson": "楼国栋@louguodong@P",
"creatorIdentity": "楼国栋@6fef54b9-e195-4609-b030-aa446a4164f3@I",
"creatorUnit": "开发部@22@U",
"application": "d18a3210-22f2-431c-af29-dbb4f6e8ae82",
"applicationName": "流程开发",
"applicationAlias": "",
"process": "4be0e389-a69b-466d-a725-97f9a189d7ff",
"processName": "流程开发测试",
"processAlias": "",
"workCreateType": "surface",
"form": "7d0d8ac2-7c8c-4bcc-9e2f-6d436a7a39b5",
"properties": {
"manualForceTaskIdentityList": [],
"manualEmpowerMap": {},
"serviceValue": {}
}
}
 */
class ProcessDraftWorkData (
    var id: String = "",
    var title: String = "",
    var creatorPerson: String = "",
    var creatorIdentity: String = "",
    var creatorUnit: String = "",
    var application: String = "",
    var applicationName: String = "",
    var applicationAlias: String = "",
    var process: String = "",
    var processName: String = "",
    var processAlias: String = "",
    var workCreateType: String = "",
    var form: String = ""
): Serializable

class ProcessDraftData(
        var work: ProcessDraftWorkData = ProcessDraftWorkData()
)