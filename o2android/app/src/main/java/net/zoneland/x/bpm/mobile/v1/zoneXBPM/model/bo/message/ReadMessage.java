package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.message;

/**
 * type:
 * read
 *
 * Created by FancyLou on 2016/8/10.
 */
public class ReadMessage extends BaseMessage {

    private String work;//workId
    private String read;//待阅id


    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
