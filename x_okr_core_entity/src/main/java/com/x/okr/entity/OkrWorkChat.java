package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * 工作交流信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkChat.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkChat extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	
	private static final String TABLE = PersistenceProperties.OkrWorkChat.table;

	/**
	 * 获取记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置记录ID
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

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime" )
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime" )
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
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
		
		//序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
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
	
	@EntityFieldDescribe( "所属中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String centerId = null;
	
	@EntityFieldDescribe( "中心工作标题" )
	@Column( name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String centerTitle = null;
	
	@EntityFieldDescribe( "所属工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@Index(name = TABLE + "_workId" )
	@CheckPersist( allowEmpty = true)
	private String workId = null;
	
	@EntityFieldDescribe( "工作标题" )
	@Column( name="xworkTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String workTitle = null;
	
	@EntityFieldDescribe( "发送者姓名" )
	@Column( name="xsenderName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String senderName = null;
	
	@EntityFieldDescribe( "发送者身份" )
	@Column( name="xsenderIdentity", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String senderIdentity = null;
	
	@EntityFieldDescribe( "目标者姓名" )
	@Column( name="xtargetName", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetName = null;
	
	@EntityFieldDescribe( "目标者身份" )
	@Column( name="xtargetIdentity", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetIdentity = null;
	
	@Lob
	@EntityFieldDescribe( "内容" )
	@Column( name="xcontent", length = JpaObject.length_1M )
	@CheckPersist( allowEmpty = true)
	private String content = null;
	
	@EntityFieldDescribe( "备注说明" )
	@Column( name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String description = null;
	
	/**
	 * 获取目标对象名称
	 * @return
	 */
	public String getTargetName() {
		return targetName;
	}
	/**
	 * 设置目标对象名称
	 * @param targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	/**
	 * 获取操作描述
	 * @return
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置操作描述
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取备注说明信息
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置备注说明信息
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 获取中心工作ID
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}
	/**
	 * 设置中心工作ID
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	/**
	 * 获取中心工作标题
	 * @return
	 */
	public String getCenterTitle() {
		return centerTitle;
	}
	/**
	 * 设置中心工作标题
	 * @param centerTitle
	 */
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
	/**
	 * 获取工作ID
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}
	/**
	 * 设置工作ID
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	/**
	 * 获取工作标题
	 * @return
	 */
	public String getWorkTitle() {
		return workTitle;
	}
	/**
	 * 设置工作标题
	 * @param workTitle
	 */
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}
	public String getTargetIdentity() {
		return targetIdentity;
	}
	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}
	public String getSenderName() {
		return senderName;
	}
	
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderIdentity() {
		return senderIdentity;
	}
	public void setSenderIdentity(String senderIdentity) {
		this.senderIdentity = senderIdentity;
	}
	
}