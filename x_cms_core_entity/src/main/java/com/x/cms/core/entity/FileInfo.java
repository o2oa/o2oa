package com.x.cms.core.entity;

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
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 内容管理应用目录分类信息
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.FileInfo.table )
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage( type = StorageType.cms )
public class FileInfo extends StorageObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.FileInfo.table;

	/**
	 * 获取文件ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置文件ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取文件信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置文件信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取文件信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 设置文件信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 获取文件信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * 设置文件信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
	private String sequence;

	@EntityFieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@EntityFieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + "_storage")
	private String storage;

	@EntityFieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = "xsite")
	@CheckPersist(allowEmpty = true)
	private String site;

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}

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
		if (null == this.catagoryId) {
			throw new Exception("catagoryId can not be null.");
		}
		if (null == this.documentId) {
			throw new Exception("documentId can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = DateTools.format(this.createTime, DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.appId;
		str += PATHSEPARATOR;
		str += this.catagoryId;
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
	@EntityFieldDescribe("文件真实名称")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_name")
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name;

	@EntityFieldDescribe("服务器上编码后的文件名,为了方便辨识带扩展名")
	@Column(name = "xfileName", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName;

	@EntityFieldDescribe("文件所属应用ID")
	@Column(name = "xappId", length = JpaObject.length_id)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String appId;

	@EntityFieldDescribe("文件所属分类ID")
	@Column(name = "xcatagoryId", length = JpaObject.length_id)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String catagoryId;

	@EntityFieldDescribe("文件所属文档ID")
	@Column(name = "xdocumentId", length = JpaObject.length_id)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String documentId;

	@EntityFieldDescribe("文件类别：文件（FILE） | 附件(ATTACHMENT)")
	@Column(name = "xfileType", length = JpaObject.length_16B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String fileType;

	@EntityFieldDescribe("文件存储主机名")
	@Column(name = "xfileHost", length = JpaObject.length_32B)
	private String fileHost;

	@EntityFieldDescribe("文件存储路径")
	@Column(name = "xfilePath", length = JpaObject.length_255B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String filePath;

	@EntityFieldDescribe("关联类型的名称.")
	@Column(name = "xstorageName", length = JpaObject.length_64B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	@Index(name = TABLE + "_storageName")
	private String storageName;

	@EntityFieldDescribe("文件说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(simplyString = false, allowEmpty = true)
	private String description;

	@EntityFieldDescribe("创建者UID")
	@Column(name = "xcreatorUid", length = JpaObject.length_64B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String creatorUid;

	@EntityFieldDescribe("扩展名")
	@Column(name = "xextension", length = JpaObject.length_16B)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension;

	@EntityFieldDescribe("文件大小.")
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
	public String getCatagoryId() {
		return catagoryId;
	}

	/**
	 * 设置文件所属分类ID
	 * 
	 * @param catagoryId
	 */
	public void setCatagoryId(String catagoryId) {
		this.catagoryId = catagoryId;
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

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
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