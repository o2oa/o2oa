package com.x.mind.assemble.control.queue;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;

public class MessageWo {

    @FieldDescribe("主贴ID或者回复ID")
    private String id;

    @FieldDescribe("标题：主题或者回复的标题")
    private String title;

    @FieldDescribe("操作者")
    private String createPerson;

    @FieldDescribe("操作时间")
    private Date createTime;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getCreatePerson() {
        return this.createPerson;
    }

    public void setCreatePerson(final String createPerson) {
        this.createPerson = createPerson;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
}
