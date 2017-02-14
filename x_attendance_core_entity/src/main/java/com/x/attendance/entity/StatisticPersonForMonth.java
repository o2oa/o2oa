package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Table(name = PersistenceProperties.StatisticPersonForMonth.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticPersonForMonth extends SliceJpaObject {

	private static final long serialVersionUID = 2751187045074892095L;
	private static final String TABLE = PersistenceProperties.StatisticPersonForMonth.table;

	/**
	 * 获取明细记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置明细记录ID
	 */
	public void setId(String id) {
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
	@Column( name="xsequence", length = AbstractPersistenceProperties.organization_name_length )
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
			this.sequence = StringUtils.join( this.statisticYear + this.statisticMonth, this.employeeName, this.getId() );
			//this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
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
	@EntityFieldDescribe("员工姓名")
	@Column(name="xemployeeName", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String employeeName;
	
	@EntityFieldDescribe("部门名称")
	@Column(name="xorganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = false)
	private String organizationName;

	@EntityFieldDescribe("公司名称")
	@Column(name="xcompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = false )
	private String companyName;
	
	@EntityFieldDescribe("统计年份")
	@Column(name="xstatisticYear", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String statisticYear;
	
	@EntityFieldDescribe("统计月份")
	@Column(name="xstatisticMonth", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String statisticMonth;

	@EntityFieldDescribe("应出勤天数")
	@Column(name="xworkDayCount" )
	private Double workDayCount;
	
	@EntityFieldDescribe("实际出勤天数")
	@Column(name="xonDutyDayCount" )
	private Double onDutyDayCount;
	
	@EntityFieldDescribe("缺勤天数")
	@Column(name="xabsenceDayCount" )
	private Double absenceDayCount;
	
	@EntityFieldDescribe("休假天数")
	@Column(name="xonSelfHolidayCount" )
	private Double onSelfHolidayCount;
	
	@EntityFieldDescribe("签到次数")
	@Column(name="xonDutyTimes" )
	private Long onDutyTimes;
	
	@EntityFieldDescribe("签退次数")
	@Column(name="xoffDutyTimes" )
	private Long offDutyTimes;
	
	@EntityFieldDescribe("迟到次数")
	@Column(name="xlateTimes" )
	private Long lateTimes;
	
	@EntityFieldDescribe("早退次数")
	@Column(name="xleaveEarlyTimes" )
	private Long leaveEarlyTimes;

	@EntityFieldDescribe("工时不足次数")
	@Column(name="xlackOfTimeCount" )
	private Long lackOfTimeCount;

	@EntityFieldDescribe("异常打卡人数")
	@Column(name="xabNormalDutyCount" )
	private Long abNormalDutyCount;

	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getStatisticYear() {
		return statisticYear;
	}
	public void setStatisticYear(String statisticYear) {
		this.statisticYear = statisticYear;
	}
	public String getStatisticMonth() {
		return statisticMonth;
	}
	public void setStatisticMonth(String statisticMonth) {
		this.statisticMonth = statisticMonth;
	}
	public Double getWorkDayCount() {
		return workDayCount;
	}
	public void setWorkDayCount(Double workDayCount) {
		this.workDayCount = workDayCount;
	}
	public Double getOnDutyDayCount() {
		return onDutyDayCount;
	}
	public void setOnDutyDayCount(Double onDutyDayCount) {
		this.onDutyDayCount = onDutyDayCount;
	}
	public Double getAbsenceDayCount() {
		return absenceDayCount<0?0:absenceDayCount;
	}
	public void setAbsenceDayCount(Double absenceDayCount) {
		this.absenceDayCount = absenceDayCount;
	}
	public Double getOnSelfHolidayCount() {
		return onSelfHolidayCount;
	}
	public void setOnSelfHolidayCount(Double onSelfHolidayCount) {
		this.onSelfHolidayCount = onSelfHolidayCount;
	}
	public Long getOnDutyTimes() {
		return onDutyTimes;
	}
	public void setOnDutyTimes(Long onDutyTimes) {
		this.onDutyTimes = onDutyTimes;
	}
	public Long getOffDutyTimes() {
		return offDutyTimes;
	}
	public void setOffDutyTimes(Long offDutyTimes) {
		this.offDutyTimes = offDutyTimes;
	}
	public Long getLateTimes() {
		return lateTimes;
	}
	public void setLateTimes(Long lateTimes) {
		this.lateTimes = lateTimes;
	}
	public Long getLeaveEarlyTimes() {
		return leaveEarlyTimes;
	}
	public void setLeaveEarlyTimes(Long leaveEarlyTimes) {
		this.leaveEarlyTimes = leaveEarlyTimes;
	}
	public Long getLackOfTimeCount() {
		return lackOfTimeCount;
	}
	public void setLackOfTimeCount(Long lackOfTimeCount) {
		this.lackOfTimeCount = lackOfTimeCount;
	}
	public Long getAbNormalDutyCount() {
		return abNormalDutyCount;
	}
	public void setAbNormalDutyCount(Long abNormalDutyCount) {
		this.abNormalDutyCount = abNormalDutyCount;
	}
}