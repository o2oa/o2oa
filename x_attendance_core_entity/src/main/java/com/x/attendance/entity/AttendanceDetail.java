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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceDetail.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDetail extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceDetail.table;

	public AttendanceDetail(){}
	
	/**
	 * 获取明细记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置明细记录ID
	 */
	public void setId( String id ) {
		this.id = id;
	}	
	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * 设置信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			//this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
			this.sequence = StringUtils.join( this.empName, this.recordDateString, this.getId() );
		}
		this.onPersist();
	}
	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe("员工号")
	@Column(name="xempNo", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String empNo;

	@EntityFieldDescribe("员工姓名")
	@Column(name="xempName", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String empName;
	
	@EntityFieldDescribe("公司名称")
	@Column(name="xcompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String companyName;
	
	@EntityFieldDescribe("部门名称")
	@Column(name="xdepartmentName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String departmentName;
	
	@EntityFieldDescribe("打卡记录年份")
	@Column(name="xyearString", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String yearString;
	
	@EntityFieldDescribe("打卡记录月份")
	@Column(name="xmonthString", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String monthString;
	
	@EntityFieldDescribe("打卡记录日期字符串")
	@Column(name="xrecordDateString", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String recordDateString;
	
	@EntityFieldDescribe("打卡记录日期")
	@Column(name="xrecordDate" )
	@CheckPersist( allowEmpty = true )
	private Date recordDate;
	
	@EntityFieldDescribe("统计周期年份")
	@Column(name="xcycleYear", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String cycleYear;
	
	@EntityFieldDescribe("统计周期月份")
	@Column(name="xcycleMonth", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String cycleMonth;
	
	@EntityFieldDescribe("请假时段:上午|下午|全天")
	@Column(name="xselfHolidayDayTime", length = JpaObject.length_32B)
	@CheckPersist( simplyString = true, allowEmpty = false)
	private String selfHolidayDayTime = "无";
	
	@EntityFieldDescribe("缺勤时段:上午|下午|全天")
	@Column(name="xabsentDayTime", length = JpaObject.length_32B)
	@CheckPersist( simplyString = true, allowEmpty = false)
	private String absentDayTime = "无";
	
	@EntityFieldDescribe("异常打卡时段:上午|下午|全天")
	@Column(name="xabnormalDutyDayTime", length = JpaObject.length_32B)
	@CheckPersist( simplyString = true, allowEmpty = false)
	private String abnormalDutyDayTime = "无";
	
	@EntityFieldDescribe("休假天数: 0|0.5|1")
	@Column(name="xgetSelfHolidayDays")
	@CheckPersist( allowEmpty = false)
	private Double getSelfHolidayDays = 0.0;
	
	@EntityFieldDescribe("上班时间")
	@Column(name="xonWorkTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String onWorkTime;

	@EntityFieldDescribe("下班时间")
	@Column(name="xoffWorkTime", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String offWorkTime;
	
	@EntityFieldDescribe("上班打卡时间")
	@Column(name="xonDutyTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String onDutyTime;

	@EntityFieldDescribe("下班打卡时间")
	@Column(name="xoffDutyTime", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String offDutyTime;	
	
	@EntityFieldDescribe("迟到时长")
	@Column(name="xlateTimeDuration")
	private Long lateTimeDuration = 0L;
	
	@EntityFieldDescribe("早退时长")
	@Column(name="xleaveEarlierTimeDuration")
	private Long leaveEarlierTimeDuration = 0L;
	
	@EntityFieldDescribe("加班时长")
	@Column(name="xworkOvertimeTimeDuration")
	@CheckPersist( allowEmpty = false)
	private Long workOvertimeTimeDuration = 0L;
	
	@EntityFieldDescribe("出勤时长")
	@Column(name="xworkTimeDuration")
	private Long workTimeDuration = 0L;
	
	@EntityFieldDescribe("出勤天数（0|0.5|1）")
	@Column(name="xattendance")
	private Double attendance = 1.0;
	
	@EntityFieldDescribe("缺勤天数（0|0.5|1）")
	@Column(name="xabsence")
	private Double absence = 0.0;
	
	@EntityFieldDescribe("记录状态：0-未分析 1-已分析")
	@Column(name="xrecordStatus")
	private Integer recordStatus = 0;

	@EntityFieldDescribe("导入批次号:导入文件的ID")
	@Column(name="xbatchName", length = JpaObject.length_96B)
	@CheckPersist( allowEmpty = true )
	private String batchName;
	
	@EntityFieldDescribe("说明备注")
	@Column(name="xdescription", length = JpaObject.length_255B)
	@CheckPersist( allowEmpty = true )
	private String description;
	
	@EntityFieldDescribe("申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过")
	@Column(name="xappealStatus")
	private Integer appealStatus = 0;
	
	@EntityFieldDescribe("申诉原因")
	@Column(name="xappealReason", length = JpaObject.length_64B)
	@CheckPersist( allowEmpty = true )
	private String appealReason;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe("申诉具体说明")
	@Column(name="xappealDescription", length = JpaObject.length_2K)
	@CheckPersist( allowEmpty = true )
	private String appealDescription;
	
	@EntityFieldDescribe("归档时间")
	@Column(name="xarchiveTime", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String archiveTime;
	
	@EntityFieldDescribe("是否法定节假日")
	@Column(name="xisHoliday")
	@CheckPersist( allowEmpty = false)
	private Boolean isHoliday = false;
	
	@EntityFieldDescribe("是否调休工作日")
	@Column(name="xisWorkday")
	@CheckPersist( allowEmpty = false)
	private Boolean isWorkday = false;
	
	@EntityFieldDescribe("是否休假")
	@Column(name="xisGetSelfHolidays")
	@CheckPersist( allowEmpty = false)
	private Boolean isGetSelfHolidays = false;
	
	@EntityFieldDescribe("是否缺勤")
	@Column(name="xisAbsent")
	private Boolean isAbsent = false;
	
	@EntityFieldDescribe("是否异常打卡")
	@Column(name="xisAbnormalDuty")
	private Boolean isAbnormalDuty = false;
	
	@EntityFieldDescribe("是否工时不足")
	@Column(name="xisLackOfTime")
	private Boolean isLackOfTime = false;
	
	@EntityFieldDescribe("是否加班")
	@Column(name="xisWorkOvertime")
	private Boolean isWorkOvertime = false;

	@EntityFieldDescribe("是否早退")
	@Column(name="xisLeaveEarlier")
	private Boolean isLeaveEarlier = false;
	
	@EntityFieldDescribe("是否迟到")
	@Column(name="xisLate")
	private Boolean isLate = false;

	@EntityFieldDescribe("是否周末")
	@Column(name="xisWeekend")
	@CheckPersist( allowEmpty = false)
	private Boolean isWeekend = false;
	
	/**
	 * 获取是否工时不足
	 * @return
	 */
	public Boolean getIsLackOfTime() {
		return isLackOfTime;
	}

	/**
	 * 设置是否工时不足
	 * @param isLackOfTime
	 */
	public void setIsLackOfTime(Boolean isLackOfTime) {
		this.isLackOfTime = isLackOfTime;
	}

	/**
	 * 获取员工号（String）
	 * @return
	 */
	public String getEmpNo() {
		return empNo;
	}
	/**
	 * 设置员工号（String）
	 * @param empNo
	 */
	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}
	/**
	 * 获取员工姓名（String）
	 * @return
	 */
	public String getEmpName() {
		return empName;
	}
	/**
	 * 设置员工姓名（String）
	 * @param empName
	 */
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	/**
	 * 获取员工是否已经迟到（Integer）
	 * @return
	 */
	public Boolean getIsLate() {
		return isLate;
	}
	/**
	 * 设置员工是否已经迟到（Integer）
	 * @param isLate
	 */
	public void setIsLate(Boolean isLate) {
		this.isLate = isLate;
	}
	/**
	 * 获取员工是否早退（Integer）
	 * @return
	 */
	public Boolean getIsLeaveEarlier() {
		return isLeaveEarlier;
	}
	/**
	 * 设置员工是否早退（Integer）
	 * @param isLeaveEarlier
	 */
	public void setIsLeaveEarlier(Boolean isLeaveEarlier) {
		this.isLeaveEarlier = isLeaveEarlier;
	}
	/**
	 * 获取员工员工打卡记录日期（String）
	 * @return
	 */
	public String getRecordDateString() {
		return recordDateString;
	}
	/**
	 * 设置员工员工打卡记录日期（String）
	 * @param isLeaveEarlier
	 */
	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}
	/**
	 * 获取员工员工打卡记录日期（Date）
	 * @return
	 */
	public Date getRecordDate() {
		return recordDate;
	}
	/**
	 * 设置员工员工打卡记录日期（Date）
	 * @param recordDate
	 */
	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}
	/**
	 * 获取员工打卡记录导入批次号-导入文件ID（String）
	 * @return
	 */
	public String getBatchName() {
		return batchName;
	}
	/**
	 * 设置员工打卡记录导入批次号-导入文件ID（String）
	 * @param batchName
	 */
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	/**
	 * 获取员工标准上班时间（String）
	 * @return
	 */
	public String getOnWorkTime() {
		return onWorkTime;
	}
	/**
	 * 设置员工标准上班时间（String）
	 * @param onWorkTime
	 */
	public void setOnWorkTime(String onWorkTime) {
		this.onWorkTime = onWorkTime;
	}
	/**
	 * 获取员工标准下班时间（String）
	 * @return
	 */
	public String getOffWorkTime() {
		return offWorkTime;
	}
	/**
	 * 设置员工标准下班时间（String）
	 * @param offWorkTime
	 */
	public void setOffWorkTime(String offWorkTime) {
		this.offWorkTime = offWorkTime;
	}
	/**
	 * 获取员工迟到时长（Long）
	 * @return
	 */
	public Long getLateTimeDuration() {
		return lateTimeDuration;
	}
	/**
	 * 设置员工迟到时长（Long）
	 * @param lateTimeDuration
	 */
	public void setLateTimeDuration(Long lateTimeDuration) {
		this.lateTimeDuration = lateTimeDuration;
	}
	/**
	 * 获取员工早退时长（Long）
	 * @return
	 */
	public Long getLeaveEarlierTimeDuration() {
		return leaveEarlierTimeDuration;
	}
	/**
	 * 设置员工早退时长（Long）
	 * @param leaveEarlierTimeDuration
	 */
	public void setLeaveEarlierTimeDuration(Long leaveEarlierTimeDuration) {
		this.leaveEarlierTimeDuration = leaveEarlierTimeDuration;
	}
	/**
	 * 获取员工是否缺勤（Integer）
	 * @return
	 */
	public Boolean getIsAbsent() {
		return isAbsent;
	}
	/**
	 * 设置员工是否缺勤（Integer）
	 * @param isAbsent
	 */
	public void setIsAbsent(Boolean isAbsent) {
		this.isAbsent = isAbsent;
	}
	/**
	 * 获取员工是否加班（Integer）
	 * @return
	 */
	public Boolean getIsWorkOvertime() {
		return isWorkOvertime;
	}
	/**
	 * 设置员工是否加班（Integer）
	 * @param isWorkOvertime
	 */
	public void setIsWorkOvertime(Boolean isWorkOvertime) {
		this.isWorkOvertime = isWorkOvertime;
	}
	/**
	 * 获取员工加班时长（Long）
	 * @return
	 */
	public Long getWorkOvertimeTimeDuration() {
		return workOvertimeTimeDuration;
	}
	/**
	 * 设置员工加班时长（Long）
	 * @param workOvertimeTimeDuration
	 */
	public void setWorkOvertimeTimeDuration(Long workOvertimeTimeDuration) {
		this.workOvertimeTimeDuration = workOvertimeTimeDuration;
	}
	/**
	 * 获取当天是否为法定节假日（Integer）
	 * @return
	 */
	public Boolean getIsHoliday() {
		return isHoliday;
	}
	/**
	 * 设置当天是否为法定节假日（Integer）
	 * @param isHoliday
	 */
	public void setIsHoliday(Boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	/**
	 * 获取员工出勤时长（分钟）（Long）
	 * @return
	 */
	public Long getWorkTimeDuration() {
		return workTimeDuration;
	}
	/**
	 * 设置员工出勤时长（分钟）（Long）
	 * @param workTimeDuration
	 */
	public void setWorkTimeDuration(Long workTimeDuration) {
		this.workTimeDuration = workTimeDuration;
	}
	/**
	 * 获取员工出勤天数（0，0.5，1）（Double）
	 * @return
	 */
	public Double getAttendance() {
		return attendance;
	}
	/**
	 * 设置员工出勤天数（0，0.5，1）（Double）
	 * @param attendance
	 */
	public void setAttendance(Double attendance) {
		this.attendance = attendance;
	}
	/**
	 * 获取员工上班打卡时间（String）
	 * @return
	 */
	public String getOnDutyTime() {
		return onDutyTime;
	}
	/**
	 * 设置员工上班打卡时间（String）
	 * @param onDutyTime
	 */
	public void setOnDutyTime(String onDutyTime) {
		this.onDutyTime = onDutyTime;
	}
	/**
	 * 获取员工下班打卡时间（String）
	 * @return
	 */
	public String getOffDutyTime() {
		return offDutyTime;
	}
	/**
	 * 设置员工下班打卡时间（String）
	 * @param offDutyTime
	 */
	public void setOffDutyTime(String offDutyTime) {
		this.offDutyTime = offDutyTime;
	}
	/**
	 * 获取员工打卡记录分析状态：0-未分析 1-已分析 -1-分析有错误（String）
	 * @return
	 */
	public Integer getRecordStatus() {
		return recordStatus;
	}
	/**
	 * 设置员工打卡记录分析状态：0-未分析 1-已分析 -1-分析有错误（String）
	 * @param recordStatus
	 */
	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}
	/**
	 * 获取员工打卡当天是否调休的工作日（String）
	 * @return
	 */
	public Boolean getIsWorkday() {
		return isWorkday;
	}
	/**
	 * 设置员工打卡当天是否调休的工作日（String）
	 * @param isWorkday
	 */
	public void setIsWorkday(Boolean isWorkday) {
		this.isWorkday = isWorkday;
	}
	/**
	 * 获取员工打卡当天是否周末（String）
	 * @return
	 */
	public Boolean getIsWeekend() {
		return isWeekend;
	}
	/**
	 * 设置员工打卡当天是否周末（String）
	 * @param isWeekend
	 */
	public void setIsWeekend(Boolean isWeekend) {
		this.isWeekend = isWeekend;
	}
	/**
	 * 获取员工打卡记录分析处理说明：系统分析时自动填写（String）
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置员工打卡记录分析处理说明：系统分析时自动填写（String）
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取员工所属公司名称（String）
	 * @return
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * 设置员工所属公司名称（String）
	 * @param companyName
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * 获取员工所属部门名称（String）
	 * @return
	 */
	public String getDepartmentName() {
		return departmentName;
	}
	/**
	 * 设置员工所属部门名称（String）
	 * @param departmentName
	 */
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	/**
	 * 获取员工打卡记录所在年份（String）
	 * @return
	 */
	public String getYearString() {
		return yearString;
	}
	/**
	 * 设置员工打卡记录所在年份（String）
	 * @param yearString
	 */
	public void setYearString(String yearString) {
		this.yearString = yearString;
	}
	/**
	 * 获取员工打卡记录所在月份（String）
	 * @return
	 */
	public String getMonthString() {
		return monthString;
	}
	/**
	 * 设置员工打卡记录所在月份（String）
	 * @param monthString
	 */
	public void setMonthString(String monthString) {
		this.monthString = monthString;
	}
	/**
	 * 获取员工是否处于休假中（Integer）
	 * @return
	 */
	public Boolean getIsGetSelfHolidays() {
		return isGetSelfHolidays;
	}
	/**
	 * 设置员工是否处于休假中（Integer）
	 * @param isGetSelfHolidays
	 */
	public void setIsGetSelfHolidays(Boolean isGetSelfHolidays) {
		this.isGetSelfHolidays = isGetSelfHolidays;
	}
	/**
	 * 获取休假时段：无，上午，下午，全天（String）
	 * @return
	 */
	public String getSelfHolidayDayTime() {
		return selfHolidayDayTime;
	}
	/**
	 * 设置休假时段：无，上午，下午，全天（String）
	 * @param selfHolidayDayTime
	 */
	public void setSelfHolidayDayTime(String selfHolidayDayTime) {
		this.selfHolidayDayTime = selfHolidayDayTime;
	}
	/**
	 * 获取员工休假天数：0,0.5,1（String）
	 * @return
	 */
	public Double getGetSelfHolidayDays() {
		return getSelfHolidayDays;
	}
	/**
	 * 设置员工休假天数：0,0.5,1（String）
	 * @param getSelfHolidayDays
	 */
	public void setGetSelfHolidayDays(Double getSelfHolidayDays) {
		this.getSelfHolidayDays = getSelfHolidayDays;
	}
	/**
	 * 获取员工缺勤天数：0,0.5,1（String）
	 * @return
	 */
	public Double getAbsence() {
		return absence;
	}
	/**
	 * 设置员工缺勤天数：0,0.5,1（String）
	 * @param absence
	 */
	public void setAbsence(Double absence) {
		this.absence = absence;
	}
	/**
	 * 获取员工打卡记录分析结果申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
	 * @return
	 */
	public Integer getAppealStatus() {
		return appealStatus;
	}
	/**
	 * 获取员工打卡记录分析结果申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
	 * @param appealStatus
	 */
	public void setAppealStatus(Integer appealStatus) {
		this.appealStatus = appealStatus;
	}
	/**
	 * 获取员工打卡记录分析结果申诉简要理由（String）
	 * @return
	 */
	public String getAppealReason() {
		return appealReason;
	}
	/**
	 * 设置员工打卡记录分析结果申诉简要理由（String）
	 * @param appealReason
	 */
	public void setAppealReason(String appealReason) {
		this.appealReason = appealReason;
	}
	/**
	 * 获取员工打卡记录分析结果申诉详细理由（String）
	 * @return
	 */
	public String getAppealDescription() {
		return appealDescription;
	}
	/**
	 * 设置员工打卡记录分析结果申诉详细理由（String）
	 * @param appealDescription
	 */
	public void setAppealDescription(String appealDescription) {
		this.appealDescription = appealDescription;
	}
	/**
	 * 获取员工缺勤时段：无，上午，下午，全天（String）
	 * @return
	 */
	public String getAbsentDayTime() {
		return absentDayTime;
	}
	/**
	 * 设置员工缺勤时段：无，上午，下午，全天（String）
	 * @param absentDayTime
	 */
	public void setAbsentDayTime(String absentDayTime) {
		this.absentDayTime = absentDayTime;
	}
	/**
	 * 获取员工打卡异常时段：无，上午，下午，全天（String）
	 * @return
	 */
	public String getAbnormalDutyDayTime() {
		return abnormalDutyDayTime;
	}
	/**
	 * 设置员工打卡异常时段：无，上午，下午，全天（String）
	 * @param abnormalDutyDayTime
	 */
	public void setAbnormalDutyDayTime(String abnormalDutyDayTime) {
		this.abnormalDutyDayTime = abnormalDutyDayTime;
	}
	/**
	 * 获取员工是否打卡异常（Integer）
	 * @return
	 */
	public Boolean getIsAbnormalDuty() {
		return isAbnormalDuty;
	}
	/**
	 * 设置员工是否打卡异常（Integer）
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

	/**
	 * 清除对该条数据信息的分析结果
	 */
	public void refresh(){
		this.recordStatus = 0;       //记录分析状态
		this.cycleYear = "1999";     //统计年份
		this.cycleMonth = "01";      //统计月份
		
		this.isLackOfTime = false;       //是否工时不足
		this.isAbnormalDuty = false;     //是否异常打卡
		this.isAbsent = false;           //是否缺勤
		this.isLate = false;             //是否迟到
		this.isLeaveEarlier = false;     //是否早退
		this.isWorkOvertime = false;     //是否加班
		
		this.isWorkday = false;          //是否调休工作日
		this.isHoliday = false;          //是否节假日
		this.isWeekend = false;          //是否周末
		this.isGetSelfHolidays = false;  //是否休假
		
		this.absence = 0.0;                   //缺勤天数
		this.attendance = 1.0;                //出勤天数
		this.lateTimeDuration = 0L;          //迟到时长（分钟）
		this.leaveEarlierTimeDuration = 0L;  //早退时长（分钟）
		
		this.workOvertimeTimeDuration = 0L;  //加班时长（分钟）
		this.workTimeDuration = 0L;          //工作时长（分钟）
		 
		this.appealDescription = "";        //申诉详情
		this.appealReason = "";             //申诉原因
		this.appealStatus = 0;              //申诉状态
		
		this.absentDayTime = "无";        //缺勤时段：无，上午，下午，全天
		this.selfHolidayDayTime = "无";   //休假时段：无，上午，下午，全天
		this.abnormalDutyDayTime = "无";  //异常打卡时段：无，上午，下午，全天
	}
}