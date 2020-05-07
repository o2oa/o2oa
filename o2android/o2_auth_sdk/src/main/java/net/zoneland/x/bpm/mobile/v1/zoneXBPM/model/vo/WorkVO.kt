package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.WorkControlData

/**
 * Created by fancyLou on 02/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


open class WorkVO(

        var createTime: String? = null,

        var updateTime: String? = null,

        var id: String? = null,

        var job: String? = null,

        var title: String? = null,

        var startTime: String? = null,

        var startTimeMonth: String? = null,

        var creatorPerson: String? = null,

        var creatorIdentity: String? = null,

        var creatorUnit: String? = null,

        var application: String? = null,

        var applicationName: String? = null,

        var process: String? = null,

        var processName: String? = null,

        var control: WorkControlData? = null,

        var manualTaskIdentityText: String? = null

)