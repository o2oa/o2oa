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
 * 领导秘书配置信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrConfigSecretary.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrConfigSecretary extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrConfigSecretary.table;

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
	
	@EntityFieldDescribe( "秘书姓名" )
	@Column(name="xsecretaryName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String secretaryName = "";
	
	@EntityFieldDescribe( "秘书身份名称" )
	@Column(name="xsecretaryIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String secretaryIdentity = "";
	
	@EntityFieldDescribe( "秘书所属组织" )
	@Column(name="xsecretaryOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String secretaryOrganizationName = "";
	
	@EntityFieldDescribe( "秘书所属公司" )
	@Column(name="xsecretaryCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String secretaryCompanyName = "";
	
	@EntityFieldDescribe( "领导姓名" )
	@Column(name="xleaderName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String leaderName = "";
	
	@EntityFieldDescribe( "领导身份名称" )
	@Column(name="xleaderIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String leaderIdentity = "";
	
	@EntityFieldDescribe( "领导所属组织" )
	@Column(name="xleaderOrganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String leaderOrganizationName = "";
	
	@EntityFieldDescribe( "领导所属公司" )
	@Column(name="xleaderCompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String leaderCompanyName = "";
	
	@EntityFieldDescribe( "备注说明" )
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true)
	private String description = null;

	/**
	 * 获取秘书姓名
	 * @return
	 */
	public String getSecretaryName() {
		return secretaryName;
	}
	/**
	 * 设置秘书姓名
	 * @param secretaryName
	 */
	public void setSecretaryName(String secretaryName) {
		this.secretaryName = secretaryName;
	}
	/**
	 * 获取秘书所属组织名称
	 * @return
	 */
	public String getSecretaryOrganizationName() {
		return secretaryOrganizationName;
	}
	/**
	 * 设置秘书所属组织名称
	 * @param secretaryOrganizationName
	 */
	public void setSecretaryOrganizationName(String secretaryOrganizationName) {
		this.secretaryOrganizationName = secretaryOrganizationName;
	}
	/**
	 * 获取秘书所属公司名称
	 * @return
	 */
	public String getSecretaryCompanyName() {
		return secretaryCompanyName;
	}
	/**
	 * 设置秘书所属公司名称
	 * @param secretaryCompanyName
	 */
	public void setSecretaryCompanyName(String secretaryCompanyName) {
		this.secretaryCompanyName = secretaryCompanyName;
	}
	/**
	 * 获取领导姓名
	 * @return
	 */
	public String getLeaderName() {
		return leaderName;
	}
	/**
	 * 设置领导姓名
	 * @param leaderName
	 */
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	/**
	 * 获取领导所属组织名称
	 * @return
	 */
	public String getLeaderOrganizationName() {
		return leaderOrganizationName;
	}
	/**
	 * 设置领导所属组织名称
	 * @param leaderOrganizationName
	 */
	public void setLeaderOrganizationName(String leaderOrganizationName) {
		this.leaderOrganizationName = leaderOrganizationName;
	}
	/**
	 * 获取领导所属公司名称
	 * @return
	 */
	public String getLeaderCompanyName() {
		return leaderCompanyName;
	}
	/**
	 * 设置领导所属公司名称
	 * @param leaderCompanyName
	 */
	public void setLeaderCompanyName(String leaderCompanyName) {
		this.leaderCompanyName = leaderCompanyName;
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
	 * 获取领导的身份
	 * @return
	 */
	public String getLeaderIdentity() {
		return leaderIdentity;
	}
	/**
	 * 设置领导的身份
	 * @param leaderIdentity
	 */
	public void setLeaderIdentity(String leaderIdentity) {
		this.leaderIdentity = leaderIdentity;
	}
	public String getSecretaryIdentity() {
		return secretaryIdentity;
	}
	public void setSecretaryIdentity(String secretaryIdentity) {
		this.secretaryIdentity = secretaryIdentity;
	}
	
	
}