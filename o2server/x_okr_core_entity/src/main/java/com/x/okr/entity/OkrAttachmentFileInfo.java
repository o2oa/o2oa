package com.x.okr.entity;

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

/**
 * 附件文件信息管理表
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrAttachmentFileInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrAttachmentFileInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.okr)
public class OkrAttachmentFileInfo extends StorageObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrAttachmentFileInfo.table;

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
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

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
		if (null == this.centerId) {
			throw new Exception("centerId can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.centerId;
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

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("文件所属中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerId;

	public static final String workInfoId_FIELDNAME = "workInfoId";
	@FieldDescribe("文件所属工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workInfoId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workInfoId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workInfoId;

	public static final String parentType_FIELDNAME = "parentType";
	@FieldDescribe("对象类别:中心工作、工作、工作汇报、问题请示")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + parentType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentType;

	public static final String key_FIELDNAME = "key";
	@FieldDescribe("关键字:存储关键字，工作ID，汇报ID或者问题请示ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + key_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String key;

	public static final String fileHost_FIELDNAME = "fileHost";
	@FieldDescribe("文件存储主机名")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + fileHost_FIELDNAME)
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

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	public static final String site_FIELDNAME = "site";
	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + site_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String site;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deepPath_FIELDNAME)
	private Boolean deepPath;

	/**
	 * 获取分类说明信息
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置分类说明信息
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取分类创建者帐号
	 * 
	 * @return
	 */
	public String getCreatorUid() {
		return creatorUid;
	}

	/**
	 * 设置分类创建者帐号
	 * 
	 * @param creatorUid
	 */
	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

	/**
	 * 获取文件名称：服务器上编码后的文件名,为了方便辨识带扩展名
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名称：服务器上编码后的文件名,为了方便辨识带扩展名
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件存储的主机名
	 * 
	 * @return
	 */
	public String getFileHost() {
		return fileHost;
	}

	/**
	 * 设置文件存储的主机名
	 * 
	 * @param fileHost
	 */
	public void setFileHost(String fileHost) {
		this.fileHost = fileHost;
	}

	/**
	 * 获取文件存储的路径
	 * 
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 设置文件存储的路径
	 * 
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 获取文件真实名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置文件真实名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * 设置文件扩展名
	 * 
	 * @param extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * 获取文件大小
	 * 
	 * @return
	 */
	public Long getLength() {
		return length;
	}

	/**
	 * 设置文件大小
	 * 
	 * @param length
	 */
	public void setLength(Long length) {
		this.length = length;
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
	 * 获取文件所属工作ID
	 * 
	 * @return
	 */
	public String getWorkInfoId() {
		return workInfoId;
	}

	/**
	 * 设置文件所属工作ID
	 * 
	 * @param workInfoId
	 */
	public void setWorkInfoId(String workInfoId) {
		this.workInfoId = workInfoId;
	}

	/**
	 * 获取对象类别:中心工作、工作、工作汇报、问题请示
	 * 
	 * @return
	 */
	public String getParentType() {
		return parentType;
	}

	/**
	 * 设置对象类别:中心工作、工作、工作汇报、问题请示
	 * 
	 * @param parentType
	 */
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	/**
	 * 获取关键字:存储关键字，工作ID，汇报ID或者问题请示ID
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置关键字:存储关键字，工作ID，汇报ID或者问题请示ID
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
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

}