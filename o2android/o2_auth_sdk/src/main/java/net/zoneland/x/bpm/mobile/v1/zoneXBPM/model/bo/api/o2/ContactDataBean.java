package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.DepartmentItem;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.item.CompanyItem;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.item.IdentityItem;

import java.util.List;

/**
 * 查询组织列表的时候 返回的json对象中 属性data对应的对象
 *
 * Created by FancyLou on 2015/10/16.
 */
public class ContactDataBean {
    private String name;
    private String display;
    private String superior;
    private List<CompanyItem> companyList;
    private List<DepartmentItem> departmentList;
    private List<IdentityItem> identityList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }

    public List<CompanyItem> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<CompanyItem> companyList) {
        this.companyList = companyList;
    }

    public List<DepartmentItem> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<DepartmentItem> departmentList) {
        this.departmentList = departmentList;
    }

    public List<IdentityItem> getIdentityList() {
        return identityList;
    }

    public void setIdentityList(List<IdentityItem> identityList) {
        this.identityList = identityList;
    }
}
