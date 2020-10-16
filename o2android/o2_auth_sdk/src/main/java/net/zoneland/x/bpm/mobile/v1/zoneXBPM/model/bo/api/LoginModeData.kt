package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api

/**
 * Created by fancyLou on 2020-09-27.
 * Copyright © 2020 O2. All rights reserved.
 */


data class LoginModeData (
        var captchaLogin: Boolean = false, //验证码登录
        var codeLogin: Boolean = false, //短信验证码登录
        var bindLogin: Boolean = false,
        var faceLogin: Boolean = false
)