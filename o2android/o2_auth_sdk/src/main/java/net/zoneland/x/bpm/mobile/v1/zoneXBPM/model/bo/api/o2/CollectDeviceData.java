package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

/**
 * Created by FancyLou on 2016/5/31.
 */
public class CollectDeviceData {

    private String unit;//上一步 选择的公司名称
    private String mobile;//手机号码
    private String code;//验证码
    private String name;//设备号 友盟的token 中心服务器推送消息需要
    private String deviceType;//设备类型  android ios
    private String account; //账号 就是手机号码对应的id


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
