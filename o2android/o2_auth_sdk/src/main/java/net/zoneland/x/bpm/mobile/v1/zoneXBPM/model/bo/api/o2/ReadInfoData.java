package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * 待阅详细信息
 * Created by FancyLou on 2016/3/9.
 */
public class ReadInfoData {

    private List<WorkLog> workLogList;
    private ReadData read;


    public List<WorkLog> getWorkLogList() {
        return workLogList;
    }

    public void setWorkLogList(List<WorkLog> workLogList) {
        this.workLogList = workLogList;
    }

    public ReadData getRead() {
        return read;
    }

    public void setRead(ReadData read) {
        this.read = read;
    }
}
