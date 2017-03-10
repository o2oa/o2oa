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
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceAppealInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceAppealInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceAppealInfo.table;

	public AttendanceAppealInfo(){}
	
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
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
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
			this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
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
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe( "申诉的打卡记录ID." )
	@Column( name="xdetailId", length = JpaObject.length_id)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String detailId;

	@EntityFieldDescribe("申诉员工姓名")
	@Column(name="xempName", length = AbstractPersistenceProperties.organization_name_length)
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
	
	@EntityFieldDescribe("申诉年份")
	@Column(name="xyearString", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String yearString;
	
	@EntityFieldDescribe("申诉月份")
	@Column(name="xmonthString", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String monthString;
	
	@EntityFieldDescribe("申诉日期字符串")
	@Column(name="xappealDateString", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String appealDateString;
	
	@EntityFieldDescribe("记录日期字符串")
	@Column(name="xrecordDateString", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String recordDateString;
	
	@EntityFieldDescribe("记录日期")
	@Column(name="xrecordDate" )
	@CheckPersist( allowEmpty = true )
	private Date recordDate;	
	
	@EntityFieldDescribe("审批状态:0-待处理，1-审批通过，-1-审批不能过，2-需要下一次审批")
	@Column(name="xstatus")
	@CheckPersist( allowEmpty = true)
	private Integer status = 0;
	
	@EntityFieldDescribe("开始时间")
	@Column(name="xstartTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String startTime;
	
	@EntityFieldDescribe("结束时间")
	@Column(name="xendTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String endTime;
	
	@EntityFieldDescribe("申诉原因")
	@Column(name="xappealReason", length = JpaObject.length_64B)
	@CheckPersist( allowEmpty = true )
	private String appealReason;
	
	@EntityFieldDescribe("请假类型")
	@Column(name="xselfHolidayType", length = JpaObject.length_64B)
	@CheckPersist( allowEmpty = true )
	private String selfHolidayType;
	
	@EntityFieldDescribe("地址")
	@Column(name="xaddress", length = JpaObject.length_255B)
	@CheckPersist( allowEmpty = true )
	private String address;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe("申诉事由")
	@Column( name="xreason", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true )
	private String reason;
	
	@Lob
	@EntityFieldDescribe("申诉详细说明")
	@Column(name="xappealDescription", length = JpaObject.length_2K)
	@CheckPersist( allowEmpty = true )
	private String appealDescription;
	
	@EntityFieldDescribe("当前审核人")
	@Column(name="xcurrentProcessor", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String currentProcessor;
	
	@EntityFieldDescribe("审批人一")
	@Column(name="xprocessPerson1", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processPerson1;
	
	@EntityFieldDescribe("审批人部门一")
	@Column(name="xprocessPersonDepartment1", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processPersonDepartment1;
	
	@EntityFieldDescribe("审批人公司一")
	@Column(name="xprocessPersonCompany1", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processPersonCompany1;
	
	@EntityFieldDescribe("审批意见一")
	@Column(name="xopinion1", length = JpaObject.length_255B)
	@CheckPersist( allowEmpty = true )
	private String opinion1;
	
	@EntityFieldDescribe("审批日期一")
	@Column(name="xprocessTime1" )
	@CheckPersist( allowEmpty = true )
	private Date processTime1;
	
	@EntityFieldDescribe("审批人二")
	@Column(name="xprocessPerson2", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processPerson2;
	
	@EntityFieldDescribe("审批人部门二")
	@Column(name="xprocessPersonDepartment2", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processPersonDepartment2;
	
	@EntityFieldDescribe("审批人公司二")
	@Column(name="xprocessPersonCompany2", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processPersonCompany2;
	
	@EntityFieldDescribe("审批意见二")
	@Column(name="xopinion2", length = JpaObject.length_255B)
	@CheckPersist( allowEmpty = true )
	private String opinion2;
	
	@EntityFieldDescribe("审批日期二")
	@Column(name="xprocessTime2" )
	@CheckPersist( allowEmpty = true )
	private Date processTime2;

	@EntityFieldDescribe("归档时间")
	@Column(name="xarchiveTime", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String archiveTime;
	
	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
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

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAppealReason() {
		return appealReason;
	}

	public void setAppealReason(String appealReason) {
		this.appealReason = appealReason;
	}

	public String getAppealDescription() {
		return appealDescription;
	}

	public void setAppealDescription(String appealDescription) {
		this.appealDescription = appealDescription;
	}

	public String getProcessPerson1() {
		return processPerson1;
	}

	public void setProcessPerson1(String processPerson1) {
		this.processPerson1 = processPerson1;
	}

	public String getProcessPersonDepartment1() {
		return processPersonDepartment1;
	}

	public void setProcessPersonDepartment1(String processPersonDepartment1) {
		this.processPersonDepartment1 = processPersonDepartment1;
	}

	public String getProcessPersonCompany1() {
		return processPersonCompany1;
	}

	public void setProcessPersonCompany1(String processPersonCompany1) {
		this.processPersonCompany1 = processPersonCompany1;
	}

	public String getOpinion1() {
		return opinion1;
	}

	public void setOpinion1(String opinion1) {
		this.opinion1 = opinion1;
	}

	public Date getProcessTime1() {
		return processTime1;
	}

	public void setProcessTime1(Date processTime1) {
		this.processTime1 = processTime1;
	}

	public String getAppealDateString() {
		return appealDateString;
	}

	public void setAppealDateString(String appealDateString) {
		this.appealDateString = appealDateString;
	}

	public String getProcessPerson2() {
		return processPerson2;
	}

	public void setProcessPerson2(String processPerson2) {
		this.processPerson2 = processPerson2;
	}

	public String getProcessPersonDepartment2() {
		return processPersonDepartment2;
	}

	public void setProcessPersonDepartment2(String processPersonDepartment2) {
		this.processPersonDepartment2 = processPersonDepartment2;
	}

	public String getProcessPersonCompany2() {
		return processPersonCompany2;
	}

	public void setProcessPersonCompany2(String processPersonCompany2) {
		this.processPersonCompany2 = processPersonCompany2;
	}

	public String getOpinion2() {
		return opinion2;
	}

	public void setOpinion2(String opinion2) {
		this.opinion2 = opinion2;
	}

	public Date getProcessTime2() {
		return processTime2;
	}

	public void setProcessTime2(Date processTime2) {
		this.processTime2 = processTime2;
	}

	public String getCurrentProcessor() {
		return currentProcessor;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setCurrentProcessor(String currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSelfHolidayType() {
		return selfHolidayType;
	}

	public void setSelfHolidayType(String selfHolidayType) {
		this.selfHolidayType = selfHolidayType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getArchiveTime() {
		return archiveTime;
	}

	public void setArchiveTime(String archiveTime) {
		this.archiveTime = archiveTime;
	}
}