package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.item;

/**
 * companyList 对象
 *
 * Created by FancyLou on 2015/10/16.
 */
public class CompanyItem extends ContactItem {

    private String name;
    private int companyCount;
    private int departmentCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompanyCount() {
        return companyCount;
    }

    public void setCompanyCount(int companyCount) {
        this.companyCount = companyCount;
    }

    public int getDepartmentCount() {
        return departmentCount;
    }

    public void setDepartmentCount(int departmentCount) {
        this.departmentCount = departmentCount;
    }
}
