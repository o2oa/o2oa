package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.item.ContactItem;

/**
 * departmentList对象
 *
 * Created by FancyLou on 2015/10/16.
 */
public class DepartmentItem extends ContactItem {
    private String name;
    private int departmentCount;
    private int identityCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepartmentCount() {
        return departmentCount;
    }

    public void setDepartmentCount(int departmentCount) {
        this.departmentCount = departmentCount;
    }

    public int getIdentityCount() {
        return identityCount;
    }

    public void setIdentityCount(int identityCount) {
        this.identityCount = identityCount;
    }
}
