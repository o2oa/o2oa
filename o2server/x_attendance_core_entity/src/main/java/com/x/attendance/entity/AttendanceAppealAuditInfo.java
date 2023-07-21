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

@Schema(name = "AttendanceAppealAuditInfo", description = "考勤申诉审批配置.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceAppealAuditInfo.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceAppealAuditInfo.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceAppealAuditInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceAppealAuditInfo.table;

	public AttendanceAppealAuditInfo() {
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
	public static final String detailId_FIELDNAME = "detailId";
	@FieldDescribe("申诉的打卡记录ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + detailId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String detailId;

	public static final String auditFlowType_FIELDNAME = "auditFlowType";
	@FieldDescribe("审批方式:WORKFLOW|BUILTIN，使用自定义流程或者使用内置流程")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + auditFlowType_FIELDNAME)
	private String auditFlowType = AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN;

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("自定义审批流程WorkID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	private String workId;

	public static final String lastFlowSyncTime_FIELDNAME = "lastFlowSyncTime";
	@FieldDescribe("上次进行状态同步时间")
	@Column(name = ColumnNamePrefix + lastFlowSyncTime_FIELDNAME)
	private Date lastFlowSyncTime;

	public static final String currentProcessor_FIELDNAME = "currentProcessor";
	@FieldDescribe("当前审核人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ currentProcessor_FIELDNAME)
	private String currentProcessor;

	public static final String processPerson1_FIELDNAME = "processPerson1";
	@FieldDescribe("审批人一")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processPerson1_FIELDNAME)
	private String processPerson1;

	public static final String processPersonUnit1_FIELDNAME = "processPersonUnit1";
	@FieldDescribe("审批人组织一")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processPersonUnit1_FIELDNAME)
	private String processPersonUnit1;

	public static final String processPersonTopUnit1_FIELDNAME = "processPersonTopUnit1";
	@FieldDescribe("审批人顶层组织一")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processPersonTopUnit1_FIELDNAME)
	private String processPersonTopUnit1;

	public static final String opinion1_FIELDNAME = "opinion1";
	@FieldDescribe("审批意见一")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinion1_FIELDNAME)
	private String opinion1;

	public static final String processTime1_FIELDNAME = "processTime1";
	@FieldDescribe("审批日期一")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + processTime1_FIELDNAME)
	private Date processTime1;

	public static final String processPerson2_FIELDNAME = "processPerson2";
	@FieldDescribe("审批人二")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processPerson2_FIELDNAME)
	private String processPerson2;

	public static final String processPersonUnit2_FIELDNAME = "processPersonUnit2";
	@FieldDescribe("审批人组织二")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processPersonUnit2_FIELDNAME)
	private String processPersonUnit2;

	public static final String processPersonTopUnit2_FIELDNAME = "processPersonTopUnit2";
	@FieldDescribe("审批人顶层组织二")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processPersonTopUnit2_FIELDNAME)
	private String processPersonTopUnit2;

	public static final String opinion2_FIELDNAME = "opinion2";
	@FieldDescribe("审批意见二")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinion2_FIELDNAME)
	private String opinion2;

	public static final String processTime2_FIELDNAME = "processTime2";
	@FieldDescribe("审批日期二")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + processTime2_FIELDNAME)
	private Date processTime2;

	public void setCurrentProcessor(String currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public String getAuditFlowType() {
		return auditFlowType;
	}

	public void setAuditFlowType(String auditFlowType) {
		this.auditFlowType = auditFlowType;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public Date getLastFlowSyncTime() {
		return lastFlowSyncTime;
	}

	public void setLastFlowSyncTime(Date lastFlowSyncTime) {
		this.lastFlowSyncTime = lastFlowSyncTime;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getProcessPerson1() {
		return processPerson1;
	}

	public void setProcessPerson1(String processPerson1) {
		this.processPerson1 = processPerson1;
	}

	public String getProcessPersonUnit1() {
		return processPersonUnit1;
	}

	public void setProcessPersonUnit1(String processPersonUnit1) {
		this.processPersonUnit1 = processPersonUnit1;
	}

	public String getProcessPersonTopUnit1() {
		return processPersonTopUnit1;
	}

	public void setProcessPersonTopUnit1(String processPersonTopUnit1) {
		this.processPersonTopUnit1 = processPersonTopUnit1;
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

	public String getProcessPerson2() {
		return processPerson2;
	}

	public void setProcessPerson2(String processPerson2) {
		this.processPerson2 = processPerson2;
	}

	public String getProcessPersonUnit2() {
		return processPersonUnit2;
	}

	public void setProcessPersonUnit2(String processPersonUnit2) {
		this.processPersonUnit2 = processPersonUnit2;
	}

	public String getProcessPersonTopUnit2() {
		return processPersonTopUnit2;
	}

	public void setProcessPersonTopUnit2(String processPersonTopUnit2) {
		this.processPersonTopUnit2 = processPersonTopUnit2;
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

}