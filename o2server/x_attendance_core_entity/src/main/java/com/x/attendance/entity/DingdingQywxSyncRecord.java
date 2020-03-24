package com.x.attendance.entity;


import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import javax.persistence.*;
import java.util.Date;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.DingdingQywxSyncRecord.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.DingdingQywxSyncRecord.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DingdingQywxSyncRecord extends SliceJpaObject  {



    private static final String TABLE = PersistenceProperties.DingdingQywxSyncRecord.table;
    private static final long serialVersionUID = -7473350816056626066L;

    @Override
    public void onPersist() throws Exception {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();
    /*
     * =============================================================================
     * ===== 以上为 JpaObject 默认字段
     * =============================================================================
     * =====
     */

    @FieldDescribe("同步开始时间")
    @Column(name = ColumnNamePrefix + "syncStartTime")
    private Date startTime;

    @FieldDescribe("同步结束时间")
    @Column(name = ColumnNamePrefix + "syncEndTime")
    private Date endTime;

    public static final String syncType_qywx = "qywx";
    public static final String syncType_dingding = "dingding";
    @FieldDescribe("同步类型，qywx（企业微信同步） ， dingding(钉钉同步)")
    @Column(name = ColumnNamePrefix + "type", length = length_32B)
    private String type;

    @FieldDescribe("同步打卡记录的开始时间")
    @Column(name = ColumnNamePrefix + "dateFrom")
    private long dateFrom;

    @FieldDescribe("同步打卡记录的结束时间， 起始与结束工作日最多相隔7天")
    @Column(name = ColumnNamePrefix + "dateTo")
    private long dateTo;

    public static final String status_loading = "loading";
    public static final String status_end = "end";
    public static final String status_error = "error";
    @FieldDescribe("同步状态，loading（正在进行） ， end（结束） , error（执行异常）")
    @Column(name = ColumnNamePrefix + "status", length = length_32B)
    private String status;


    @FieldDescribe("异常信息")
    @Column(name = ColumnNamePrefix + "exceptionMessage", length = length_255B)
    private String exceptionMessage;


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    public void setDateTo(long dateTo) {
        this.dateTo = dateTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
