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
 * 工作委托记录信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkAuthorizeRecord.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkAuthorizeRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkAuthorizeRecord.table;

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
	@EntityFieldDescribe( "工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String workId = "";
	
	@EntityFieldDescribe( "工作标题" )
	@Column(name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String title = "";
	
	@EntityFieldDescribe( "中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String centerId = "";
	
	@EntityFieldDescribe( "中心工作标题" )
	@Column(name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String centerTitle = "";

	@EntityFieldDescribe( "委托者姓名" )
	@Column(name="xdelegatorName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String delegatorName = "";
	
	@EntityFieldDescribe( "委托者身份" )
	@Column(name="xdelegatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String delegatorIdentity = "";
	
	@EntityFieldDescribe( "委托者所属组织" )
	@Column(name="xdelegatorOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String delegatorOrganizationName = "";
	
	@EntityFieldDescribe( "委托者所属公司" )
	@Column(name="xdelegatorCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String delegatorCompanyName = "";
	
	@EntityFieldDescribe( "委托层级" )
	@Column(name="xdelegateLevel" )
	@CheckPersist( allowEmpty = true )
	private Integer delegateLevel = 1;
	
	@EntityFieldDescribe( "受托者姓名" )
	@Column(name="xtargetName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String targetName = "";
	
	@EntityFieldDescribe( "受托者身份" )
	@Column(name="xtargetIdentity", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = true)
	private String targetIdentity = null;
	
	@EntityFieldDescribe( "受托者所属组织" )
	@Column(name="xtargetOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String targetOrganizationName = "";
	
	@EntityFieldDescribe( "受托者所属公司" )
	@Column(name="xtargetCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String targetCompanyName = "";
	
	@EntityFieldDescribe( "委托时间：yyyy-mm-dd hh:mi:ss" )
	@Column(name="xdelegateDateTimeStr", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String delegateDateTimeStr = "";
	
	@EntityFieldDescribe( "委托时间" )
	@Column(name="xdelegateDateTime" )
	@CheckPersist( allowEmpty = true )
	private Date delegateDateTime = null;
	
	@EntityFieldDescribe( "收回时间" )
	@Column(name="xtakebackDateTime" )
	@CheckPersist( allowEmpty = true )
	private Date takebackDateTime = null;
	
	@EntityFieldDescribe( "委托意见" )
	@Column(name="xdelegateOpinion", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true )
	private String delegateOpinion = "";
	
	@EntityFieldDescribe( "信息状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
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
	 * @param parentWorkId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	/**
	 * 获取委托者姓名
	 * @return
	 */
	public String getDelegatorName() {
		return delegatorName;
	}
	/**
	 * 设置委托者姓名
	 * @param delegatorName
	 */
	public void setDelegatorName(String delegatorName) {
		this.delegatorName = delegatorName;
	}
	/**
	 * 获取委托者所属组织名称
	 * @return
	 */
	public String getDelegatorOrganizationName() {
		return delegatorOrganizationName;
	}
	/**
	 * 设置委托者所属组织名称
	 * @param delegatorOrganizationName
	 */
	public void setDelegatorOrganizationName(String delegatorOrganizationName) {
		this.delegatorOrganizationName = delegatorOrganizationName;
	}
	/**
	 * 获取委托者所属公司名称
	 * @return
	 */
	public String getDelegatorCompanyName() {
		return delegatorCompanyName;
	}
	/**
	 * 设置委托者所属公司名称
	 * @param delegatorCompanyName
	 */
	public void setDelegatorCompanyName(String delegatorCompanyName) {
		this.delegatorCompanyName = delegatorCompanyName;
	}
	/**
	 * 获取受托者姓名
	 * @return
	 */
	public String getTargetName() {
		return targetName;
	}
	/**
	 * 设置受托者姓名
	 * @param targetName
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	/**
	 * 获取受托者所属组织名称
	 * @return
	 */
	public String getTargetOrganizationName() {
		return targetOrganizationName;
	}
	/**
	 * 设置受托者所属组织名称
	 * @param targetOrganizationName
	 */
	public void setTargetOrganizationName(String targetOrganizationName) {
		this.targetOrganizationName = targetOrganizationName;
	}
	/**
	 * 获取受托者所属公司名称
	 * @return
	 */
	public String getTargetCompanyName() {
		return targetCompanyName;
	}
	/**
	 * 设置受托者所属公司名称
	 * @param targetCompanyName
	 */
	public void setTargetCompanyName(String targetCompanyName) {
		this.targetCompanyName = targetCompanyName;
	}
	/**
	 * 获取委托时间
	 * @return
	 */
	public String getDelegateDateTimeStr() {
		return delegateDateTimeStr;
	}
	/**
	 * 设置委托时间
	 * @param delegateDateTimeStr
	 */
	public void setDelegateDateTimeStr(String delegateDateTimeStr) {
		this.delegateDateTimeStr = delegateDateTimeStr;
	}
	/**
	 * 获取委托时间
	 * @return
	 */
	public Date getDelegateDateTime() {
		return delegateDateTime;
	}
	/**
	 * 设置委托时间
	 * @param delegateDateTime
	 */
	public void setDelegateDateTime(Date delegateDateTime) {
		this.delegateDateTime = delegateDateTime;
	}
	/**
	 * 获取委托意见
	 * @return
	 */
	public String getDelegateOpinion() {
		return delegateOpinion;
	}
	/**
	 * 设置委托意见
	 * @param delegateOpinion
	 */
	public void setDelegateOpinion(String delegateOpinion) {
		this.delegateOpinion = delegateOpinion;
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
	/**
	 * 获取委托者身份
	 * @return
	 */
	public String getDelegatorIdentity() {
		return delegatorIdentity;
	}
	/**
	 * 设置委托者身份
	 * @param delegatorIdentity
	 */
	public void setDelegatorIdentity(String delegatorIdentity) {
		this.delegatorIdentity = delegatorIdentity;
	}
	
	/**
	 * 获取目标用户身份
	 * @return
	 */
	public String getTargetIdentity() {
		return targetIdentity;
	}
	/**
	 * 设置目标用户身份
	 * @param targetIdentity
	 */
	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}
	public Integer getDelegateLevel() {
		return delegateLevel;
	}
	public void setDelegateLevel(Integer delegateLevel) {
		this.delegateLevel = delegateLevel;
	}
	public Date getTakebackDateTime() {
		return takebackDateTime;
	}
	public void setTakebackDateTime(Date takebackDateTime) {
		this.takebackDateTime = takebackDateTime;
	}
}