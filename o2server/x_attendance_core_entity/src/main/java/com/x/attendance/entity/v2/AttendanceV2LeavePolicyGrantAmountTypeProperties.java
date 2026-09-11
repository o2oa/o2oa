package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2LeavePolicyGrantAmountTypeProperties extends JsonProperties {

    private static final long serialVersionUID = -4872177863446165403L;

    @FieldDescribe("额度发放脚本.")
    private String grantScript;

    public String getGrantScript() {
        return grantScript;
    }

    public void setGrantScript(String grantScript) {
        this.grantScript = grantScript;
    }
}
