package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * 已阅详细信息
 * Created by FancyLou on 2016/3/9.
 */
public class ReadCompleteInfoData {

    private List<WorkLog> workLogList;
    private ReadCompleteData readCompleted;

    public List<WorkLog> getWorkLogList() {
        return workLogList;
    }

    public void setWorkLogList(List<WorkLog> workLogList) {
        this.workLogList = workLogList;
    }

    public ReadCompleteData getReadCompleted() {
        return readCompleted;
    }

    public void setReadCompleted(ReadCompleteData readCompleted) {
        this.readCompleted = readCompleted;
    }
}
