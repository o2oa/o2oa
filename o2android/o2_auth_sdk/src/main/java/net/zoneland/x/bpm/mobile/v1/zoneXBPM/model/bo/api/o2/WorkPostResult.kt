package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2

/**
 * Created by fancyLou on 2020-09-01.
 * Copyright © 2020 O2. All rights reserved.
 */
//"id": "4cf5707e-286f-4dfe-9aa9-58dfa1b7068b",
//        "application": "687f8355-fb91-4605-857f-c03e2ede716b",
//        "process": "a2aab7b4-812e-42b2-84ca-2230bf9242cf",
//        "job": "caf280c4-8048-4c99-90dc-0e9fdd41638f",
//        "work": "def24592-cd14-4083-8093-4bd8979dc576",
//        "completed": false,
//        "display": true,
//        "order": 1598944402840,
//        "properties": {
//            "nextManualList": [
//                {
//                    "activity": "5cae04d5-14de-4ea5-ba1e-5e4a7e0b3a27",
//                    "activityType": "manual",
//                    "activityName": "核稿",
//                    "activityAlias": "",
//                    "activityToken": "2f10ea31-9a9d-467d-96d8-f72b25e8ba0d",
//                    "taskIdentityList": [
//                        "楼国栋@b8684062-d6ee-4740-9f2c-d660cda49ca8@I"
//                    ]
//                }
//            ],
//            "nextManualTaskIdentityList": [
//                "楼国栋@b8684062-d6ee-4740-9f2c-d660cda49ca8@I"
//            ],
//            "routeName": "送核稿",
//            "opinion": "哈哈😄",
//            "mediaOpinion": "",
//            "startTime": "2020-08-27 16:01:39",
//            "elapsed": 1211,
//            "fromGroup": "",
//            "fromOpinionGroup": ""
//        },
//        "fromActivity": "02e23421-c048-4582-a04f-47451128e200",
//        "fromActivityType": "manual",
//        "fromActivityName": "拟稿",
//        "fromActivityAlias": "",
//        "fromActivityToken": "005a38b4-3e1f-46a2-9f53-4769ed0f4487",
//        "recordTime": "2020-09-01 15:13:22",
//        "person": "楼国栋@237@P",
//        "identity": "楼国栋@b8684062-d6ee-4740-9f2c-d660cda49ca8@I",
//        "unit": "移动开发组@320494093@U",
//        "type": "task"

data class WorkPostResult(
    var id: String = "",
    var application: String = "",
    var process: String = "",
    var job: String = "",
    var work: String = "",
    var completed: Boolean = false,
    var fromActivity: String = "",
    var fromActivityType: String = "",
    var fromActivityName: String = "",
    var fromActivityAlias: String = "",
    var fromActivityToken: String = "",
    var recordTime: String = "",
    var person: String = "",
    var identity: String = "",
    var unit: String = "",
    var type: String = "",
    var properties: WorkProperties? = null
)


data class WorkProperties(
        var nextManualTaskIdentityList: ArrayList<String> = ArrayList(),
        var routeName: String = "",
        var opinion: String = "",
        var mediaOpinion: String = "",
        var fromGroup: String = "",
        var fromOpinionGroup: String = "",
        var nextManualList: ArrayList<WorkManual> = ArrayList()
)

data class WorkManual(
        var activity: String = "",
        var activityType: String = "",
        var activityName: String = "",
        var activityAlias: String = "",
        var activityToken: String = "",
        var taskIdentityList: ArrayList<String> = ArrayList()
)