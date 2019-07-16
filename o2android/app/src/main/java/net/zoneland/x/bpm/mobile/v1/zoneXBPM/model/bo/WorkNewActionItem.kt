package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo

/**
 * Created by fancyLou on 2019-05-21.
 * Copyright © 2019 O2. All rights reserved.
 */


class WorkNewActionItem(
        var id: String = "",
        var text: String = "",
        var action: String = "",
        var control: String = "", // 工作默认操作
        var actionScript: String = "", //其他操作
        var title: String = "",
        var read: Boolean = true
)