package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person

/**
 * Created by fancyLou on 2020-09-03.
 * Copyright © 2020 O2. All rights reserved.
 */


class PersonPwdForm(
        var oldPassword: String = "",
        var newPassword: String = "",
        var confirmPassword: String = "",
        var isEncrypted: String = ""
)