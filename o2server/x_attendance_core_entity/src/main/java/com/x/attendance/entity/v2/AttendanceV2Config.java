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
 * 考勤配置
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2Config", description = "考勤配置信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2Config.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2Config.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Config extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2Config.table;
    private static final long serialVersionUID = 3024779660233909771L;


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


    public static final String holidayList_FIELDNAME = "holidayList";
    @FieldDescribe("节假日")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + holidayList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + holidayList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + holidayList_FIELDNAME)
    @ElementIndex(name = TABLE + holidayList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> holidayList;



    public static final String workDayList_FIELDNAME = "workDayList";
    @FieldDescribe("工作日")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + workDayList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + workDayList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + workDayList_FIELDNAME)
    @ElementIndex(name = TABLE + workDayList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> workDayList;


    public List<String> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<String> holidayList) {
        this.holidayList = holidayList;
    }

    public List<String> getWorkDayList() {
        return workDayList;
    }

    public void setWorkDayList(List<String> workDayList) {
        this.workDayList = workDayList;
    }
}
