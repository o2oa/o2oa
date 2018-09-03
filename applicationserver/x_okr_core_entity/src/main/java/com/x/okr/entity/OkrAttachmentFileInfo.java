package com.x.okr.entity;

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

	@FieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + "_storage")
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
	@FieldDescribe("文件真实名称")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_name")
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name;

	@FieldDescribe("服务器上编码后的文件名,为了方便辨识带扩展名")
	@Column(name = "xfileName", length = AbstractPersistenceProperties.processPlatform_name_length)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName;

	@FieldDescribe("文件所属中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String centerId;

	@FieldDescribe("文件所属工作ID")
	@Column(name = "xworkInfoId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String workInfoId;

	@FieldDescribe("对象类别:中心工作、工作、工作汇报、问题请示")
	@Column(name = "xparentType", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String parentType;

	@FieldDescribe("关键字:存储关键字，工作ID，汇报ID或者问题请示ID")
	@Column(name = "xkey", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String key;

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

	@FieldDescribe("文件大小.")
	@Column(name = "xlength")
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = true)
	private Long length;

	@FieldDescribe("处理状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = "xsite")
	@CheckPersist(allowEmpty = true)
	private String site;

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