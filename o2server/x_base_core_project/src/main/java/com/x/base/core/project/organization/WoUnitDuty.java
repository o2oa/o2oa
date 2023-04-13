package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.List;

/**
 * @author sword
 * @date 2023/04/12 14:12
 **/
public class WoUnitDuty extends GsonPropertyObject {

    @FieldDescribe("组织对象")
    private WoUnit woUnit;

    @FieldDescribe("个人属性名称")
    private String name;

    @FieldDescribe("组织")
    private String unit;

    @FieldDescribe("身份信息")
    private List<String> identityList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getIdentityList() {
        return identityList;
    }

    public void setIdentityList(List<String> identityList) {
        this.identityList = identityList;
    }

    public WoUnit getWoUnit() {
        return woUnit;
    }

    public void setWoUnit(WoUnit woUnit) {
        this.woUnit = woUnit;
    }


}
