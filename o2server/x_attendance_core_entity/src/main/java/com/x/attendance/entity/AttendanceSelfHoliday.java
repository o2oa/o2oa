package com.x.attendance.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import javax.persistence.*;
import java.util.Date;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceSelfHoliday.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceSelfHoliday.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceSelfHoliday extends SliceJpaObject {

	private static final long serialVersionUID = 2742632629888741120L;
	private static final String TABLE = PersistenceProperties.AttendanceSelfHoliday.table;

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
	@FieldDescribe("流程WorkId")
	@Column(name = "xdocId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String docId;

	@FieldDescribe("顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	@FieldDescribe("顶层组织编号")
	@Column(name = "xtopUnitOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitOu;

	@FieldDescribe("组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	@FieldDescribe("组织编号")
	@Column(name = "xunitOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitOu;

	@FieldDescribe("员工姓名")
	@Column(name = "xemployeeName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = false)
	private String employeeName;

	@FieldDescribe("员工号")
	@Column(name = "xemployeeNumber", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String employeeNumber;

	@FieldDescribe("请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|其他")
	@Column(name = "xleaveType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String leaveType;

	@FieldDescribe("开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xstartTime")
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	@FieldDescribe("结束时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xendTime")
	@CheckPersist(allowEmpty = false)
	private Date endTime;

	@FieldDescribe("请假天数")
	@Column(name = "xleaveDayNumber")
	@CheckPersist(allowEmpty = true)
	private Double leaveDayNumber = 0.0;

	@FieldDescribe("请假说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("录入批次标识：可以填写流程workId，jobId, CMS的文档ID，或者自定义信息，数据保存时会先根据batchFlag做删除，然后再保存新的数据")
	@Column(name = "xbatchFlag", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String batchFlag;

	public String getBatchFlag() { return this.batchFlag; }

	public void setBatchFlag(final String batchFlag) { this.batchFlag = batchFlag; }

	public String getUnitName() {
		return unitName;
	}

	public String getUnitOu() {
		return unitOu;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setUnitOu(String unitOu) {
		this.unitOu = unitOu;
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

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getTopUnitOu() {
		return topUnitOu;
	}

	public void setTopUnitOu(String topUnitOu) {
		this.topUnitOu = topUnitOu;
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