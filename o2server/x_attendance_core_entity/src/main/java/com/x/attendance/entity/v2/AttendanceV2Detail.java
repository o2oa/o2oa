package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
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
 * Created by fancyLou on 2023/2/21.
 * Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2Detail", description = "考勤每日详细信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2Detail.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2Detail.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Detail extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2Detail.table;


    private static final long serialVersionUID = -4038741539513754723L;


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

    public static final String yearString_FIELDNAME = "yearString";
    @FieldDescribe("记录年份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + yearString_FIELDNAME)
    private String yearString;

    public static final String monthString_FIELDNAME = "monthString";
    @FieldDescribe("记录月份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + monthString_FIELDNAME)
    private String monthString;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("记录日期")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
    private String recordDateString;

    public static final String recordDay_FIELDNAME = "recordDay";
    @FieldDescribe("记录日期的周几，0-6")
    @Column(length = JpaObject.length_4B, name = ColumnNamePrefix + recordDay_FIELDNAME)
    private String recordDay;


    public static final String isWorkday_FIELDNAME = "workDay";
    @FieldDescribe("是否工作日")
    @Column(name = ColumnNamePrefix + isWorkday_FIELDNAME)
    private Boolean workDay = false;



    //////// 统计相关
    public static final String lateTimeDuration_FIELDNAME = "lateTimeDuration";
    @FieldDescribe("迟到时长(分钟)")
    @Column(name = ColumnNamePrefix + lateTimeDuration_FIELDNAME)
    private Long lateTimeDuration = 0L;

    public static final String leaveEarlierTimeDuration_FIELDNAME = "leaveEarlierTimeDuration";
    @FieldDescribe("早退时长(分钟)")
    @Column(name = ColumnNamePrefix + leaveEarlierTimeDuration_FIELDNAME)
    private Long leaveEarlierTimeDuration = 0L;

//    public static final String workOvertimeTimeDuration_FIELDNAME = "workOvertimeTimeDuration";
//    @FieldDescribe("加班时长(分钟)")
//    @Column(name = ColumnNamePrefix + workOvertimeTimeDuration_FIELDNAME)
//    private Long workOvertimeTimeDuration = 0L;

    public static final String workTimeDuration_FIELDNAME = "workTimeDuration";
    @FieldDescribe("工作时长(分钟)")
    @Column(name = ColumnNamePrefix + workTimeDuration_FIELDNAME)
    private Long workTimeDuration = 0L;

    public static final String attendance_FIELDNAME = "attendance";
    @FieldDescribe("出勤天数")
    @Column(name = ColumnNamePrefix + attendance_FIELDNAME)
    private Integer attendance = 0;

    public static final String rest_FIELDNAME = "rest";
    @FieldDescribe("休息天数")
    @Column(name = ColumnNamePrefix + rest_FIELDNAME)
    private Integer rest = 0;

    public static final String absenteeismDays_FIELDNAME = "absenteeismDays";
    @FieldDescribe("旷工天数")
    @Column(name = ColumnNamePrefix + absenteeismDays_FIELDNAME)
    private Integer absenteeismDays = 0;

    public static final String leaveDays_FIELDNAME = "leaveDays";
    @FieldDescribe("请假天数")
    @Column(name = ColumnNamePrefix + leaveDays_FIELDNAME)
    private Integer leaveDays = 0;

    public static final String lateTimes_FIELDNAME = "lateTimes";
    @FieldDescribe("迟到次数")
    @Column(name = ColumnNamePrefix + lateTimes_FIELDNAME)
    private Integer lateTimes = 0;

    public static final String leaveEarlierTimes_FIELDNAME = "leaveEarlierTimes";
    @FieldDescribe("早退次数")
    @Column(name = ColumnNamePrefix + leaveEarlierTimes_FIELDNAME)
    private Integer leaveEarlierTimes = 0;

    public static final String onDutyAbsenceTimes_FIELDNAME = "onDutyAbsenceTimes";
    @FieldDescribe("上班缺卡次数")
    @Column(name = ColumnNamePrefix + onDutyAbsenceTimes_FIELDNAME)
    private Integer onDutyAbsenceTimes = 0;

    public static final String offDutyAbsenceTimes_FIELDNAME = "offDutyAbsenceTimes";
    @FieldDescribe("下班缺卡次数")
    @Column(name = ColumnNamePrefix + offDutyAbsenceTimes_FIELDNAME)
    private Integer offDutyAbsenceTimes = 0;

    public static final String fieldWorkTimes_FIELDNAME = "fieldWorkTimes";
    @FieldDescribe("外勤打卡次数")
    @Column(name = ColumnNamePrefix + fieldWorkTimes_FIELDNAME)
    private Integer fieldWorkTimes = 0;


    ///// 关联
    public static final String recordIdList_FIELDNAME = "recordIdList";
    @FieldDescribe("考勤记录id列表")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + recordIdList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = TABLE + recordIdList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + recordIdList_FIELDNAME)
    @ElementIndex(name = TABLE + recordIdList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> recordIdList;

    public static final String groupId_FIELDNAME = "groupId";
    @FieldDescribe("考勤组id.")
    @Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
    private String groupId;

    public static final String groupName_FIELDNAME = "groupName";
    @FieldDescribe("考勤组名称")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + groupName_FIELDNAME)
    private String groupName ;

    public static final String shiftId_FIELDNAME = "shiftId";
    @FieldDescribe("班次id.")
    @Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
    private String shiftId;

    public static final String shiftName_FIELDNAME = "shiftName";
    @FieldDescribe("班次名称，休息日")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + shiftName_FIELDNAME)
    private String shiftName;


    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Integer getFieldWorkTimes() {
        return fieldWorkTimes;
    }

    public void setFieldWorkTimes(Integer fieldWorkTimes) {
        this.fieldWorkTimes = fieldWorkTimes;
    }

    public String getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(String recordDay) {
        this.recordDay = recordDay;
    }

    public Boolean getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Boolean workDay) {
        this.workDay = workDay;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getYearString() {
        return yearString;
    }

    public void setYearString(String yearString) {
        this.yearString = yearString;
    }

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    public Long getLateTimeDuration() {
        return lateTimeDuration;
    }

    public void setLateTimeDuration(Long lateTimeDuration) {
        this.lateTimeDuration = lateTimeDuration;
    }

    public Long getLeaveEarlierTimeDuration() {
        return leaveEarlierTimeDuration;
    }

    public void setLeaveEarlierTimeDuration(Long leaveEarlierTimeDuration) {
        this.leaveEarlierTimeDuration = leaveEarlierTimeDuration;
    }
//
//    public Long getWorkOvertimeTimeDuration() {
//        return workOvertimeTimeDuration;
//    }
//
//    public void setWorkOvertimeTimeDuration(Long workOvertimeTimeDuration) {
//        this.workOvertimeTimeDuration = workOvertimeTimeDuration;
//    }

    public Long getWorkTimeDuration() {
        return workTimeDuration;
    }

    public void setWorkTimeDuration(Long workTimeDuration) {
        this.workTimeDuration = workTimeDuration;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getAbsenteeismDays() {
        return absenteeismDays;
    }

    public void setAbsenteeismDays(Integer absenteeismDays) {
        this.absenteeismDays = absenteeismDays;
    }

    public Integer getLateTimes() {
        return lateTimes;
    }

    public void setLateTimes(Integer lateTimes) {
        this.lateTimes = lateTimes;
    }

    public Integer getLeaveEarlierTimes() {
        return leaveEarlierTimes;
    }

    public void setLeaveEarlierTimes(Integer leaveEarlierTimes) {
        this.leaveEarlierTimes = leaveEarlierTimes;
    }

    public Integer getOnDutyAbsenceTimes() {
        return onDutyAbsenceTimes;
    }

    public void setOnDutyAbsenceTimes(Integer onDutyAbsenceTimes) {
        this.onDutyAbsenceTimes = onDutyAbsenceTimes;
    }

    public Integer getOffDutyAbsenceTimes() {
        return offDutyAbsenceTimes;
    }

    public void setOffDutyAbsenceTimes(Integer offDutyAbsenceTimes) {
        this.offDutyAbsenceTimes = offDutyAbsenceTimes;
    }

    public List<String> getRecordIdList() {
        return recordIdList;
    }

    public void setRecordIdList(List<String> recordIdList) {
        this.recordIdList = recordIdList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }
}
