package com.x.query.core.entity.index;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class StateProperties extends JsonProperties {

    @FieldDescribe("最后更新对象标识.")
    @Transient
    private List<String> latestIdList = new ArrayList<>();

    public List<String> getLatestIdList() {
        return latestIdList;
    }

    public void setLatestIdList(List<String> latestIdList) {
        this.latestIdList = latestIdList;
    }
}
