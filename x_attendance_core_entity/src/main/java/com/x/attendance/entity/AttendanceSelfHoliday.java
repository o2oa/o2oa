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
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceSelfHoliday.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceSelfHoliday extends SliceJpaObject {

	private static final long serialVersionUID = 2742632629888741120L;
	private static final String TABLE = PersistenceProperties.AttendanceSelfHoliday.table;

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
	@EntityFieldDescribe("公司名称")
	@Column(name="xdocId", length = JpaObject.length_id)
	@CheckPersist( allowEmpty = true)
	private String docId;
	
	@EntityFieldDescribe("公司名称")
	@Column(name="xcompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String companyName;

	@EntityFieldDescribe("公司编号")
	@Column(name="xcompanyOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String companyOu;
	
	@EntityFieldDescribe("部门名称")
	@Column(name="xorganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String organizationName;

	@EntityFieldDescribe("部门编号")
	@Column(name="xorganizationOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String organizationOu;
	
	@EntityFieldDescribe("员工姓名")
	@Column(name="xemployeeName", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String employeeName;
	
	@EntityFieldDescribe("员工号")
	@Column(name="xemployeeNumber", length = JpaObject.length_32B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String employeeNumber;
	
	@EntityFieldDescribe("请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|其他")
	@Column(name="xleaveType", length = JpaObject.length_32B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String leaveType;
	
	@EntityFieldDescribe("开始时间")
	@Column(name="xstartTime" )
	@CheckPersist( allowEmpty = false )
	private Date startTime;
	
	@EntityFieldDescribe("结束时间")
	@Column(name="xendTime" )
	@CheckPersist( allowEmpty = false )
	private Date endTime;
	
	@EntityFieldDescribe("请假天数")
	@Column(name="xleaveDayNumber" )
	@CheckPersist( allowEmpty = true )
	private Double leaveDayNumber;
	
	@EntityFieldDescribe("请假说明")
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description;

	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getOrganizationOu() {
		return organizationOu;
	}
	public void setOrganizationOu(String organizationOu) {
		this.organizationOu = organizationOu;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Double getLeaveDayNumber() {
		return leaveDayNumber;
	}
	public void setLeaveDayNumber(Double leaveDayNumber) {
		this.leaveDayNumber = leaveDayNumber;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyOu() {
		return companyOu;
	}
	public void setCompanyOu(String companyOu) {
		this.companyOu = companyOu;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
}