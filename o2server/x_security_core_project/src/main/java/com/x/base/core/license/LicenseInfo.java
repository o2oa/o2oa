package com.x.base.core.license;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Date;

/**
 * @author chengjian
 * @date 2025/01/22 16:38
 **/
public class LicenseInfo extends GsonPropertyObject {
    @FieldDescribe("客户名称.")
    private String name;
    @FieldDescribe("邮箱.")
    private String email;
    @FieldDescribe("授权版本.")
    private String version;
    @FieldDescribe("授权时间.")
    private Date startTime;
    @FieldDescribe("授权到期时间.")
    private Date expireTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
