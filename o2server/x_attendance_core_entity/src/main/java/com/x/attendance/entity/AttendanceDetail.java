package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceDetail", description = "考勤信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceDetail.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetail.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDetail extends SliceJpaObject {

    private static final long serialVersionUID = 3856138316794473794L;
    private static final String TABLE = PersistenceProperties.AttendanceDetail.table;

    public AttendanceDetail() {
    }

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
        this.setSequence(StringUtils.join(this.empName, this.recordDateString, this.getId()));
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
    public static final String empNo_FIELDNAME = "empNo";
    @FieldDescribe("员工号")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + empNo_FIELDNAME)

    private String empNo;

    public static final String empName_FIELDNAME = "empName";
    @FieldDescribe("员工姓名distinguishedName")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + empName_FIELDNAME)

    private String empName;

    public static final String topUnitName_FIELDNAME = "topUnitName";
    @FieldDescribe("顶层组织名称distinguishedName")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + topUnitName_FIELDNAME)

    private String topUnitName;

    public static final String unitName_FIELDNAME = "unitName";
    @FieldDescribe("组织名称distinguishedName")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + unitName_FIELDNAME)

    private String unitName;

    public static final String recordDate_FIELDNAME = "recordDate";
    @FieldDescribe("打卡记录日期")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + recordDate_FIELDNAME)

    private Date recordDate;

    public static final String yearString_FIELDNAME = "yearString";
    @FieldDescribe("打卡记录年份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + yearString_FIELDNAME)

    private String yearString;

    public static final String monthString_FIELDNAME = "monthString";
    @FieldDescribe("打卡记录月份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + monthString_FIELDNAME)

    private String monthString;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("打卡记录日期字符串")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)

    private String recordDateString;

    public static final String cycleYear_FIELDNAME = "cycleYear";
    @FieldDescribe("统计周期年份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + cycleYear_FIELDNAME)

    private String cycleYear;

    public static final String cycleMonth_FIELDNAME = "cycleMonth";
    @FieldDescribe("统计周期月份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + cycleMonth_FIELDNAME)

    private String cycleMonth;

    public static final String selfHolidayDayTime_FIELDNAME = "selfHolidayDayTime";
    @FieldDescribe("请假时段:上午|下午|全天")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + selfHolidayDayTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String selfHolidayDayTime = "无";

    public static final String absentDayTime_FIELDNAME = "absentDayTime";
    @FieldDescribe("缺勤时段:上午|下午|全天")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + absentDayTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String absentDayTime = "无";

    public static final String abnormalDutyDayTime_FIELDNAME = "abnormalDutyDayTime";
    @FieldDescribe("异常打卡时段:上午|下午|全天")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + abnormalDutyDayTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String abnormalDutyDayTime = "无";

    public static final String getSelfHolidayDays_FIELDNAME = "getSelfHolidayDays";
    @FieldDescribe("休假天数: 0|0.5|1")
    @Column(name = ColumnNamePrefix + getSelfHolidayDays_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Double getSelfHolidayDays = 0.0;

    public static final String onWorkTime_FIELDNAME = "onWorkTime";
    @FieldDescribe("上班时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + onWorkTime_FIELDNAME)

    private String onWorkTime;

    public static final String offWorkTime_FIELDNAME = "offWorkTime";
    @FieldDescribe("下班时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + offWorkTime_FIELDNAME)

    private String offWorkTime;

    public static final String middayRestStartTime_FIELDNAME = "middayRestStartTime";
    @FieldDescribe("午休开始时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestStartTime_FIELDNAME)

    private String middayRestStartTime;

    public static final String middayRestEndTime_FIELDNAME = "middayRestEndTime";
    @FieldDescribe("午休结束时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestEndTime_FIELDNAME)

    private String middayRestEndTime;

    public static final String onDutyTime_FIELDNAME = "onDutyTime";
    @FieldDescribe("上班打卡签到时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + onDutyTime_FIELDNAME)

    private String onDutyTime;

    public static final String morningOffDutyTime_FIELDNAME = "morningOffDutyTime";
    @FieldDescribe("上班下午打卡签退时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + morningOffDutyTime_FIELDNAME)

    private String morningOffDutyTime;

    public static final String afternoonOnDutyTime_FIELDNAME = "afternoonOnDutyTime";
    @FieldDescribe("下午上班打卡签到时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + afternoonOnDutyTime_FIELDNAME)

    private String afternoonOnDutyTime;

    public static final String offDutyTime_FIELDNAME = "offDutyTime";
    @FieldDescribe("下班打卡签退时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + offDutyTime_FIELDNAME)

    private String offDutyTime;

    public static final String lateTimeDuration_FIELDNAME = "lateTimeDuration";
    @FieldDescribe("迟到时长")
    @Column(name = ColumnNamePrefix + lateTimeDuration_FIELDNAME)
    private Long lateTimeDuration = 0L;

    public static final String leaveEarlierTimeDuration_FIELDNAME = "leaveEarlierTimeDuration";
    @FieldDescribe("早退时长")
    @Column(name = ColumnNamePrefix + leaveEarlierTimeDuration_FIELDNAME)
    private Long leaveEarlierTimeDuration = 0L;

    public static final String workOvertimeTimeDuration_FIELDNAME = "workOvertimeTimeDuration";
    @FieldDescribe("加班时长")
    @Column(name = ColumnNamePrefix + workOvertimeTimeDuration_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Long workOvertimeTimeDuration = 0L;

    public static final String workTimeDuration_FIELDNAME = "workTimeDuration";
    @FieldDescribe("出勤时长")
    @Column(name = ColumnNamePrefix + workTimeDuration_FIELDNAME)
    private Long workTimeDuration = 0L;

    public static final String attendance_FIELDNAME = "attendance";
    @FieldDescribe("出勤天数（0|0.5|1）")
    @Column(name = ColumnNamePrefix + attendance_FIELDNAME)
    private Double attendance = 1.0;

    public static final String absence_FIELDNAME = "absence";
    @FieldDescribe("缺勤天数（0|0.5|1）")
    @Column(name = ColumnNamePrefix + absence_FIELDNAME)
    private Double absence = 0.0;

    public static final String recordStatus_FIELDNAME = "recordStatus";
    @FieldDescribe("记录状态：0-未分析 1-已分析")
    @Column(name = ColumnNamePrefix + recordStatus_FIELDNAME)
    private Integer recordStatus = 0;

    public static final String batchName_FIELDNAME = "batchName";
    @FieldDescribe("导入批次号:导入文件的ID")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + batchName_FIELDNAME)

    private String batchName;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("说明备注")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)

    private String description;

    public static final String appealStatus_FIELDNAME = "appealStatus";
    @FieldDescribe("申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过")
    @Column(name = ColumnNamePrefix + appealStatus_FIELDNAME)
    private Integer appealStatus = 0;

    public static final String appealReason_FIELDNAME = "appealReason";
    @FieldDescribe("申诉原因")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + appealReason_FIELDNAME)

    private String appealReason;

    public static final String appealProcessor_FIELDNAME = "appealProcessor";
    @FieldDescribe("申诉处理人")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + appealProcessor_FIELDNAME)

    private String appealProcessor;

    public static final String appealDescription_FIELDNAME = "appealDescription";
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @FieldDescribe("申诉具体说明")
    @Column(length = JpaObject.length_2K, name = ColumnNamePrefix + appealDescription_FIELDNAME)

    private String appealDescription;

    public static final String isHoliday_FIELDNAME = "isHoliday";
    @FieldDescribe("是否法定节假日")
    @Column(name = ColumnNamePrefix + isHoliday_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isHoliday = false;

    public static final String isWorkday_FIELDNAME = "isWorkday";
    @FieldDescribe("是否调休工作日")
    @Column(name = ColumnNamePrefix + isWorkday_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isWorkday = false;

    public static final String isGetSelfHolidays_FIELDNAME = "isGetSelfHolidays";
    @FieldDescribe("是否休假")
    @Column(name = ColumnNamePrefix + isGetSelfHolidays_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isGetSelfHolidays = false;

    public static final String leaveType_FIELDNAME = "leaveType";
    @FieldDescribe("休假类型")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + leaveType_FIELDNAME)

    private String leaveType;

    public static final String isAbsent_FIELDNAME = "isAbsent";
    @FieldDescribe("是否缺勤")
    @Column(name = ColumnNamePrefix + isAbsent_FIELDNAME)
    private Boolean isAbsent = false;

    public static final String isAbnormalDuty_FIELDNAME = "isAbnormalDuty";
    @FieldDescribe("是否异常打卡")
    @Column(name = ColumnNamePrefix + isAbnormalDuty_FIELDNAME)
    private Boolean isAbnormalDuty = false;

    public static final String isLackOfTime_FIELDNAME = "isLackOfTime";
    @FieldDescribe("是否工时不足")
    @Column(name = ColumnNamePrefix + isLackOfTime_FIELDNAME)
    private Boolean isLackOfTime = false;

    public static final String isWorkOvertime_FIELDNAME = "isWorkOvertime";
    @FieldDescribe("是否加班")
    @Column(name = ColumnNamePrefix + isWorkOvertime_FIELDNAME)
    private Boolean isWorkOvertime = false;

    public static final String isLeaveEarlier_FIELDNAME = "isLeaveEarlier";
    @FieldDescribe("是否早退")
    @Column(name = ColumnNamePrefix + isLeaveEarlier_FIELDNAME)
    private Boolean isLeaveEarlier = false;

    public static final String isLate_FIELDNAME = "isLate";
    @FieldDescribe("是否迟到")
    @Column(name = ColumnNamePrefix + isLate_FIELDNAME)
    private Boolean isLate = false;

    public static final String isWeekend_FIELDNAME = "isWeekend";
    @FieldDescribe("是否周末")
    @Column(name = ColumnNamePrefix + isWeekend_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isWeekend = false;

    public static final String archiveTime_FIELDNAME = "archiveTime";
    @FieldDescribe("记录归档时间")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + archiveTime_FIELDNAME)

    private String archiveTime;

    public static final String isExternal_FIELDNAME = "isExternal";
    @FieldDescribe("是否范围外打卡")
    @Column(name = ColumnNamePrefix + isExternal_FIELDNAME)

    private Boolean isExternal = false;

    public static final String recordAddress_FIELDNAME = "recordAddress";
    @FieldDescribe("打卡地址")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recordAddress_FIELDNAME)

    private String recordAddress;

    public static final String optMachineType_FIELDNAME = "optMachineType";
    @FieldDescribe("设备信息")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + optMachineType_FIELDNAME)

    private String optMachineType;

    public String getRecordAddress() {
        return recordAddress;
    }

    public void setRecordAddress(String recordAddress) {
        this.recordAddress = recordAddress;
    }

    public String getOptMachineType() {
        return optMachineType;
    }

    public void setOptMachineType(String optMachineType) {
        this.optMachineType = optMachineType;
    }

    public String getMorningOffDutyTime() {
        return morningOffDutyTime;
    }

    public void setMorningOffDutyTime(String morningOffDutyTime) {
        this.morningOffDutyTime = morningOffDutyTime;
    }

    public String getAfternoonOnDutyTime() {
        return afternoonOnDutyTime;
    }

    public void setAfternoonOnDutyTime(String afternoonOnDutyTime) {
        this.afternoonOnDutyTime = afternoonOnDutyTime;
    }

    /**
     * 获取是否工时不足
     *
     * @return
     */
    public Boolean getIsLackOfTime() {
        return isLackOfTime;
    }

    /**
     * 设置是否工时不足
     *
     * @param isLackOfTime
     */
    public void setIsLackOfTime(Boolean isLackOfTime) {
        this.isLackOfTime = isLackOfTime;
    }

    /**
     * 获取员工号（String）
     *
     * @return
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * 设置员工号（String）
     *
     * @param empNo
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * 获取员工姓名（String）
     *
     * @return
     */
    public String getEmpName() {
        if (empName != null) {
            empName = empName.trim();
        }
        return empName;
    }

    /**
     * 设置员工姓名（String）
     *
     * @param empName
     */
    public void setEmpName(String empName) {
        if (empName != null) {
            empName = empName.trim();
        }
        this.empName = empName;
    }

    /**
     * 获取员工是否已经迟到（Integer）
     *
     * @return
     */
    public Boolean getIsLate() {
        return isLate;
    }

    /**
     * 设置员工是否已经迟到（Integer）
     *
     * @param isLate
     */
    public void setIsLate(Boolean isLate) {
        this.isLate = isLate;
    }

    /**
     * 获取员工是否早退（Integer）
     *
     * @return
     */
    public Boolean getIsLeaveEarlier() {
        return isLeaveEarlier;
    }

    /**
     * 设置员工是否早退（Integer）
     *
     * @param isLeaveEarlier
     */
    public void setIsLeaveEarlier(Boolean isLeaveEarlier) {
        this.isLeaveEarlier = isLeaveEarlier;
    }

    /**
     * 获取员工员工打卡记录日期（String）
     *
     * @return
     */
    public String getRecordDateString() {
        return recordDateString;
    }

    /**
     * 设置员工员工打卡记录日期（String）
     *
     * @param recordDateString
     */
    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    /**
     * 获取员工员工打卡记录日期（Date）
     *
     * @return
     */
    public Date getRecordDate() {
        return recordDate;
    }

    /**
     * 设置员工员工打卡记录日期（Date）
     *
     * @param recordDate
     */
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * 获取员工打卡记录导入批次号-导入文件ID（String）
     *
     * @return
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * 设置员工打卡记录导入批次号-导入文件ID（String）
     *
     * @param batchName
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    /**
     * 获取员工标准上班时间（String）
     *
     * @return
     */
    public String getOnWorkTime() {
        return onWorkTime;
    }

    /**
     * 设置员工标准上班时间（String）
     *
     * @param onWorkTime
     */
    public void setOnWorkTime(String onWorkTime) {
        this.onWorkTime = onWorkTime;
    }

    /**
     * 获取员工标准下班时间（String）
     *
     * @return
     */
    public String getOffWorkTime() {
        return offWorkTime;
    }

    /**
     * 设置员工标准下班时间（String）
     *
     * @param offWorkTime
     */
    public void setOffWorkTime(String offWorkTime) {
        this.offWorkTime = offWorkTime;
    }

    /**
     * 获取员工迟到时长（Long）
     *
     * @return
     */
    public Long getLateTimeDuration() {
        return lateTimeDuration;
    }

    /**
     * 设置员工迟到时长（Long）
     *
     * @param lateTimeDuration
     */
    public void setLateTimeDuration(Long lateTimeDuration) {
        this.lateTimeDuration = lateTimeDuration;
    }

    /**
     * 获取员工早退时长（Long）
     *
     * @return
     */
    public Long getLeaveEarlierTimeDuration() {
        return leaveEarlierTimeDuration;
    }

    /**
     * 设置员工早退时长（Long）
     *
     * @param leaveEarlierTimeDuration
     */
    public void setLeaveEarlierTimeDuration(Long leaveEarlierTimeDuration) {
        this.leaveEarlierTimeDuration = leaveEarlierTimeDuration;
    }

    /**
     * 获取员工是否缺勤（Integer）
     *
     * @return
     */
    public Boolean getIsAbsent() {
        return isAbsent;
    }

    /**
     * 设置员工是否缺勤（Integer）
     *
     * @param isAbsent
     */
    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    /**
     * 获取员工是否加班（Integer）
     *
     * @return
     */
    public Boolean getIsWorkOvertime() {
        return isWorkOvertime;
    }

    /**
     * 设置员工是否加班（Integer）
     *
     * @param isWorkOvertime
     */
    public void setIsWorkOvertime(Boolean isWorkOvertime) {
        this.isWorkOvertime = isWorkOvertime;
    }

    /**
     * 获取员工加班时长（Long）
     *
     * @return
     */
    public Long getWorkOvertimeTimeDuration() {
        return workOvertimeTimeDuration;
    }

    /**
     * 设置员工加班时长（Long）
     *
     * @param workOvertimeTimeDuration
     */
    public void setWorkOvertimeTimeDuration(Long workOvertimeTimeDuration) {
        this.workOvertimeTimeDuration = workOvertimeTimeDuration;
    }

    /**
     * 获取当天是否为法定节假日（Integer）
     *
     * @return
     */
    public Boolean getIsHoliday() {
        return isHoliday;
    }

    /**
     * 设置当天是否为法定节假日（Integer）
     *
     * @param isHoliday
     */
    public void setIsHoliday(Boolean isHoliday) {
        this.isHoliday = isHoliday;
    }

    /**
     * 获取员工出勤时长（分钟）（Long）
     *
     * @return
     */
    public Long getWorkTimeDuration() {
        return workTimeDuration;
    }

    /**
     * 设置员工出勤时长（分钟）（Long）
     *
     * @param workTimeDuration
     */
    public void setWorkTimeDuration(Long workTimeDuration) {
        this.workTimeDuration = workTimeDuration;
    }

    /**
     * 获取员工出勤天数（0，0.5，1）（Double）
     *
     * @return
     */
    public Double getAttendance() {
        return attendance;
    }

    /**
     * 设置员工出勤天数（0，0.5，1）（Double）
     *
     * @param attendance
     */
    public void setAttendance(Double attendance) {
        this.attendance = attendance;
    }

    /**
     * 获取员工上班打卡时间（String）
     *
     * @return
     */
    public String getOnDutyTime() {
        return onDutyTime;
    }

    /**
     * 设置员工上班打卡时间（String）
     *
     * @param onDutyTime
     */
    public void setOnDutyTime(String onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    /**
     * 获取员工下班打卡时间（String）
     *
     * @return
     */
    public String getOffDutyTime() {
        return offDutyTime;
    }

    /**
     * 设置员工下班打卡时间（String）
     *
     * @param offDutyTime
     */
    public void setOffDutyTime(String offDutyTime) {
        this.offDutyTime = offDutyTime;
    }

    /**
     * 获取员工打卡记录分析状态：0-未分析 1-已分析 -1-分析有错误（String）
     *
     * @return
     */
    public Integer getRecordStatus() {
        return recordStatus;
    }

    /**
     * 设置员工打卡记录分析状态：0-未分析 1-已分析 -1-分析有错误（String）
     *
     * @param recordStatus
     */
    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * 获取员工打卡当天是否调休的工作日（String）
     *
     * @return
     */
    public Boolean getIsWorkday() {
        return isWorkday;
    }

    /**
     * 设置员工打卡当天是否调休的工作日（String）
     *
     * @param isWorkday
     */
    public void setIsWorkday(Boolean isWorkday) {
        this.isWorkday = isWorkday;
    }

    /**
     * 获取员工打卡当天是否周末（String）
     *
     * @return
     */
    public Boolean getIsWeekend() {
        return isWeekend;
    }

    /**
     * 设置员工打卡当天是否周末（String）
     *
     * @param isWeekend
     */
    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    /**
     * 获取员工打卡记录分析处理说明：系统分析时自动填写（String）
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置员工打卡记录分析处理说明：系统分析时自动填写（String）
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取员工所属顶层组织名称（String）
     *
     * @return
     */
    public String getTopUnitName() {
        return topUnitName;
    }

    /**
     * 设置员工所属顶层组织名称（String）
     *
     * @param topUnitName
     */
    public void setTopUnitName(String topUnitName) {
        this.topUnitName = topUnitName;
    }

    /**
     * 获取员工所属组织名称（String）
     *
     * @return
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * 设置员工所属组织名称（String）
     *
     * @param unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * 获取员工打卡记录所在年份（String）
     *
     * @return
     */
    public String getYearString() {
        return yearString;
    }

    /**
     * 设置员工打卡记录所在年份（String）
     *
     * @param yearString
     */
    public void setYearString(String yearString) {
        this.yearString = yearString;
    }

    /**
     * 获取员工打卡记录所在月份（String）
     *
     * @return
     */
    public String getMonthString() {
        return monthString;
    }

    /**
     * 设置员工打卡记录所在月份（String）
     *
     * @param monthString
     */
    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    /**
     * 获取员工是否处于休假中（Integer）
     *
     * @return
     */
    public Boolean getIsGetSelfHolidays() {
        return isGetSelfHolidays;
    }

    /**
     * 设置员工是否处于休假中（Integer）
     *
     * @param isGetSelfHolidays
     */
    public void setIsGetSelfHolidays(Boolean isGetSelfHolidays) {
        this.isGetSelfHolidays = isGetSelfHolidays;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    /**
     * 获取休假时段：无，上午，下午，全天（String）
     *
     * @return
     */
    public String getSelfHolidayDayTime() {
        return selfHolidayDayTime;
    }

    /**
     * 设置休假时段：无，上午，下午，全天（String）
     *
     * @param selfHolidayDayTime
     */
    public void setSelfHolidayDayTime(String selfHolidayDayTime) {
        this.selfHolidayDayTime = selfHolidayDayTime;
    }

    /**
     * 获取员工休假天数：0,0.5,1（String）
     *
     * @return
     */
    public Double getGetSelfHolidayDays() {
        return getSelfHolidayDays;
    }

    /**
     * 设置员工休假天数：0,0.5,1（String）
     *
     * @param getSelfHolidayDays
     */
    public void setGetSelfHolidayDays(Double getSelfHolidayDays) {
        this.getSelfHolidayDays = getSelfHolidayDays;
    }

    /**
     * 获取员工缺勤天数：0,0.5,1（String）
     *
     * @return
     */
    public Double getAbsence() {
        return absence;
    }

    /**
     * 设置员工缺勤天数：0,0.5,1（String）
     *
     * @param absence
     */
    public void setAbsence(Double absence) {
        this.absence = absence;
    }

    /**
     * 获取员工打卡记录分析结果申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
     *
     * @return
     */
    public Integer getAppealStatus() {
        return appealStatus;
    }

    /**
     * 获取员工打卡记录分析结果申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
     *
     * @param appealStatus
     */
    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    /**
     * 获取员工打卡记录分析结果申诉简要理由（String）
     *
     * @return
     */
    public String getAppealReason() {
        return appealReason;
    }

    /**
     * 设置员工打卡记录分析结果申诉简要理由（String）
     *
     * @param appealReason
     */
    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    /**
     * 获取员工打卡记录分析结果申诉详细理由（String）
     *
     * @return
     */
    public String getAppealDescription() {
        return appealDescription;
    }

    /**
     * 设置员工打卡记录分析结果申诉详细理由（String）
     *
     * @param appealDescription
     */
    public void setAppealDescription(String appealDescription) {
        this.appealDescription = appealDescription;
    }

    /**
     * 获取员工缺勤时段：无，上午，下午，全天（String）
     *
     * @return
     */
    public String getAbsentDayTime() {
        return absentDayTime;
    }

    /**
     * 设置员工缺勤时段：无，上午，下午，全天（String）
     *
     * @param absentDayTime
     */
    public void setAbsentDayTime(String absentDayTime) {
        this.absentDayTime = absentDayTime;
    }

    /**
     * 获取员工打卡异常时段：无，上午，下午，全天（String）
     *
     * @return
     */
    public String getAbnormalDutyDayTime() {
        return abnormalDutyDayTime;
    }

    /**
     * 设置员工打卡异常时段：无，上午，下午，全天（String）
     *
     * @param abnormalDutyDayTime
     */
    public void setAbnormalDutyDayTime(String abnormalDutyDayTime) {
        this.abnormalDutyDayTime = abnormalDutyDayTime;
    }

    /**
     * 获取员工是否打卡异常（Integer）
     *
     * @return
     */
    public Boolean getIsAbnormalDuty() {
        return isAbnormalDuty;
    }

    /**
     * 设置员工是否打卡异常（Integer）
     *
     * @param isAbnormalDuty
     */
    public void setIsAbnormalDuty(Boolean isAbnormalDuty) {
        this.isAbnormalDuty = isAbnormalDuty;
    }

    public String getCycleYear() {
        return cycleYear;
    }

    public void setCycleYear(String cycleYear) {
        this.cycleYear = cycleYear;
    }

    public String getCycleMonth() {
        return cycleMonth;
    }

    public void setCycleMonth(String cycleMonth) {
        this.cycleMonth = cycleMonth;
    }

    public String getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(String archiveTime) {
        this.archiveTime = archiveTime;
    }

    public String getAppealProcessor() {
        return appealProcessor;
    }

    public void setAppealProcessor(String appealProcessor) {
        this.appealProcessor = appealProcessor;
    }

    public String getMiddayRestStartTime() {
        return middayRestStartTime;
    }

    public void setMiddayRestStartTime(String middayRestStartTime) {
        this.middayRestStartTime = middayRestStartTime;
    }

    public String getMiddayRestEndTime() {
        return middayRestEndTime;
    }

    public void setMiddayRestEndTime(String middayRestEndTime) {
        this.middayRestEndTime = middayRestEndTime;
    }

    public Boolean getHoliday() {
        return isHoliday;
    }

    public void setHoliday(Boolean holiday) {
        isHoliday = holiday;
    }

    public Boolean getWorkday() {
        return isWorkday;
    }

    public void setWorkday(Boolean workday) {
        isWorkday = workday;
    }

    public Boolean getGetSelfHolidays() {
        return isGetSelfHolidays;
    }

    public void setGetSelfHolidays(Boolean getSelfHolidays) {
        isGetSelfHolidays = getSelfHolidays;
    }

    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
    }

    public Boolean getAbnormalDuty() {
        return isAbnormalDuty;
    }

    public void setAbnormalDuty(Boolean abnormalDuty) {
        isAbnormalDuty = abnormalDuty;
    }

    public Boolean getLackOfTime() {
        return isLackOfTime;
    }

    public void setLackOfTime(Boolean lackOfTime) {
        isLackOfTime = lackOfTime;
    }

    public Boolean getWorkOvertime() {
        return isWorkOvertime;
    }

    public void setWorkOvertime(Boolean workOvertime) {
        isWorkOvertime = workOvertime;
    }

    public Boolean getLeaveEarlier() {
        return isLeaveEarlier;
    }

    public void setLeaveEarlier(Boolean leaveEarlier) {
        isLeaveEarlier = leaveEarlier;
    }

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public Boolean getWeekend() {
        return isWeekend;
    }

    public void setWeekend(Boolean weekend) {
        isWeekend = weekend;
    }

    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    /**
     * 清除对该条数据信息的分析结果
     */
    public void refresh() {
        this.recordStatus = 0; // 记录分析状态
        this.cycleYear = "1999"; // 统计年份
        this.cycleMonth = "01"; // 统计月份

        this.isLackOfTime = false; // 是否工时不足
        this.isAbnormalDuty = false; // 是否异常打卡
        this.isAbsent = false; // 是否缺勤
        this.isLate = false; // 是否迟到
        this.isLeaveEarlier = false; // 是否早退
        this.isWorkOvertime = false; // 是否加班

        this.isWorkday = false; // 是否调休工作日
        this.isHoliday = false; // 是否节假日
        this.isWeekend = false; // 是否周末
        this.isGetSelfHolidays = false; // 是否休假

        this.absence = 0.0; // 缺勤天数
        this.attendance = 1.0; // 出勤天数
        this.lateTimeDuration = 0L; // 迟到时长（分钟）
        this.leaveEarlierTimeDuration = 0L; // 早退时长（分钟）

        this.workOvertimeTimeDuration = 0L; // 加班时长（分钟）
        this.workTimeDuration = 0L; // 工作时长（分钟）

        this.appealDescription = ""; // 申诉详情
        this.appealReason = ""; // 申诉原因
        this.appealStatus = 0; // 申诉状态
        this.appealProcessor = "";

        this.absentDayTime = "无"; // 缺勤时段：无，上午，下午，全天
        this.selfHolidayDayTime = "无"; // 休假时段：无，上午，下午，全天
        this.abnormalDutyDayTime = "无"; // 异常打卡时段：无，上午，下午，全天
    }
}