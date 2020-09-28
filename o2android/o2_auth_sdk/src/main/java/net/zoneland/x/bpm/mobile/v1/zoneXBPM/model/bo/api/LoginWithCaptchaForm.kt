package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api

/**
 * Created by fancyLou on 2020-09-27.
 * Copyright © 2020 O2. All rights reserved.
 */

data class LoginWithCaptchaForm (
        var credential: String = "",
        var password: String = "",
        var captcha: String ="", //图片认证编号id
        var captchaAnswer: String =  "",//图片认证码
        var isEncrypted: String = "" //是否启用加密
)