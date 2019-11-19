package com.x.okr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 工作汇报处理链信息，记录工作汇报的处理人信息以及步骤信息，流程按处理人链一步一步执行 形成一个简易的流程控制过程
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkReportPersonLink.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkReportPersonLink.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkReportPersonLink extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkReportPersonLink.table;

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
	public static final String workReportId_FIELDNAME = "workReportId";
	@FieldDescribe("工作汇报ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workReportId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workReportId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workReportId = "";

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerId = "";

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerTitle = "";

	public static final String processorName_FIELDNAME = "processorName";
	@FieldDescribe("处理人姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processorName = "";

	public static final String processorIdentity_FIELDNAME = "processorIdentity";
	@FieldDescribe("处理人身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processorIdentity = "";

	public static final String processorUnitName_FIELDNAME = "processorUnitName";
	@FieldDescribe("处理人所属组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processorUnitName = "";

	public static final String processorTopUnitName_FIELDNAME = "processorTopUnitName";
	@FieldDescribe("处理人所属顶层组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ processorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processorTopUnitName = "";

	public static final String processLevel_FIELDNAME = "processLevel";
	@FieldDescribe("处理层级次序")
	@Column(name = ColumnNamePrefix + processLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer processLevel = 1;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("处理环节名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName = "";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	public static final String processStatus_FIELDNAME = "processStatus";
	@FieldDescribe("处理状态：待处理|处理中|已处理")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + processStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String processStatus = "待处理";

	/**
	 * 获取工作汇报信息ID
	 * 
	 * @return
	 */
	public String getWorkReportId() {
		return workReportId;
	}

	/**
	 * 设置工作汇报信息ID
	 * 
	 * @param workReportId
	 */
	public void setWorkReportId(String workReportId) {
		this.workReportId = workReportId;
	}

	/**
	 * 获取工作标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置工作标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取所属中心工作ID
	 * 
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * 设置所属中心工作ID
	 * 
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * 获取中心工作标题
	 * 
	 * @return
	 */
	public String getCenterTitle() {
		return centerTitle;
	}

	/**
	 * 设置中心工作标题
	 * 
	 * @param centerTitle
	 */
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	/**
	 * 获取工作ID
	 * 
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}

	/**
	 * 设置工作ID
	 * 
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

	/**
	 * 获取处理人姓名
	 * 
	 * @return
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * 设置处理人姓名
	 * 
	 * @param processorName
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	/**
	 * 获取处理人所属组织名称
	 * 
	 * @return
	 */
	public String getProcessorUnitName() {
		return processorUnitName;
	}

	/**
	 * 设置处理人所属组织名称
	 * 
	 * @param processorUnitName
	 */
	public void setProcessorUnitName(String processorUnitName) {
		this.processorUnitName = processorUnitName;
	}

	/**
	 * 获取处理人所属顶层组织名称
	 * 
	 * @return
	 */
	public String getProcessorTopUnitName() {
		return processorTopUnitName;
	}

	/**
	 * 设置处理人所属顶层组织名称
	 * 
	 * @param processorTopUnitName
	 */
	public void setProcessorTopUnitName(String processorTopUnitName) {
		this.processorTopUnitName = processorTopUnitName;
	}

	/**
	 * 获取处理层级次序
	 * 
	 * @return
	 */
	public Integer getProcessLevel() {
		return processLevel;
	}

	/**
	 * 设置处理层级次序
	 * 
	 * @param processLevel
	 */
	public void setProcessLevel(Integer processLevel) {
		this.processLevel = processLevel;
	}

	/**
	 * 获取处理环节名称
	 * 
	 * @return
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * 设置处理环节名称
	 * 
	 * @param activityName
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * 获取信息状态：正常|已删除
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置信息状态：正常|已删除
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessorIdentity() {
		return processorIdentity;
	}

	public void setProcessorIdentity(String processorIdentity) {
		this.processorIdentity = processorIdentity;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

}