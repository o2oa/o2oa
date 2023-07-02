package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceSelfHoliday", description = "考勤假日配置.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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
//	public static final String docId_FIELDNAME = "docId";
//	@FieldDescribe("流程WorkId")
//	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + docId_FIELDNAME )
//	@CheckPersist(allowEmpty = true)
//	private String docId;

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("顶层组织名称")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	public static final String topUnitOu_FIELDNAME = "topUnitOu";
	@FieldDescribe("顶层组织编号")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitOu_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String topUnitOu;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("组织名称")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String unitName;

	public static final String unitOu_FIELDNAME = "unitOu";
	@FieldDescribe("组织编号")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitOu_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String unitOu;

	public static final String employeeName_FIELDNAME = "employeeName";
	@FieldDescribe("员工姓名：员工的标识，distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + employeeName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String employeeName;

	public static final String employeeNumber_FIELDNAME = "employeeNumber";
	@FieldDescribe("员工号，如果没有员工号，可以使用员工标识代替，不可为空")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + employeeNumber_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String employeeNumber;

	public static final String leaveType_FIELDNAME = "leaveType";
	@FieldDescribe("请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|出差|培训|其他")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveType_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String leaveType;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("开始时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + startTime_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("结束时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + endTime_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private Date endTime;

	public static final String leaveDayNumber_FIELDNAME = "leaveDayNumber";
	@FieldDescribe("请假天数")
	@Column( name = ColumnNamePrefix + leaveDayNumber_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private Double leaveDayNumber = 0.0;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("请假说明")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String batchFlag_FIELDNAME = "batchFlag";
	@FieldDescribe("录入批次标识：可以填写流程workId，jobId, CMS的文档ID，或者自定义信息，数据保存时会先根据batchFlag做删除，然后再保存新的数据")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + batchFlag_FIELDNAME )
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
//
//	public String getDocId() {
//		return docId;
//	}
//
//	public void setDocId(String docId) {
//		this.docId = docId;
//	}

}