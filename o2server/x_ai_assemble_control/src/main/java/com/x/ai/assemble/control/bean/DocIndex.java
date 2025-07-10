package com.x.ai.assemble.control.bean;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Date;
import java.util.List;

/**
 * @author chengjian
 * @date 2025/04/11 17:43
 **/
public class DocIndex extends GsonPropertyObject {
    private String catalogId;
    @FieldDescribe("应用名称.")
    private String catalogName;
    @FieldDescribe("关联文档ID.")
    private String referenceId;
    private String sourceType = "external";
    @FieldDescribe("文档创建者.")
    private String referenceCreatorPerson;
    private String referenceCreatorUnit;
    @FieldDescribe("文档创建时间.")
    private Date referenceCreateDateTime;
    @FieldDescribe("标题.")
    private String title;
    private String content;
    private List<String> permissionList;
    private Boolean catalogEnable = true;
    private Boolean searchEnable = true;
    private Boolean embedEnable = true;
    private Boolean questionEnable = false;

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getReferenceCreatorPerson() {
        return referenceCreatorPerson;
    }

    public void setReferenceCreatorPerson(String referenceCreatorPerson) {
        this.referenceCreatorPerson = referenceCreatorPerson;
    }

    public String getReferenceCreatorUnit() {
        return referenceCreatorUnit;
    }

    public void setReferenceCreatorUnit(String referenceCreatorUnit) {
        this.referenceCreatorUnit = referenceCreatorUnit;
    }

    public Date getReferenceCreateDateTime() {
        return referenceCreateDateTime;
    }

    public void setReferenceCreateDateTime(Date referenceCreateDateTime) {
        this.referenceCreateDateTime = referenceCreateDateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public Boolean getCatalogEnable() {
        return catalogEnable;
    }

    public void setCatalogEnable(Boolean catalogEnable) {
        this.catalogEnable = catalogEnable;
    }

    public Boolean getSearchEnable() {
        return searchEnable;
    }

    public void setSearchEnable(Boolean searchEnable) {
        this.searchEnable = searchEnable;
    }

    public Boolean getEmbedEnable() {
        return embedEnable;
    }

    public void setEmbedEnable(Boolean embedEnable) {
        this.embedEnable = embedEnable;
    }

    public Boolean getQuestionEnable() {
        return questionEnable;
    }

    public void setQuestionEnable(Boolean questionEnable) {
        this.questionEnable = questionEnable;
    }
}
