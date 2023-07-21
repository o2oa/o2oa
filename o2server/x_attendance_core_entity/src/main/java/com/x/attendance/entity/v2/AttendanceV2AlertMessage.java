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
@Schema(name = "AttendanceV2AlertMessage", description = "考勤提醒消息对象.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2AlertMessage.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2AlertMessage.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2AlertMessage extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.AttendanceV2AlertMessage.table;
    private static final long serialVersionUID = 4469504288529954958L;


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

    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("用户标识")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;


    public static final String sendDateTime_FIELDNAME = "sendDateTime";
    @FieldDescribe("消息发送时间.")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + sendDateTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date sendDateTime;



    public static final String checkInType_FIELDNAME = "checkInType";
    @FieldDescribe("考勤类型: OnDuty|OffDuty")
    @Column(length = length_16B, name = ColumnNamePrefix + checkInType_FIELDNAME)
    private String checkInType;


    // 每天早上检查 发送消息提醒
    public static final String sendStatus_FIELDNAME = "sendStatus";
    @FieldDescribe("是否已经发送消息")
    @Column(name = ColumnNamePrefix + sendStatus_FIELDNAME)
    private Boolean sendStatus = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(Date sendDateTime) {
        this.sendDateTime = sendDateTime;
    }


    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public Boolean getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Boolean sendStatus) {
        this.sendStatus = sendStatus;
    }
}
