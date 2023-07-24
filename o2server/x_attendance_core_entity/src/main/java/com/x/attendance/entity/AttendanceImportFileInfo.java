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

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceImportFileInfo", description = "考勤雇员配置.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceImportFileInfo.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceImportFileInfo.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceImportFileInfo extends SliceJpaObject {

	private static final long serialVersionUID = 1L;
	private static final String TABLE = PersistenceProperties.AttendanceImportFileInfo.table;

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

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String fileName_FIELDNAME = "fileName";
	@FieldDescribe("文件真实名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ fileName_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName = "";

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件真实名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name = "";

	public static final String fileBody_FIELDNAME = "fileBody";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("文件内容, 10M大约可以存储50万行Excel")
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + fileBody_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private byte[] fileBody;

	public static final String dataContent_FIELDNAME = "dataContent";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("文件数据JSON内容, 10M大约可以存储50万行Excel")
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + dataContent_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dataContent;

	public static final String errorContent_FIELDNAME = "errorContent";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("错误数据JSON内容, 10M大约可以存储50万行Excel")
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + errorContent_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String errorContent;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("文件说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public static final String creatorUid_FIELDNAME = "creatorUid";
	@FieldDescribe("创建者UID")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + creatorUid_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUid = "";

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension = "xlsx";

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length = 0L;

	public static final String rowCount_FIELDNAME = "rowCount";
	@FieldDescribe("记录行数.")
	@Column(name = ColumnNamePrefix + rowCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long rowCount = 0L;

	public static final String fileStatus_FIELDNAME = "fileStatus";
	@FieldDescribe("文件状态:new|imported.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fileStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileStatus = "new";

	public static final String tempFilePath_FIELDNAME = "tempFilePath";
	@FieldDescribe("临时文件地址")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + tempFilePath_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String tempFilePath = null;

	public static final String currentProcessName_FIELDNAME = "currentProcessName";
	@FieldDescribe("当前数据处理操作步骤:GETDATA|CHECKDATA|SAVEDATA|SUPPLEMENT|ANALYSIS")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + currentProcessName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String currentProcessName = "NONE";

	public static final String validateOk_FIELDNAME = "validateOk";
	@FieldDescribe("数据校验结果")
	@Column(name = ColumnNamePrefix + validateOk_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean validateOk = false;

	public static final String processing_FIELDNAME = "processing";
	@FieldDescribe("数据操作状态")
	@Column(name = ColumnNamePrefix + processing_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean processing = false;

	public static final String recordTotle_FIELDNAME = "recordTotle";
	@FieldDescribe("数据总量")
	@Column(name = ColumnNamePrefix + recordTotle_FIELDNAME)
	private Long recordTotle = 0L;

	public static final String processCount_FIELDNAME = "processCount";
	@FieldDescribe("当前数据操作数量")
	@Column(name = ColumnNamePrefix + processCount_FIELDNAME)
	private Long processCount = 0L;

	public static final String startDate_FIELDNAME = "startDate";
	@FieldDescribe("数据开始日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startDate_FIELDNAME)
	private Date startDate = null;

	public static final String endDate_FIELDNAME = "endDate";
	@FieldDescribe("数据结束日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + endDate_FIELDNAME)
	private Date endDate = null;

	public static final String site_FIELDNAME = "site";
	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + site_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String site;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileBody() {
		return fileBody;
	}

	public void setFileBody(byte[] fileBody) {
		this.fileBody = fileBody;
	}

	public String getDataContent() {
		return dataContent;
	}

	public void setDataContent(String dataContent) {
		this.dataContent = dataContent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatorUid() {
		return creatorUid;
	}

	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public Long getRowCount() {
		return rowCount;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	/**
	 * 附件控件框
	 * 
	 * @return
	 */
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getErrorContent() {
		return errorContent;
	}

	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}

	public String getCurrentProcessName() {
		return currentProcessName;
	}

	public Boolean getProcessing() {
		return processing;
	}

	public Long getRecordTotle() {
		return recordTotle;
	}

	public Long getProcessCount() {
		return processCount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setCurrentProcessName(String currentProcessName) {
		this.currentProcessName = currentProcessName;
	}

	public void setProcessing(Boolean processing) {
		this.processing = processing;
	}

	public void setRecordTotle(Long recordTotle) {
		this.recordTotle = recordTotle;
	}

	public void setProcessCount(Long processCount) {
		this.processCount = processCount;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getValidateOk() {
		return validateOk;
	}

	public void setValidateOk(Boolean validateOk) {
		this.validateOk = validateOk;
	}

	public String getTempFilePath() {
		return tempFilePath;
	}

	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}

}