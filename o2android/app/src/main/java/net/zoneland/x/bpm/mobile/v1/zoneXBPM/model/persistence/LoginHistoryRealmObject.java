package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by fancy on 2017/4/18.
 */

public class LoginHistoryRealmObject extends RealmObject {
    @PrimaryKey
    private String id;
    private String loginName;
    private String lastLoginTime;// yyyy-MM-dd HH:mm:ss
    //所属公司的id  切换绑定的时候 切换不同的数据
    private String unitId;

    private String loginPhone;//用户手机号码,登录可以使用手机号码 也可以用用户名

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
