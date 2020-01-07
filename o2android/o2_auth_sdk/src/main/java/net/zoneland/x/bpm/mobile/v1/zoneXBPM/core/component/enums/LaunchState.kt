package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums

/**
 * Created by fancyLou on 2018/11/30.
 * Copyright © 2018 O2. All rights reserved.
 */

enum class LaunchState {
    ConnectO2Collect,
    ConnectO2Server,
    CheckMobileConfig,
    DownloadMobileConfig,
    AutoLogin,
    NoBindError,
    NoLoginError,
    UnknownError,
    Success
}