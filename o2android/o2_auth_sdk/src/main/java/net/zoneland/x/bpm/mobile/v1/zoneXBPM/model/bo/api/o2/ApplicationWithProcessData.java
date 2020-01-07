package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * 应用对象 包含了流程数据
 * Created by FancyLou on 2016/2/23.
 */
public class ApplicationWithProcessData {
    private String id;
    private String name;
    private String alias;
    private String createTime;
    private String updateTime;
    private String description;
    private String applicationCategory;
    private String icon;
    private String creatorPerson;
    private String lastUpdateTime;
    private String lastUpdatePerson;
    private List<String> availableIdentityList;
    private List<String> availableDepartmentList;
    private List<String> availableCompanyList;
    private List<String> controllerList;

    private List<ProcessInfoData> processList;


    public List<ProcessInfoData> getProcessList() {
        return processList;
    }

    public void setProcessList(List<ProcessInfoData> processList) {
        this.processList = processList;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplicationCategory() {
        return applicationCategory;
    }

    public void setApplicationCategory(String applicationCategory) {
        this.applicationCategory = applicationCategory;
    }

    public String getCreatorPerson() {
        return creatorPerson;
    }

    public void setCreatorPerson(String creatorPerson) {
        this.creatorPerson = creatorPerson;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdatePerson() {
        return lastUpdatePerson;
    }

    public void setLastUpdatePerson(String lastUpdatePerson) {
        this.lastUpdatePerson = lastUpdatePerson;
    }

    public List<String> getAvailableIdentityList() {
        return availableIdentityList;
    }

    public void setAvailableIdentityList(List<String> availableIdentityList) {
        this.availableIdentityList = availableIdentityList;
    }

    public List<String> getAvailableDepartmentList() {
        return availableDepartmentList;
    }

    public void setAvailableDepartmentList(List<String> availableDepartmentList) {
        this.availableDepartmentList = availableDepartmentList;
    }

    public List<String> getAvailableCompanyList() {
        return availableCompanyList;
    }

    public void setAvailableCompanyList(List<String> availableCompanyList) {
        this.availableCompanyList = availableCompanyList;
    }

    public List<String> getControllerList() {
        return controllerList;
    }

    public void setControllerList(List<String> controllerList) {
        this.controllerList = controllerList;
    }
}
