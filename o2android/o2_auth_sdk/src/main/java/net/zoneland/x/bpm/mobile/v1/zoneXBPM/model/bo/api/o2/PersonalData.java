package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 *  个人信息
 * 	x_organization_assemble_personal/jaxrs/person
 * 	接口返回的data对象
 * Created by FancyLou on 2015/12/6.
 */
public class PersonalData {

    private String id;
    private String updateTime;
    private String genderType;
    private String pinyin;
    private String pinyinInitial;
    private String name;
    private String employee;
    private String unique;
    private List<String> controllerList;
    private String mail;
    private String qq;
    private String weixin;
    private String mobile;
    private List<String> deviceList;
    private String icon;//头像
    private String signature;


    public void copy(PersonalData data) {
        setId(data.getId());
        setUpdateTime(data.getUpdateTime());
        setGenderType(data.getGenderType());
        setPinyin(data.getPinyin());
        setPinyinInitial(data.getPinyinInitial());
        setName(data.getName());
        setEmployee(data.getEmployee());
        setUnique(data.getUnique());
        setControllerList(data.getControllerList());
        setMail(data.getMail());
        setQq(data.getQq());
        setWeixin(data.getWeixin());
        setMobile(data.getMobile());
        setDeviceList(data.getDeviceList());
        setIcon(data.getIcon());
        setSignature(data.getSignature());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getGenderType() {
        return genderType;
    }

    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinInitial() {
        return pinyinInitial;
    }

    public void setPinyinInitial(String pinyinInitial) {
        this.pinyinInitial = pinyinInitial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }



    public List<String> getControllerList() {
        return controllerList;
    }

    public void setControllerList(List<String> controllerList) {
        this.controllerList = controllerList;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<String> deviceList) {
        this.deviceList = deviceList;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
