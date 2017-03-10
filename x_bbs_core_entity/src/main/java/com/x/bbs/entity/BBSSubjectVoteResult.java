package com.x.bbs.entity;

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
 * 主题投票结果表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSSubjectVoteResult.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSSubjectVoteResult extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSSubjectVoteResult.table;

	@EntityFieldDescribe( "ID, 和主题ID相同,一个主题一个投票统计结果." )
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
	@EntityFieldDescribe( "论坛ID" )
	@Column(name="xforumId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String forumId = "";
	
	@EntityFieldDescribe( "版块ID" )
	@Column(name="xsectionId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String sectionId = "";
	
	@EntityFieldDescribe( "主版块ID" )
	@Column(name="xmainSectionId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String mainSectionId = "";
		
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "统计内容" )
	@Column(name="xstatisticContent", length = JpaObject.length_1M )
	@CheckPersist( allowEmpty = true )
	private String statisticContent = "";

	public String getForumId() {
		return forumId;
	}
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getMainSectionId() {
		return mainSectionId;
	}
	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}
	public String getStatisticContent() {
		return statisticContent;
	}
	public void setStatisticContent(String statisticContent) {
		this.statisticContent = statisticContent;
	}	
}