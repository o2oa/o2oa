package com.x.cms.core.entity.query;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * 消息通知对象
 *
 * @author sword
 * @date 2022/03/10 16:45
 **/
public class DocumentNotify extends GsonPropertyObject {

    private String documentId;

    @FieldDescribe("消息通知对象：人员、组织或者群组")
    private List<String> notifyPersonList;

    @FieldDescribe("是否根据文档的可见范围发送：true(忽略notifyPersonList的值)|false(默认)")
    private Boolean notifyByDocumentReadPerson;

    @FieldDescribe("是否通知文档创建人：true(默认)|false")
    private Boolean notifyCreatePerson;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getNotifyPersonList() {
        return notifyPersonList;
    }

    public void setNotifyPersonList(List<String> notifyPersonList) {
        this.notifyPersonList = notifyPersonList;
    }

    public Boolean getNotifyByDocumentReadPerson() {
        return notifyByDocumentReadPerson;
    }

    public void setNotifyByDocumentReadPerson(Boolean notifyByDocumentReadPerson) {
        this.notifyByDocumentReadPerson = notifyByDocumentReadPerson;
    }

    public Boolean getNotifyCreatePerson() {
        return notifyCreatePerson;
    }

    public void setNotifyCreatePerson(Boolean notifyCreatePerson) {
        this.notifyCreatePerson = notifyCreatePerson;
    }
}
