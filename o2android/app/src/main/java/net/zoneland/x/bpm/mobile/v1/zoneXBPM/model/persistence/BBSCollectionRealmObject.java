package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by fancy on 2017/4/11.
 */

public class BBSCollectionRealmObject extends RealmObject {

    @PrimaryKey
    private String id;
    private String sectionName;
    private String sectionIcon;
    private long createTime;
    //所属公司的id  @Date 2017-04-18  切换绑定的时候 切换不同的数据
    private String unitId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionIcon() {
        return sectionIcon;
    }

    public void setSectionIcon(String sectionIcon) {
        this.sectionIcon = sectionIcon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
