package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * x_organization_assemble_express/jaxrs/identity/list/person/{personName}
 *
 * Created by FancyLou on 2016/9/10.
 */
public class PersonIdentityData {
    private String name;
    private String person;
    private String display;
    private String department;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
