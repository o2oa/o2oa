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
@Table(name = PersistenceProperties.StatisticDepartmentForDay.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticDepartmentForDay extends SliceJpaObject {

	private static final long serialVersionUID = 440601082336632065L;
	private static final String TABLE = PersistenceProperties.StatisticDepartmentForDay.table;

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
	public void prePersist() throws Exception { 
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join( this.statisticDate, this.companyName + this.organizationName, this.getId() );
			//this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
		}
		this.onPersist();
	}
	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
		//System.out.println(  this.toValueString() );
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
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
	
	@EntityFieldDescribe("统计日期")
	@Column(name="xstatisticDate", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String statisticDate;

	@EntityFieldDescribe("应出勤人数")
	@Column(name="xemployeeCount" )
	private Double employeeCount;
	
	@EntityFieldDescribe("实际出勤人数")
	@Column(name="xonDutyEmployeeCount" )
	private Double onDutyEmployeeCount;
	
	@EntityFieldDescribe("缺勤人数")
	@Column(name="xabsenceDayCount" )
	private Double absenceDayCount;
	
	@EntityFieldDescribe("休假人数")
	@Column(name="xonSelfHolidayEmployeeCount" )
	private Double onSelfHolidayEmployeeCount;
	
	@EntityFieldDescribe("签到人数")
	@Column(name="xonDutyCount" )
	private Long onDutyCount;
	
	@EntityFieldDescribe("签退人数")
	@Column(name="xoffDutyCount" )
	private Long offDutyCount;
	
	@EntityFieldDescribe("迟到人数")
	@Column(name="xlateCount" )
	private Long lateCount;
	
	@EntityFieldDescribe("早退人数")
	@Column(name="xleaveEarlyCount" )
	private Long leaveEarlyCount;

	@EntityFieldDescribe("工时不足人数")
	@Column(name="xlackOfTimeCount" )
	private Long lackOfTimeCount;

	@EntityFieldDescribe("异常打卡人数")
	@Column(name="xabNormalDutyCount" )
	private Long abNormalDutyCount;

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
	public String getStatisticDate() {
		return statisticDate;
	}
	public void setStatisticDate(String statisticDate) {
		this.statisticDate = statisticDate;
	}
	public Double getEmployeeCount() {
		return employeeCount;
	}
	public void setEmployeeCount(Double employeeCount) {
		this.employeeCount = employeeCount;
	}
	public Double getOnDutyEmployeeCount() {
		return onDutyEmployeeCount;
	}
	public void setOnDutyEmployeeCount(Double onDutyEmployeeCount) {
		this.onDutyEmployeeCount = onDutyEmployeeCount;
	}
	public Double getAbsenceDayCount() {
		return absenceDayCount;
	}
	public void setAbsenceDayCount(Double absenceDayCount) {
		this.absenceDayCount = absenceDayCount;
	}
	public Double getOnSelfHolidayEmployeeCount() {
		return onSelfHolidayEmployeeCount;
	}
	public void setOnSelfHolidayEmployeeCount(Double onSelfHolidayEmployeeCount) {
		this.onSelfHolidayEmployeeCount = onSelfHolidayEmployeeCount;
	}
	public Long getOnDutyCount() {
		return onDutyCount;
	}
	public void setOnDutyCount(Long onDutyCount) {
		this.onDutyCount = onDutyCount;
	}
	public Long getOffDutyCount() {
		return offDutyCount;
	}
	public void setOffDutyCount(Long offDutyCount) {
		this.offDutyCount = offDutyCount;
	}
	public Long getLateCount() {
		return lateCount;
	}
	public void setLateCount(Long lateCount) {
		this.lateCount = lateCount;
	}
	public Long getLeaveEarlyCount() {
		return leaveEarlyCount;
	}
	public void setLeaveEarlyCount(Long leaveEarlyCount) {
		this.leaveEarlyCount = leaveEarlyCount;
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