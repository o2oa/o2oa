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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceDetail.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetail.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
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
	@FieldDescribe("员工号")
	@Column(name = "xempNo", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String empNo;

	@FieldDescribe("员工姓名")
	@Column(name = "xempName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String empName;

	@FieldDescribe("顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	@FieldDescribe("组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	@FieldDescribe("打卡记录年份")
	@Column(name = "xyearString", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String yearString;

	@FieldDescribe("打卡记录月份")
	@Column(name = "xmonthString", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String monthString;

	@FieldDescribe("打卡记录日期字符串")
	@Column(name = "xrecordDateString", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String recordDateString;

	@FieldDescribe("打卡记录日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xrecordDate")
	@CheckPersist(allowEmpty = true)
	private Date recordDate;

	@FieldDescribe("统计周期年份")
	@Column(name = "xcycleYear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String cycleYear;

	@FieldDescribe("统计周期月份")
	@Column(name = "xcycleMonth", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String cycleMonth;

	@FieldDescribe("请假时段:上午|下午|全天")
	@Column(name = "xselfHolidayDayTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String selfHolidayDayTime = "无";

	@FieldDescribe("缺勤时段:上午|下午|全天")
	@Column(name = "xabsentDayTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String absentDayTime = "无";

	@FieldDescribe("异常打卡时段:上午|下午|全天")
	@Column(name = "xabnormalDutyDayTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String abnormalDutyDayTime = "无";

	@FieldDescribe("休假天数: 0|0.5|1")
	@Column(name = "xgetSelfHolidayDays")
	@CheckPersist(allowEmpty = false)
	private Double getSelfHolidayDays = 0.0;

	@FieldDescribe("上班时间")
	@Column(name = "xonWorkTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String onWorkTime;

	@FieldDescribe("下班时间")
	@Column(name = "xoffWorkTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String offWorkTime;

	@FieldDescribe("上班打卡时间")
	@Column(name = "xonDutyTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String onDutyTime;

	@FieldDescribe("下班打卡时间")
	@Column(name = "xoffDutyTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String offDutyTime;

	@FieldDescribe("迟到时长")
	@Column(name = "xlateTimeDuration")
	private Long lateTimeDuration = 0L;

	@FieldDescribe("早退时长")
	@Column(name = "xleaveEarlierTimeDuration")
	private Long leaveEarlierTimeDuration = 0L;

	@FieldDescribe("加班时长")
	@Column(name = "xworkOvertimeTimeDuration")
	@CheckPersist(allowEmpty = false)
	private Long workOvertimeTimeDuration = 0L;

	@FieldDescribe("出勤时长")
	@Column(name = "xworkTimeDuration")
	private Long workTimeDuration = 0L;

	@FieldDescribe("出勤天数（0|0.5|1）")
	@Column(name = "xattendance")
	private Double attendance = 1.0;

	@FieldDescribe("缺勤天数（0|0.5|1）")
	@Column(name = "xabsence")
	private Double absence = 0.0;

	@FieldDescribe("记录状态：0-未分析 1-已分析")
	@Column(name = "xrecordStatus")
	private Integer recordStatus = 0;

	@FieldDescribe("导入批次号:导入文件的ID")
	@Column(name = "xbatchName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String batchName;

	@FieldDescribe("说明备注")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过")
	@Column(name = "xappealStatus")
	private Integer appealStatus = 0;

	@FieldDescribe("申诉原因")
	@Column(name = "xappealReason", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String appealReason;

	@FieldDescribe("申诉处理人")
	@Column(name = "xappealProcessor", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String appealProcessor;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("申诉具体说明")
	@Column(name = "xappealDescription", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String appealDescription;

	@FieldDescribe("归档时间")
	@Column(name = "xarchiveTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String archiveTime;

	@FieldDescribe("是否法定节假日")
	@Column(name = "xisHoliday")
	@CheckPersist(allowEmpty = false)
	private Boolean isHoliday = false;

	@FieldDescribe("是否调休工作日")
	@Column(name = "xisWorkday")
	@CheckPersist(allowEmpty = false)
	private Boolean isWorkday = false;

	@FieldDescribe("是否休假")
	@Column(name = "xisGetSelfHolidays")
	@CheckPersist(allowEmpty = false)
	private Boolean isGetSelfHolidays = false;

	@FieldDescribe("是否缺勤")
	@Column(name = "xisAbsent")
	private Boolean isAbsent = false;

	@FieldDescribe("是否异常打卡")
	@Column(name = "xisAbnormalDuty")
	private Boolean isAbnormalDuty = false;

	@FieldDescribe("是否工时不足")
	@Column(name = "xisLackOfTime")
	private Boolean isLackOfTime = false;

	@FieldDescribe("是否加班")
	@Column(name = "xisWorkOvertime")
	private Boolean isWorkOvertime = false;

	@FieldDescribe("是否早退")
	@Column(name = "xisLeaveEarlier")
	private Boolean isLeaveEarlier = false;

	@FieldDescribe("是否迟到")
	@Column(name = "xisLate")
	private Boolean isLate = false;

	@FieldDescribe("是否周末")
	@Column(name = "xisWeekend")
	@CheckPersist(allowEmpty = false)
	private Boolean isWeekend = false;

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
	 * @param isLeaveEarlier
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