package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api;

/**
 * Created by FancyLou on 2016/2/22.
 */
public class ValueNumberData {

    private int value;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "id:"+ id + " , value:"+value;
    }
}
