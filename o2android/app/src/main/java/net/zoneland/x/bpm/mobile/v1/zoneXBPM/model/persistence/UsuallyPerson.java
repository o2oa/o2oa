package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence;

/**
 * 常用联系人表
 * Created by FancyLou on 2016/10/27.
 */

public class UsuallyPerson {


    private String id;
    private String owner;//所用者
    private String ownerDisplay;//所有者显示名
    private String person;//常用联系人
    private String personDisplay;//常用联系人的显示名
    private String department;//所属部门
    private String gender;//性别
    private String mobile;//手机号码

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getOwnerDisplay() {
        return ownerDisplay;
    }

    public void setOwnerDisplay(String ownerDisplay) {
        this.ownerDisplay = ownerDisplay;
    }

    public String getPersonDisplay() {
        return personDisplay;
    }

    public void setPersonDisplay(String personDisplay) {
        this.personDisplay = personDisplay;
    }
}
