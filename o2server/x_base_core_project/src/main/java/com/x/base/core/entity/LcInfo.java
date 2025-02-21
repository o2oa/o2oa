package com.x.base.core.entity;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Date;
import java.util.List;

/**
 * @author chengjian
 * @date 2025/02/06 17:25
 **/
public class LcInfo extends GsonPropertyObject {

    @FieldDescribe("客户编号.")
    private String unique;
    @FieldDescribe("客户名称.")
    private String name;
    @FieldDescribe("顶层组织名称.")
    private String unitName;
    @FieldDescribe("邮箱.")
    private String email;
    @FieldDescribe("版本号.")
    private String version;
    @FieldDescribe("版本类型.")
    private String versionType;
    @FieldDescribe("授权模式.")
    private String model;
    @FieldDescribe("授权时间.")
    private Date startTime;
    @FieldDescribe("授权到期时间.")
    private Date expireTime;

    private List<String> supportDbList;

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public List<String> getSupportDbList() {
        return supportDbList;
    }

    public void setSupportDbList(List<String> supportDbList) {
        this.supportDbList = supportDbList;
    }
}
