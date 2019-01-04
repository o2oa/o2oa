package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * Created by FancyLou on 2016/8/10.
 */
public class BaseMessage {


    private String type;//消息分类
    private String person;//接收者
    private String time;// yyyy-MM-dd HH:mm:ss 消息时间
    private String category;//大分类


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
