package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * ORG_EXPRESS_PERSON_DEPARTMENT_LIST;
 * 返回的data对象
 * Created by FancyLou on 2015/12/10.
 */
public class PersonDeptData {

    private String name;
    private String display;
    private String company;
    private String superior;


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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }
}
