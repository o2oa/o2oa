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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 内容管理应用目录分类信息
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceImportFileInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceImportFileInfo extends SliceJpaObject {

	private static final long serialVersionUID = 1L;
	private static final String TABLE = PersistenceProperties.AttendanceImportFileInfo.table;

	/**
	 * 获取文件ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置文件ID
	 */
	public void setId( String id ) {
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
	public void setCreateTime( Date createTime ) {
		this.createTime = createTime;
	}
	/**
	 * 获取文件信息更新时间
	 */
	public void setUpdateTime( Date updateTime ) {
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
	public void setSequence( String sequence ) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
	@EntityFieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;
	
	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() throws Exception { 
		Date date = new Date();
		if ( null == this.createTime ) {
			this.createTime = date;
		}
		this.updateTime = date;
		if ( null == this.sequence ) {
			this.sequence = StringUtils.join( DateTools.compact( this.getCreateTime() ), this.getId() );
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

	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe("文件真实名称")
	@Column(name="xfileName", length = AbstractPersistenceProperties.processPlatform_name_length )
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName = "";
	
	@EntityFieldDescribe("文件真实名称")
	@Column(name="xname", length = AbstractPersistenceProperties.processPlatform_name_length )
	@CheckPersist( fileNameString = true, allowEmpty = true )
	private String name = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe("文件内容, 10M大约可以存储50万行Excel")
	@Column(name="xcontent", length = JpaObject.length_10M )
	@CheckPersist( allowEmpty = true)
	private byte[] fileBody;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe("文件数据JSON内容, 10M大约可以存储50万行Excel")
	@Column(name="xdataContent", length = JpaObject.length_10M )
	@CheckPersist( allowEmpty = true)
	private String dataContent;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe("错误数据JSON内容, 10M大约可以存储50万行Excel")
	@Column(name="xerrorContent", length = JpaObject.length_10M )
	@CheckPersist( allowEmpty = true)
	private String errorContent;
	
	@EntityFieldDescribe( "文件说明" )
	@Column( name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( simplyString = false, allowEmpty = true )
	private String description = "";
	
	@EntityFieldDescribe( "创建者UID" )
	@Column( name="xcreatorUid", length = JpaObject.length_64B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String creatorUid = "";
	
	@EntityFieldDescribe("扩展名")
	@Column( name="xextension", length = JpaObject.length_16B)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension = "xlsx";
	
	@EntityFieldDescribe("文件大小.")
	@Column( name="xlength" )
	@CheckPersist(allowEmpty = true)
	private Long length = 0L;
	
	@EntityFieldDescribe("记录行数.")
	@Column( name="xrowCount" )
	@CheckPersist(allowEmpty = true)
	private Long rowCount = 0L;
	
	@EntityFieldDescribe("文件状态:new|imported.")
	@Column( name="xfileStatus", length = JpaObject.length_16B )
	@CheckPersist(allowEmpty = true)
	private String fileStatus = "new";
	
	@EntityFieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_64B, name = "xsite")
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
	
	
}