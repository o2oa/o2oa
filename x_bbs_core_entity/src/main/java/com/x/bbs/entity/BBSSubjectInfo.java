package com.x.bbs.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 主题信息表
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.BBSSubjectInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class BBSSubjectInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSSubjectInfo.table;

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
	
	@EntityFieldDescribe( "论坛名称" )
	@Column(name="xforumName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String forumName = "";
	
	@EntityFieldDescribe( "版块ID" )
	@Column(name="xsectionId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String sectionId = "";
	
	@EntityFieldDescribe( "版块名称" )
	@Column(name="xsectionName", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String sectionName = "";
	
	@EntityFieldDescribe( "主版块ID" )
	@Column(name="xmainSectionId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String mainSectionId = "";
	
	@EntityFieldDescribe( "主版块名称" )
	@Column(name="xmainSectionName", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String mainSectionName = "";
	
	@EntityFieldDescribe( "首页图片ID" )
	@Column(name="xpicId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String picId = "";
	
	@EntityFieldDescribe( "主题名称：标题" )
	@Column(name="xtitle", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String title = "";
	
	@EntityFieldDescribe( "主题类别：讨论，新闻等等,根据版块设置" )
	@Column(name="xtype", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String type = "新闻";
	
	@EntityFieldDescribe( "主题的类别,不同的类别有不同的操作:信息|问题|投票" )
	@Column(name="xtypeCategory", length = JpaObject.length_64B )
	private String typeCategory = "信息";
	
	@EntityFieldDescribe( "主题摘要" )
	@Column(name="xsummary", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String summary = "";
	
	@EntityFieldDescribe( "最新回复时间" )
	@Column(name="xlatestReplyTime" )
	@CheckPersist( allowEmpty = true )
	private Date latestReplyTime = null;
	
	@EntityFieldDescribe( "最新回复用户" )
	@Column(name="xlatestReplyUser", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String latestReplyUser = "";
	
	@EntityFieldDescribe( "最新回复ID" )
	@Column(name="xlatestReplyId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String latestReplyId = "";
	
	@EntityFieldDescribe( "回复数量" )
	@Column(name="xreplyTotal" )
	private Long replyTotal = 0L;
	
	@EntityFieldDescribe( "查看数量" )
	@Column(name="xviewTotal" )
	private Long viewTotal = 0L;
	
	@EntityFieldDescribe( "主题热度" )
	@Column(name="xhot" )
	private Long hot = 0L;
	
	@EntityFieldDescribe( "禁止回贴" )
	@Column(name="xstopReply" )
	private Boolean stopReply = false;
	
	@EntityFieldDescribe( "推荐到系统首页" )
	@Column(name="xrecommendToBBSIndex" )
	private Boolean recommendToBBSIndex = false;
	
	@EntityFieldDescribe( "首页推荐人姓名" )
	@Column(name="xbBSIndexSetterName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String bBSIndexSetterName = "";
	
	@EntityFieldDescribe( "首页推荐时间" )
	@Column(name="xbBSIndexSetterTime" )
	@CheckPersist( allowEmpty = true )
	private Date bBSIndexSetterTime = null;
	
	@EntityFieldDescribe( "推荐到论坛首页" )
	@Column(name="xrecommendToForumIndex" )
	private Boolean recommendToForumIndex = false;
	
	@EntityFieldDescribe( "论坛推荐人姓名" )
	@Column(name="xforumIndexSetterName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String forumIndexSetterName = "";
	
	@EntityFieldDescribe( "论坛推荐时间" )
	@Column(name="xforumIndexSetterTime" )
	@CheckPersist( allowEmpty = true )
	private Date forumIndexSetterTime = null;
	
	@EntityFieldDescribe( "版块置顶" )
	@Column(name="xtopToSection" )
	private Boolean topToSection = false;
	
	@EntityFieldDescribe( "主版块置顶" )
	@Column(name="xtopToMainSection" )
	private Boolean topToMainSection = false;
	
	@EntityFieldDescribe( "论坛置顶" )
	@Column(name="xtopToForum" )
	private Boolean topToForum = false;
	
	@EntityFieldDescribe( "全局置顶" )
	@Column(name="xtopToBBS" )
	private Boolean topToBBS = false;
	
	@EntityFieldDescribe( "是否为置顶主题" )
	@Column(name="xisTopSubject" )
	private Boolean isTopSubject = false;
	
	@EntityFieldDescribe( "精华主题" )
	@Column( name="xisCreamSubject" )
	private Boolean isCreamSubject = false;
	
	@EntityFieldDescribe( "是否已解决:为问题贴准备" )
	@Column( name="xisCompleted" )
	private Boolean isCompleted = false;
	
	@EntityFieldDescribe( "采纳的回复ID:为问题贴准备,问题贴可以采纳一个回复" )
	@Column( name="xacceptReplyId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true )
	private String acceptReplyId = "";
	
	@EntityFieldDescribe( "精华设置人姓名" )
	@Column( name="xscreamSetterName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String screamSetterName = "";
	
	@EntityFieldDescribe( "精华设置时间" )
	@Column( name="xscreamSetterTime" )
	@CheckPersist( allowEmpty = true )
	private Date screamSetterTime = null;
	
	@EntityFieldDescribe( "原创主题" )
	@Column( name="xisOriginalSubject" )
	private Boolean isOriginalSubject = false;
	
	@EntityFieldDescribe( "原创设置人姓名" )
	@Column(name="xoriginalSetterName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String originalSetterName = "";
	
	@EntityFieldDescribe( "原创设置时间" )
	@Column(name="xoriginalSetterTime" )
	@CheckPersist( allowEmpty = true )
	private Date originalSetterTime = null;
	
	@EntityFieldDescribe( "版主推荐主题" )
	@Column(name="xisRecommendSubject" )
	private Boolean isRecommendSubject = false;
	
	@EntityFieldDescribe( "推荐人姓名" )
	@Column(name="xrecommendorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String recommendorName = "";
	
	@EntityFieldDescribe( "推荐时间" )
	@Column(name="xrecommendTime" )
	@CheckPersist( allowEmpty = true )
	private Date recommendTime = null;
	
	@EntityFieldDescribe( "创建人姓名" )
	@Column(name="xcreatorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String creatorName = "";
	
	@EntityFieldDescribe( "主题审核状态：无审核|待审核|审核通过" )
	@Column(name="xsubjectAuditStatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String subjectAuditStatus = "无审核";
	
	@EntityFieldDescribe( "审核人姓名" )
	@Column(name="xauditorName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String auditorName = "";
	
	@EntityFieldDescribe( "主题状态：启用|关闭|锁定    锁定后是不允许任何人修改和回复的" )
	@Column(name="xsubjectStatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String subjectStatus = "启用";
	
	@EntityFieldDescribe( "排序号" )
	@Column(name="xorderNumber" )
	private Integer orderNumber = 1;
	
	@EntityFieldDescribe( "附件列表" )
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn )
	@ContainerTable(name = TABLE + "_attachmentList", joinIndex = @Index(name = TABLE + "_attachmentList_join" ) )
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_attachmentList_element" )
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;
	
	@EntityFieldDescribe( "设备类别：手机|平板电脑|个人电脑等" )
	@Column(name="xmachineName", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String machineName = "PC";
	
	@EntityFieldDescribe( "系统名称" )
	@Column(name="xsystemType", length = JpaObject.length_64B )
	@CheckPersist( allowEmpty = true )
	private String systemType = "Windows";
	
	@EntityFieldDescribe( "IP地址" )
	@Column(name="xhostIp", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String hostIp = "";

	public String getForumName() {
		return forumName;
	}
	public void setForumName(String forumName) {
		this.forumName = forumName;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
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
	public String getTitle() {
		return title == null ? null : title.trim();
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Date getLatestReplyTime() {
		return latestReplyTime;
	}
	public void setLatestReplyTime(Date latestReplyTime) {
		this.latestReplyTime = latestReplyTime;
	}
	public String getLatestReplyUser() {
		return latestReplyUser;
	}
	public void setLatestReplyUser(String latestReplyUser) {
		this.latestReplyUser = latestReplyUser;
	}
	public String getLatestReplyId() {
		return latestReplyId;
	}
	public void setLatestReplyId(String latestReplyId) {
		this.latestReplyId = latestReplyId;
	}
	public Long getReplyTotal() {
		return replyTotal;
	}
	public void setReplyTotal(Long replyTotal) {
		this.replyTotal = replyTotal;
	}
	public Long getViewTotal() {
		return viewTotal;
	}
	public void setViewTotal(Long viewTotal) {
		this.viewTotal = viewTotal;
	}
	public Long getHot() {
		return hot;
	}
	public void setHot(Long hot) {
		this.hot = hot;
	}
	public Boolean getStopReply() {
		return stopReply;
	}
	public void setStopReply(Boolean stopReply) {
		this.stopReply = stopReply;
	}
	public Boolean getRecommendToBBSIndex() {
		return recommendToBBSIndex;
	}
	public void setRecommendToBBSIndex(Boolean recommendToBBSIndex) {
		this.recommendToBBSIndex = recommendToBBSIndex;
	}
	public Boolean getRecommendToForumIndex() {
		return recommendToForumIndex;
	}
	public void setRecommendToForumIndex(Boolean recommendToForumIndex) {
		this.recommendToForumIndex = recommendToForumIndex;
	}
	public Boolean getTopToSection() {
		return topToSection;
	}
	public void setTopToSection(Boolean topToSection) {
		this.topToSection = topToSection;
	}
	public Boolean getTopToForum() {
		return topToForum;
	}
	public void setTopToForum(Boolean topToForum) {
		this.topToForum = topToForum;
	}
	public Boolean getTopToBBS() {
		return topToBBS;
	}
	public void setTopToBBS(Boolean topToBBS) {
		this.topToBBS = topToBBS;
	}
	public Boolean getIsCreamSubject() {
		return isCreamSubject;
	}
	public void setIsCreamSubject(Boolean isCreamSubject) {
		this.isCreamSubject = isCreamSubject;
	}
	public Boolean getIsOriginalSubject() {
		return isOriginalSubject;
	}
	public void setIsOriginalSubject(Boolean isOriginalSubject) {
		this.isOriginalSubject = isOriginalSubject;
	}
	public Boolean getIsRecommendSubject() {
		return isRecommendSubject;
	}
	public void setIsRecommendSubject(Boolean isRecommendSubject) {
		this.isRecommendSubject = isRecommendSubject;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getSubjectAuditStatus() {
		return subjectAuditStatus;
	}
	public void setSubjectAuditStatus(String subjectAuditStatus) {
		this.subjectAuditStatus = subjectAuditStatus;
	}
	public String getSubjectStatus() {
		return subjectStatus;
	}
	public void setSubjectStatus(String subjectStatus) {
		this.subjectStatus = subjectStatus;
	}
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getMainSectionId() {
		return mainSectionId;
	}
	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}
	public String getMainSectionName() {
		return mainSectionName;
	}
	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}
	public String getbBSIndexSetterName() {
		return bBSIndexSetterName;
	}
	public void setbBSIndexSetterName(String bBSIndexSetterName) {
		this.bBSIndexSetterName = bBSIndexSetterName;
	}
	public Date getbBSIndexSetterTime() {
		return bBSIndexSetterTime;
	}
	public void setbBSIndexSetterTime(Date bBSIndexSetterTime) {
		this.bBSIndexSetterTime = bBSIndexSetterTime;
	}
	public String getForumIndexSetterName() {
		return forumIndexSetterName;
	}
	public void setForumIndexSetterName(String forumIndexSetterName) {
		this.forumIndexSetterName = forumIndexSetterName;
	}
	public Date getForumIndexSetterTime() {
		return forumIndexSetterTime;
	}
	public void setForumIndexSetterTime(Date forumIndexSetterTime) {
		this.forumIndexSetterTime = forumIndexSetterTime;
	}
	public String getScreamSetterName() {
		return screamSetterName;
	}
	public void setScreamSetterName(String screamSetterName) {
		this.screamSetterName = screamSetterName;
	}
	public Date getScreamSetterTime() {
		return screamSetterTime;
	}
	public void setScreamSetterTime(Date screamSetterTime) {
		this.screamSetterTime = screamSetterTime;
	}
	public String getOriginalSetterName() {
		return originalSetterName;
	}
	public void setOriginalSetterName(String originalSetterName) {
		this.originalSetterName = originalSetterName;
	}
	public Date getOriginalSetterTime() {
		return originalSetterTime;
	}
	public void setOriginalSetterTime(Date originalSetterTime) {
		this.originalSetterTime = originalSetterTime;
	}
	public String getRecommendorName() {
		return recommendorName;
	}
	public void setRecommendorName(String recommendorName) {
		this.recommendorName = recommendorName;
	}
	public Date getRecommendTime() {
		return recommendTime;
	}
	public void setRecommendTime(Date recommendTime) {
		this.recommendTime = recommendTime;
	}
	public String getAuditorName() {
		return auditorName;
	}
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	public Boolean getTopToMainSection() {
		return topToMainSection;
	}
	public void setTopToMainSection(Boolean topToMainSection) {
		this.topToMainSection = topToMainSection;
	}
	public List<String> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public Boolean getIsTopSubject() {
		return isTopSubject;
	}
	public void setIsTopSubject(Boolean isTopSubject) {
		this.isTopSubject = isTopSubject;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getSystemType() {
		return systemType;
	}
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public Boolean getIsCompleted() {
		return isCompleted;
	}
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	public String getTypeCategory() {
		return typeCategory;
	}
	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}
	public String getAcceptReplyId() {
		return acceptReplyId;
	}
	public void setAcceptReplyId(String acceptReplyId) {
		this.acceptReplyId = acceptReplyId;
	}
	public String getPicId() {
		return picId;
	}
	public void setPicId(String picId) {
		this.picId = picId;
	}
	
}