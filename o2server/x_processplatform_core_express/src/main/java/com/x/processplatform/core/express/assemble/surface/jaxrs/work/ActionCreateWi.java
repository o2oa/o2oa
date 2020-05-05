package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionCreateWi extends GsonPropertyObject {

    @FieldDescribe("直接打开指定人员已经有的草稿,草稿判断:工作没有已办,只有一条此人的待办.")
    private Boolean latest;

    @FieldDescribe("标题.")
    private String title;

    @FieldDescribe("启动人员身份.")
    private String identity;

    @FieldDescribe("工作数据.")
    private JsonElement data;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public Boolean getLatest() {
        return latest;
    }

    public void setLatest(Boolean latest) {
        this.latest = latest;
    }

}