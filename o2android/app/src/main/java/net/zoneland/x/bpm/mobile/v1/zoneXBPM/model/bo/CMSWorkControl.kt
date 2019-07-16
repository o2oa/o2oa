package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo

/**
 * Created by fancyLou on 2019-07-04.
 * Copyright © 2019 O2. All rights reserved.
 */

class CMSWorkControl(
        var allowPublishDocument: Boolean = false,
        var allowSave: Boolean = false,
        var allowEditDocument: Boolean = false,
        var allowDeleteDocument: Boolean = false,
        var allowArchiveDocument: Boolean = false,
        var allowRedraftDocument: Boolean = false
)