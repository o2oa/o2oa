package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo;

/**
 * Created by FancyLou on 2016/4/21.
 */
public class PgyUpdateBean {
    private String message;
    private int code;
    private PgyUpdateDataBean data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PgyUpdateDataBean getData() {
        return data;
    }

    public void setData(PgyUpdateDataBean data) {
        this.data = data;
    }
}
