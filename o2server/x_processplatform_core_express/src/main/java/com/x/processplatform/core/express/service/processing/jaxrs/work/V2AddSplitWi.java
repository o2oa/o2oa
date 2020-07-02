package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2AddSplitWi extends GsonPropertyObject {

    @FieldDescribe("添加的拆分值.")
    private List<String> splitValueList;

    @FieldDescribe("排除已经存在的拆分值.")
    private Boolean trimExist;

    @FieldDescribe("添加分支的workLog.")
    private String workLog;

    public List<String> getSplitValueList() {
        return splitValueList;
    }

    public void setSplitValueList(List<String> splitValueList) {
        this.splitValueList = splitValueList;
    }

    public Boolean getTrimExist() {
        return trimExist;
    }

    public void setTrimExist(Boolean trimExist) {
        this.trimExist = trimExist;
    }

    public String getWorkLog() {
        return workLog;
    }

    public void setWorkLog(String workLog) {
        this.workLog = workLog;
    }

}