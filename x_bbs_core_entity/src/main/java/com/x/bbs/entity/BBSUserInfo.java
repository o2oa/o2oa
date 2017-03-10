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
 * 论坛用户信息表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSUserInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSUserInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSUserInfo.table;

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
	
	@EntityFieldDescribe( "用户姓名" )
	@Column(name="xuserName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String userName = "";
	
	@EntityFieldDescribe( "昵称" )
	@Column(name="xnickName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String nickName = "";

	@EntityFieldDescribe( "性别:0-女 1-男" )
	@Column(name="xsex" )
	@CheckPersist( allowEmpty = true )
	private Integer sex = 0;
	
	@EntityFieldDescribe( "身份证号" )
	@Column(name="xcardId", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String cardId = "";
	
	@EntityFieldDescribe( "移动电话" )
	@Column(name="xmobile", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String mobile = "";
	
	@EntityFieldDescribe( "积分" )
	@Column(name="xcredit" )
	@CheckPersist( allowEmpty = true )
	private Long credit = 0L;	
	
	@EntityFieldDescribe( "主题数" )
	@Column(name="xsubjectCount" )
	@CheckPersist( allowEmpty = true )
	private Long subjectCount = 0L;
	
	@EntityFieldDescribe( "今日发表主题数" )
	@Column(name="xsubjectCountToday" )
	@CheckPersist( allowEmpty = true )
	private Long subjectCountToday = 0L;
	
	@EntityFieldDescribe( "回复数" )
	@Column(name="xreplyCount" )
	@CheckPersist( allowEmpty = true )
	private Long replyCount = 0L;
	
	@EntityFieldDescribe( "今日发表回复数" )
	@Column(name="xreplyCountToday" )
	@CheckPersist( allowEmpty = true )
	private Long replyCountToday = 0L;
	
	@EntityFieldDescribe( "精华贴数" )
	@Column(name="xcreamCount" )
	@CheckPersist( allowEmpty = true )
	private Long creamCount = 0L;
	
	@EntityFieldDescribe( "原创贴数" )
	@Column(name="xoriginalCount" )
	@CheckPersist( allowEmpty = true )
	private Long originalCount = 0L;
	
	@EntityFieldDescribe( "人气" )
	@Column(name="xpopularity" )
	@CheckPersist( allowEmpty = true )
	private Long popularity = 0L;
	
	@EntityFieldDescribe( "粉丝数" )
	@Column(name="xfansCount" )
	@CheckPersist( allowEmpty = true )
	private Long fansCount = 0L;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "签名内容" )
	@Column(name="xsignContent", length = JpaObject.length_4K )
	@CheckPersist( allowEmpty = true )
	private String signContent = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "权限角色内容" )
	@Column(name="xpermissionContent", length = JpaObject.length_1M )
	@CheckPersist( allowEmpty = true )
	private String permissionContent = "";
	
	@EntityFieldDescribe( "在线状态" )
	@Column(name="xonline" )
	@CheckPersist( allowEmpty = true )
	private Boolean online = false;
	
	@EntityFieldDescribe( "上次访问时间" )
	@Column(name="xlastVisitTime" )
	@CheckPersist( allowEmpty = true )
	private Date lastVisitTime = null;
	
	@EntityFieldDescribe( "最近一次操作时间" )
	@Column(name="xlastOperationTime" )
	@CheckPersist( allowEmpty = true )
	private Date lastOperationTime = null;	
	
	@EntityFieldDescribe( "用户状态：启用|停用" )
	@Column(name="xuserStatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String userStatus = "启用";
	
	@EntityFieldDescribe( "排序号" )
	@Column(name="xorderNumber" )
	private Integer orderNumber = 1;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}
	public Long getCredit() {
		return credit;
	}
	public void setCredit(Long credit) {
		this.credit = credit;
	}
	public Long getSubjectCount() {
		return subjectCount;
	}
	public void setSubjectCount(Long subjectCount) {
		this.subjectCount = subjectCount;
	}
	public Long getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(Long replyCount) {
		this.replyCount = replyCount;
	}
	public Long getCreamCount() {
		return creamCount;
	}
	public void setCreamCount(Long creamCount) {
		this.creamCount = creamCount;
	}
	public Long getOriginalCount() {
		return originalCount;
	}
	public void setOriginalCount(Long originalCount) {
		this.originalCount = originalCount;
	}
	public Long getPopularity() {
		return popularity;
	}
	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}
	public Long getFansCount() {
		return fansCount;
	}
	public void setFansCount(Long fansCount) {
		this.fansCount = fansCount;
	}
	public String getSignContent() {
		return signContent;
	}
	public void setSignContent(String signContent) {
		this.signContent = signContent;
	}
	public Boolean getOnline() {
		return online;
	}
	public void setOnline(Boolean online) {
		this.online = online;
	}
	public Date getLastVisitTime() {
		return lastVisitTime;
	}
	public void setLastVisitTime(Date lastVisitTime) {
		this.lastVisitTime = lastVisitTime;
	}
	public Date getLastOperationTime() {
		return lastOperationTime;
	}
	public void setLastOperationTime(Date lastOperationTime) {
		this.lastOperationTime = lastOperationTime;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getPermissionContent() {
		return permissionContent;
	}
	public void setPermissionContent(String permissionContent) {
		this.permissionContent = permissionContent;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public Long getSubjectCountToday() {
		return subjectCountToday;
	}
	public void setSubjectCountToday(Long subjectCountToday) {
		this.subjectCountToday = subjectCountToday;
	}
	public Long getReplyCountToday() {
		return replyCountToday;
	}
	public void setReplyCountToday(Long replyCountToday) {
		this.replyCountToday = replyCountToday;
	}
}