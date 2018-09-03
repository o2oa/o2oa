package com.x.cms.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
 * 内容管理应用目录分类信息
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.FileInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.FileInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.cms)
public class FileInfo extends StorageObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.FileInfo.table;

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

	@FieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = true)
	private Date lastUpdateTime;

	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + "_storage")
	private String storage;

	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = "xsite")
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
	public String path() throws Exception {
		if (null == this.appId) {
			throw new Exception("appId can not be null.");
		}
		if (null == this.categoryId) {
			throw new Exception("categoryId can not be null.");
		}
		if (null == this.documentId) {
			throw new Exception("documentId can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.appId;
		str += PATHSEPARATOR;
		str += this.categoryId;
		str += PATHSEPARATOR;
		str += this.documentId;
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
	@FieldDescribe("文件真实名称")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_name")
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name;

	@FieldDescribe("云文件ID")
	@Column(name = "xcloudId", length = JpaObject.length_id)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String cloudId;

	@FieldDescribe("服务器上编码后的文件名,为了方便辨识带扩展名")
	@Column(name = "xfileName", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName;

	@FieldDescribe("文件所属应用ID")
	@Column(name = "xappId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String appId;

	@FieldDescribe("文件所属分类ID")
	@Column(name = "xcategoryId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	@FieldDescribe("文件所属文档ID")
	@Column(name = "xdocumentId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	@FieldDescribe("文件类别：云文件（CLOUD） | 附件(ATTACHMENT)")
	@Column(name = "xfileType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String fileType;

	@FieldDescribe("文件类别：PICTURE | WORD | EXCEL | PPT | ZIP | TXT | OTHER")
	@Column(name = "xfileExtType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String fileExtType;

	@FieldDescribe("文件存储主机名")
	@Column(name = "xfileHost", length = JpaObject.length_32B)
	private String fileHost;

	@FieldDescribe("文件存储路径")
	@Column(name = "xfilePath", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String filePath;

	@FieldDescribe("文件说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("创建者UID")
	@Column(name = "xcreatorUid", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String creatorUid;

	@FieldDescribe("扩展名")
	@Column(name = "xextension", length = JpaObject.length_16B)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension;

	@FieldDescribe("扩展名")
	@Column(name = "xseqNumber")
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private Integer seqNumber = 1000;

	@FieldDescribe("文件大小.")
	@Column(name = "xlength")
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = true)
	private Long length;

	/**
	 * 获取文件所属应用ID
	 * 
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * 设置文件所属应用ID
	 * 
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

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
	 * 获取文件名称
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名称
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件所属分类ID
	 * 
	 * @return
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * 设置文件所属分类ID
	 * 
	 * @param categoryId
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * 获取文件所属应用ID
	 * 
	 * @return
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * 设置文件所属应用ID
	 * 
	 * @param documentId
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * 获取文件的类别：文件（FILE）|附件(ATTACHMENT)
	 * 
	 * @return
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * 设置文件的类别：文件（FILE）|附件(ATTACHMENT)
	 * 
	 * @param fileType
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getFileExtType() {
		return fileExtType;
	}

	public void setFileExtType(String fileExtType) {
		this.fileExtType = fileExtType;
	}

	public String getCloudId() {
		return cloudId;
	}

	public void setCloudId(String cloudId) {
		this.cloudId = cloudId;
	}

	public Integer getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(Integer seqNumber) {
		this.seqNumber = seqNumber;
	}
}