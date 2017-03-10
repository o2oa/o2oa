package com.x.cms.core.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 内容管理文档大图base64信息
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.DocumentPictureInfo.table )
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage( type = StorageType.cms )
public class DocumentPictureInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentPictureInfo.table;

	/**
	 * 获取应用ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置应用ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * 设置信息记录排序号
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

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() throws Exception { 
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
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
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
	@EntityFieldDescribe("文档ID")
	@Column(name = "xdocumentId", length = JpaObject.length_id )
	@CheckPersist(allowEmpty = true)
	private String documentId;

	@EntityFieldDescribe("应用别名")
	@Column(name = "xappAlias", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	@EntityFieldDescribe("信息说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xbase64", length = JpaObject.length_1M )
	private String base64;

	public String getDocumentId() {
		return documentId;
	}

	public String getAppAlias() {
		return appAlias;
	}

	public String getDescription() {
		return description;
	}

	public String getBase64() {
		return base64;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	
}