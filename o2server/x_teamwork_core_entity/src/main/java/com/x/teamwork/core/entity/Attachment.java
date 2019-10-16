package com.x.teamwork.core.entity;

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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Attachment.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Attachment.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.teamwork)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Attachment.table;

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

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lastUpdateTime;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String site_FIELDNAME = "site";
	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + site_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String site;

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	@Override
	public String path() throws Exception {
		if (StringUtils.isEmpty(this.projectId)) {
			throw new Exception("projectId can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.projectId;
		if (StringUtils.isNotEmpty(this.taskId)) {
			str += PATHSEPARATOR;
			str += this.taskId;
		}
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件真实名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name;

	public static final String fileName_FIELDNAME = "fileName";
	@FieldDescribe("服务器上编码后的文件名,为了方便辨识带扩展名")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ fileName_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName;

	public static final String projectId_FIELDNAME = "projectId";
	@FieldDescribe("文件所属项目ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + projectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + projectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projectId;

	public static final String taskId_FIELDNAME = "taskId";
	@FieldDescribe("文件所属工作任务ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + taskId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskId;

	public static final String fileType_FIELDNAME = "fileType";
	@FieldDescribe("文件类别：云文件（CLOUD） | 附件(ATTACHMENT)")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fileType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileType;

	public static final String bundleObjType_FIELDNAME = "bundleObjType";
	@FieldDescribe("文件宿主类别：PROJECT | TASK")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + bundleObjType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String bundleObjType;

	public static final String fileHost_FIELDNAME = "fileHost";
	@FieldDescribe("文件存储主机名")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + fileHost_FIELDNAME)
	private String fileHost;

	public static final String filePath_FIELDNAME = "filePath";
	@FieldDescribe("文件存储路径")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + filePath_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String filePath;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("文件说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String creatorUid_FIELDNAME = "creatorUid";
	@FieldDescribe("创建者UID")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ creatorUid_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUid;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension;

	public static final String seqNumber_FIELDNAME = "seqNumber";
	@FieldDescribe("排序号")
	@Column(name = ColumnNamePrefix + seqNumber_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private Integer seqNumber = 1000;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deepPath_FIELDNAME)
	private Boolean deepPath;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileHost() {
		return fileHost;
	}

	public void setFileHost(String fileHost) {
		this.fileHost = fileHost;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public Integer getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(Integer seqNumber) {
		this.seqNumber = seqNumber;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getBundleObjType() {
		return bundleObjType;
	}

	public void setBundleObjType(String bundleObjType) {
		this.bundleObjType = bundleObjType;
	}
}