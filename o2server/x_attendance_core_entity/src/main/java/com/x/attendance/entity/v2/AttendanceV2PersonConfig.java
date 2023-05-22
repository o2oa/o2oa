package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;

import javax.persistence.*;
import java.util.List;

/**
 * 考勤个人配置
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2PersonConfig", description = "考勤个人配置信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2PersonConfig.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2PersonConfig.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2PersonConfig extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2PersonConfig.table;
    private static final long serialVersionUID = -3015660811179908189L;


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
    @FieldDescribe("人员DN")
    @Column(length = length_128B, name = ColumnNamePrefix + person_FIELDNAME)
    private String person;


    public static final String onDutyFastCheckInEnable_FIELDNAME = "onDutyFastCheckInEnable";
    @FieldDescribe("上班极速打卡，app端有效")
    @Column(name = ColumnNamePrefix + onDutyFastCheckInEnable_FIELDNAME)
    private Boolean onDutyFastCheckInEnable = false;

    public static final String onDutyFastCheckInBegin_FIELDNAME = "onDutyFastCheckInBegin";
    @FieldDescribe("上班极速打卡开始，上班打卡前几分钟，默认上班时间前1小时，app端有效")
    @Column( name = ColumnNamePrefix + onDutyFastCheckInBegin_FIELDNAME)
    private Integer onDutyFastCheckInBegin = -60;

    public static final String onDutyFastCheckInEnd_FIELDNAME = "onDutyFastCheckInEnd";
    @FieldDescribe("上班极速打卡结束，上班打卡后几分钟，默认到上班时间，app端有效")
    @Column(  name = ColumnNamePrefix + onDutyFastCheckInEnd_FIELDNAME)
    private Integer onDutyFastCheckInEnd = 0;



    public static final String offDutyFastCheckInEnable_FIELDNAME = "offDutyFastCheckInEnable";
    @FieldDescribe("下班极速打卡，app端有效")
    @Column(name = ColumnNamePrefix + offDutyFastCheckInEnable_FIELDNAME)
    private Boolean offDutyFastCheckInEnable = false;

    public static final String offDutyFastCheckInBegin_FIELDNAME = "offDutyFastCheckInBegin";
    @FieldDescribe("下班极速打卡开始，下班后几分钟，默认下班时间，app端有效")
    @Column( name = ColumnNamePrefix + offDutyFastCheckInBegin_FIELDNAME)
    private Integer offDutyFastCheckInBegin = 0;

    public static final String offDutyFastCheckInEnd_FIELDNAME = "offDutyFastCheckInEnd";
    @FieldDescribe("下班极速打卡结束，下班后几分钟，默认到下班后1小时，app端有效")
    @Column(  name = ColumnNamePrefix + offDutyFastCheckInEnd_FIELDNAME)
    private Integer offDutyFastCheckInEnd = 60;


    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Boolean getOnDutyFastCheckInEnable() {
        return onDutyFastCheckInEnable;
    }

    public void setOnDutyFastCheckInEnable(Boolean onDutyFastCheckInEnable) {
        this.onDutyFastCheckInEnable = onDutyFastCheckInEnable;
    }

    public Integer getOnDutyFastCheckInBegin() {
        return onDutyFastCheckInBegin;
    }

    public void setOnDutyFastCheckInBegin(Integer onDutyFastCheckInBegin) {
        this.onDutyFastCheckInBegin = onDutyFastCheckInBegin;
    }

    public Integer getOnDutyFastCheckInEnd() {
        return onDutyFastCheckInEnd;
    }

    public void setOnDutyFastCheckInEnd(Integer onDutyFastCheckInEnd) {
        this.onDutyFastCheckInEnd = onDutyFastCheckInEnd;
    }

    public Boolean getOffDutyFastCheckInEnable() {
        return offDutyFastCheckInEnable;
    }

    public void setOffDutyFastCheckInEnable(Boolean offDutyFastCheckInEnable) {
        this.offDutyFastCheckInEnable = offDutyFastCheckInEnable;
    }

    public Integer getOffDutyFastCheckInBegin() {
        return offDutyFastCheckInBegin;
    }

    public void setOffDutyFastCheckInBegin(Integer offDutyFastCheckInBegin) {
        this.offDutyFastCheckInBegin = offDutyFastCheckInBegin;
    }

    public Integer getOffDutyFastCheckInEnd() {
        return offDutyFastCheckInEnd;
    }

    public void setOffDutyFastCheckInEnd(Integer offDutyFastCheckInEnd) {
        this.offDutyFastCheckInEnd = offDutyFastCheckInEnd;
    }
}
