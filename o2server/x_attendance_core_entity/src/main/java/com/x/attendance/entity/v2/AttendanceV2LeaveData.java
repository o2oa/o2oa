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
@Schema(name = "AttendanceV2LeaveData", description = "请假数据表.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2LeaveData.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2LeaveData.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveData extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.AttendanceV2LeaveData.table;
    private static final long serialVersionUID = -7264799630750073594L;


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


    public static final String person_FIELDNAME = "person";
    @FieldDescribe("用户标识")
    @Column(length = length_96B, name = ColumnNamePrefix + person_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String person;

    public static final String leaveType_FIELDNAME = "leaveType";
    @FieldDescribe("请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|出差|培训|其他")
    @Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveType_FIELDNAME )
    @CheckPersist(allowEmpty = false)
    private String leaveType;

    public static final String startTime_FIELDNAME = "startTime";
    @FieldDescribe("开始时间：yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column( name = ColumnNamePrefix + startTime_FIELDNAME )
    @CheckPersist(allowEmpty = false)
    private Date startTime;

    public static final String endTime_FIELDNAME = "endTime";
    @FieldDescribe("结束时间：yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column( name = ColumnNamePrefix + endTime_FIELDNAME )
    @CheckPersist(allowEmpty = false)
    private Date endTime;

    public static final String leaveDayNumber_FIELDNAME = "leaveDayNumber";
    @FieldDescribe("请假天数")
    @Column( name = ColumnNamePrefix + leaveDayNumber_FIELDNAME )
    private Double leaveDayNumber = 0.0;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("请假说明")
    @Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
    private String description;

    public static final String jobId_FIELDNAME = "jobId";
    @FieldDescribe("流程的jobId，申诉流程结束后写入.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + jobId_FIELDNAME)
    private String jobId;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

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

    public Double getLeaveDayNumber() {
        return leaveDayNumber;
    }

    public void setLeaveDayNumber(Double leaveDayNumber) {
        this.leaveDayNumber = leaveDayNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
