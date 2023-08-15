package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fancyLou on 2023/3/2.
 * Copyright © 2023 O2. All rights reserved.
 */
@Schema(name = "AttendanceV2AppealInfo", description = "考勤申诉信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2AppealInfo.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2AppealInfo.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2AppealInfo  extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.AttendanceV2AppealInfo.table;
    private static final long serialVersionUID = -2879724233155330444L;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {
    }
    /*
     * =============================================================================
     * ===== 以上为 JpaObject 默认字段
     * =============================================================================
     * =====
     */

    /*
     * =============================================================================
     * ===== 以下为具体不同的业务及数据表字段要求
     * =============================================================================
     * =====
     */

    public static final String recordId_FIELDNAME = "recordId";
    @FieldDescribe("申诉的打卡考勤记录ID.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + recordId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String recordId;

    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("用户标识")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("打卡记录日期，yyyy-MM-dd，来自申诉的打卡考勤记录.")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String recordDateString;

    public static final String recordDate_FIELDNAME = "recordDate";
    @FieldDescribe("打卡记录时间，来自申诉的打卡考勤记录.")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + recordDate_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date recordDate;


    public static final String startTime_FIELDNAME = "startTime";
    @FieldDescribe("申诉开始时间")
    @Column( length = JpaObject.length_32B, name = ColumnNamePrefix + startTime_FIELDNAME)
    private String startTime;

    public static final String endTime_FIELDNAME = "endTime";
    @FieldDescribe("申诉结束时间")
    @Column( length = JpaObject.length_32B, name = ColumnNamePrefix + endTime_FIELDNAME)
    private String endTime;

    public static final String reason_FIELDNAME = "reason";
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @FieldDescribe("申诉详细，申诉流程结束后写入.")
    @Column( length = JpaObject.length_2K, name = ColumnNamePrefix + reason_FIELDNAME)
    private String reason;

    public static final Integer status_TYPE_INIT = 0; // 待处理
    public static final Integer status_TYPE_PROCESSING = 1; // 审批中
    public static final Integer status_TYPE_PROCESS_AGREE = 2; // 审批通过
    public static final Integer status_TYPE_PROCESS_DISAGREE = 3; // 审批不通过
    public static final Integer status_TYPE_END_BY_ADMIN = 4; // 管理员已处理
    public static final String status_FIELDNAME = "status";
    @FieldDescribe("申诉状态:0-待处理，1-审批中（已发起流程），2-审批通过，3-审批不通过")
    @Column( name = ColumnNamePrefix + status_FIELDNAME)
    private Integer status = status_TYPE_INIT;

    public static final String jobId_FIELDNAME = "jobId";
    @FieldDescribe("流程的jobId，申诉流程结束后写入.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + jobId_FIELDNAME)
    private String jobId;


    public static final String updateStatusAdminPerson_FIELDNAME = "updateStatusAdminPerson";
    @FieldDescribe("管理员标识，最后操作人")
    @Column(length = length_96B, name = ColumnNamePrefix + updateStatusAdminPerson_FIELDNAME)
    private String updateStatusAdminPerson;



    // 每天早上检查 发送消息提醒
    public static final String sendStatus_FIELDNAME = "sendStatus";
    @FieldDescribe("是否已经发送消息")
    @Column(name = ColumnNamePrefix + sendStatus_FIELDNAME)
    private Boolean sendStatus = false;


    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Boolean getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Boolean sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getUpdateStatusAdminPerson() {
        return updateStatusAdminPerson;
    }

    public void setUpdateStatusAdminPerson(String updateStatusAdminPerson) {
        this.updateStatusAdminPerson = updateStatusAdminPerson;
    }

    
}
