package com.x.okr.entity;

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
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 工作处理链信息，记录工作的处理人信息以及步骤信息，流程按处理人链一步一步执行
 * 形成一个简易的流程控制过程
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkProcessLink.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkProcessLink extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkProcessLink.table;

	/**
	 * 获取明细记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置明细记录ID
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
	public void prePersist() {
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
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe( "工作标题" )
	@Column(name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String title = "";
	
	@EntityFieldDescribe( "中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = false)
	private String centerId = "";
	
	@EntityFieldDescribe( "中心工作标题" )
	@Column(name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String centerTitle = "";
	
	@EntityFieldDescribe( "工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String workId = "";

	@EntityFieldDescribe( "处理人姓名" )
	@Column(name="xprocessorName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_processorName" )
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processorName = "";
	
	@EntityFieldDescribe( "处理人身份" )
	@Column(name="xprocessorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_processorIdentity" )
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processorIdentity = "";
	
	@EntityFieldDescribe( "处理人所属组织" )
	@Column(name="xprocessorOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processorOrganizationName = "";
	
	@EntityFieldDescribe( "处理人所属公司" )
	@Column(name="xprocessorCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String processorCompanyName = "";
	
	@EntityFieldDescribe( "处理层级次序" )
	@Column(name="xprocessLevel" )
	@CheckPersist( allowEmpty = true )
	private Integer processLevel = 1;	
	
	@EntityFieldDescribe( "处理环节名称" )
	@Column(name="xactivityName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String activityName = "";
	
	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = false )
	private String status = "正常";
	
	/**
	 * 获取工作标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置工作标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取所属中心工作ID
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}
	/**
	 * 设置所属中心工作ID
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
	 * 获取处理人姓名
	 * @return
	 */
	public String getProcessorName() {
		return processorName;
	}
	/**
	 * 设置处理人姓名
	 * @param processorName
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}
	/**
	 * 获取处理人所属组织名称
	 * @return
	 */
	public String getProcessorOrganizationName() {
		return processorOrganizationName;
	}
	/**
	 * 设置处理人所属组织名称
	 * @param processorOrganizationName
	 */
	public void setProcessorOrganizationName(String processorOrganizationName) {
		this.processorOrganizationName = processorOrganizationName;
	}
	/**
	 * 获取处理人所属公司名称
	 * @return
	 */
	public String getProcessorCompanyName() {
		return processorCompanyName;
	}
	/**
	 * 设置处理人所属公司名称
	 * @param processorCompanyName
	 */
	public void setProcessorCompanyName(String processorCompanyName) {
		this.processorCompanyName = processorCompanyName;
	}
	/**
	 * 获取处理层级次序
	 * @return
	 */
	public Integer getProcessLevel() {
		return processLevel;
	}
	/**
	 * 设置处理层级次序
	 * @param processLevel
	 */
	public void setProcessLevel(Integer processLevel) {
		this.processLevel = processLevel;
	}
	/**
	 * 获取处理环节名称
	 * @return
	 */
	public String getActivityName() {
		return activityName;
	}
	/**
	 * 设置处理环节名称
	 * @param activityName
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public OkrWorkProcessLink(){}
	
	/**
	 * 
	 * @param title   工作标题
	 * @param centerId  中心工作ID
	 * @param centerTitle  中心工作标题
	 * @param workId   工作ID
	 * @param processorName   处理者姓名
	 * @param processorOrganizationName   处理者所在组织名称
	 * @param processorCompanyName  处理者所在公司名称
	 * @param processLevel   处理层级
	 * @param activityName   处理环节名称
	 */
	public OkrWorkProcessLink(String title, String centerId, String centerTitle, String workId, String processorName,
			String processIdentityName, String processorOrganizationName, String processorCompanyName, Integer processLevel, String activityName) {
		super();
		this.title = title;
		this.centerId = centerId;
		this.centerTitle = centerTitle;
		this.workId = workId;
		this.processorName = processorName;
		this.processorOrganizationName = processorOrganizationName;
		this.processorCompanyName = processorCompanyName;
		this.processLevel = processLevel;
		this.activityName = activityName;
		this.processorIdentity = processIdentityName;
	}
	/**
	 * 获取信息状态：正常|已删除
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * 设置信息状态：正常|已删除
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
}