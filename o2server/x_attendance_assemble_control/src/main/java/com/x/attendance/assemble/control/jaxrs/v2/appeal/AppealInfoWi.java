package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.List;

public class AppealInfoWi extends GsonPropertyObject {

    private static final long serialVersionUID = 3800681861289573236L;

    @FieldDescribe("申诉的打卡考勤记录ID")
    private String recordId;
    @FieldDescribe("用户标识")
    private List<String> users;
    @FieldDescribe("开始日期")
    private String startDate;
    @FieldDescribe("结束日期")
    private String endDate;
    @FieldDescribe("申诉状态:0-待处理，1-审批中（已发起流程），2-审批通过，3-审批不通过 4-管理员处理")
    private String status;

    public boolean isStatusValid() {
        return status != null && (status.equals("0") || status.equals("1") || status.equals("2") || status.equals("3") || status.equals("4"));
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
