package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * Created by FancyLou on 2016/4/29.
 */
public class PersonattributeData {

    private String name;
    private List<String> attributeList;
    private String person;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
