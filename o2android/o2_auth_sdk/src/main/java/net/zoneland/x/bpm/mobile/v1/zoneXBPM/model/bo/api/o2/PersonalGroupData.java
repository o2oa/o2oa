package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * /jaxrs/group/list/person/%s/sup/direct
 * 返回的data对象
 * Created by FancyLou on 2015/12/10.
 */
public class PersonalGroupData {

    private String name;
    private String display;
    private List<String> groupList;//子群组列表
    private List<String> personList;//成员列表


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

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    public List<String> getPersonList() {
        return personList;
    }

    public void setPersonList(List<String> personList) {
        this.personList = personList;
    }
}
