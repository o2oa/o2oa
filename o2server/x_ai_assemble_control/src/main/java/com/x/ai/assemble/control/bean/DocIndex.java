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
    private String processId;
    @FieldDescribe("应用名称.")
    private String processName;
    @FieldDescribe("关联文档ID.")
    private String id;
    @FieldDescribe("文号.")
    private String serial;
    private String sourceType = "external";
    private String sourceId = "external";
    @FieldDescribe("文档创建者.")
    private String creatorPerson;
    private String creatorUnit;
    private String creatorUnitLevelName;
    @FieldDescribe("文档创建时间.")
    private Date createDateTime;
    private Date updateDateTime;
    @FieldDescribe("标题.")
    private String title;
    @FieldDescribe("分类.")
    private String category;
    private String content;
    private List<String> permissionList;
    private Boolean catalogEnable = true;
    private Boolean searchEnable = true;
    private Boolean embedEnable = true;
    private Boolean questionEnable = false;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorPerson() {
        return creatorPerson;
    }

    public void setCreatorPerson(String creatorPerson) {
        this.creatorPerson = creatorPerson;
    }

    public String getCreatorUnit() {
        return creatorUnit;
    }

    public void setCreatorUnit(String creatorUnit) {
        this.creatorUnit = creatorUnit;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getCreatorUnitLevelName() {
        return creatorUnitLevelName;
    }

    public void setCreatorUnitLevelName(String creatorUnitLevelName) {
        this.creatorUnitLevelName = creatorUnitLevelName;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
