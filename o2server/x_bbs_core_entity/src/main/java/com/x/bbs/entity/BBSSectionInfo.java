package com.x.bbs.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.OrderColumn;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 版块信息表
 *
 * @author LIYI
 */
@Schema(name = "BBSSectionInfo", description = "论坛版块信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSSectionInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSSectionInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSSectionInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSSectionInfo.table;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	@Override
	public void onPersist() throws Exception {
	}

	public static final String sectionName_FIELDNAME = "sectionName";
	@FieldDescribe("版块名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sectionName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionName = "";

	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("论坛ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumId = "";

	public static final String forumName_FIELDNAME = "forumName";
	@FieldDescribe("论坛名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + forumName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumName = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("主版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mainSectionId = "";

	public static final String mainSectionName_FIELDNAME = "mainSectionName";
	@FieldDescribe("主版块名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + mainSectionName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mainSectionName = "";

	public static final String sectionLevel_FIELDNAME = "sectionLevel";
	@FieldDescribe("版块级别：主版块|子版块")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + sectionLevel_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionLevel_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionLevel = "主版块";

	public static final String sectionDescription_FIELDNAME = "sectionDescription";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("版块简介")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + sectionDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionDescription = "";

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String sectionNotice_FIELDNAME = "sectionNotice";
	@Lob
	@FieldDescribe("版块公告")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + sectionNotice_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionNotice = "";

	public static final String sectionVisible_FIELDNAME = "sectionVisible";
	@FieldDescribe("版块可见：所有人（默认）|根据权限")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + sectionVisible_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionVisible_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionVisible = "所有人";

	public static final String visiblePermissionList_FIELDNAME = "visiblePermissionList";
	@FieldDescribe("版块可见范围")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ visiblePermissionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ visiblePermissionList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ visiblePermissionList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + visiblePermissionList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> visiblePermissionList;

	public static final String subjectPublishAble_FIELDNAME = "subjectPublishAble";
	@FieldDescribe("版块发贴权限：所有人（默认）|根据权限")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + subjectPublishAble_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectPublishAble = "所有人";

	public static final String publishPermissionList_FIELDNAME = "publishPermissionList";
	@FieldDescribe("版块可发表范围")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishPermissionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishPermissionList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishPermissionList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishPermissionList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishPermissionList;

	public static final String replyPublishAble_FIELDNAME = "replyPublishAble";
	@FieldDescribe("版块回复权限：所有人（默认）|根据权限")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + replyPublishAble_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String replyPublishAble = "所有人";

	public static final String replyPermissionList_FIELDNAME = "replyPermissionList";
	@FieldDescribe("版块可回复范围")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ replyPermissionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ replyPermissionList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + replyPermissionList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + replyPermissionList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> replyPermissionList;

	public static final String moderatorNames_FIELDNAME = "moderatorNames";
	@FieldDescribe("版主姓名：可多值，默认为创建者")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ moderatorNames_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ moderatorNames_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + moderatorNames_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + moderatorNames_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> moderatorNames;

	public static final String subjectTypeList_FIELDNAME = "subjectTypeList";
	@FieldDescribe("论坛版块-应用类型（从应用市场同步）")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ subjectTypeList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ subjectTypeList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + subjectTypeList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + subjectTypeList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> subjectTypeList;

	public static final String sectionType_FIELDNAME = "sectionType";
	@FieldDescribe("版块类别：图片新闻，普通新闻，公告，经典（默认）")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + sectionType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionType = "经典";

	public static final String subjectType_FIELDNAME = "subjectType";
	@FieldDescribe("论坛版块默认的主题分类名称,以|分隔,如讨论|新闻, 如果未填写, 则以分区配置为主")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + subjectType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectType = "讨论|新闻";

	public static final String typeCategory_FIELDNAME = "typeCategory";
	@FieldDescribe("论坛版块支持的主题类别名称,以|分隔,信息|问题|投票, 如果未填写,则以分区配置为主")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + typeCategory_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String typeCategory = "信息|问题|投票";

	public static final String indexRecommendable_FIELDNAME = "indexRecommendable";
	@FieldDescribe("允许推荐到首页：true|false")
	@Column(name = ColumnNamePrefix + indexRecommendable_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean indexRecommendable = true;

	public static final String subjectNeedAudit_FIELDNAME = "subjectNeedAudit";
	@FieldDescribe("主题需要审核：true|false")
	@Column(name = ColumnNamePrefix + subjectNeedAudit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean subjectNeedAudit = false;

	public static final String replyNeedAudit_FIELDNAME = "replyNeedAudit";
	@FieldDescribe("回复需要审核：true|false")
	@Column(name = ColumnNamePrefix + replyNeedAudit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean replyNeedAudit = false;

	public static final String subSectionCreateAble_FIELDNAME = "subSectionCreateAble";
	@FieldDescribe("允许创建子版块：true|false")
	@Column(name = ColumnNamePrefix + subSectionCreateAble_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean subSectionCreateAble = true;

	public static final String subjectTotal_FIELDNAME = "subjectTotal";
	@FieldDescribe("主题数量")
	@Column(name = ColumnNamePrefix + subjectTotal_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long subjectTotal = 0L;

	public static final String replyTotal_FIELDNAME = "replyTotal";
	@FieldDescribe("回复数量")
	@Column(name = ColumnNamePrefix + replyTotal_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long replyTotal = 0L;

	public static final String subjectTotalToday_FIELDNAME = "subjectTotalToday";
	@FieldDescribe("今日主题数量")
	@Column(name = ColumnNamePrefix + subjectTotalToday_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long subjectTotalToday = 0L;

	public static final String replyTotalToday_FIELDNAME = "replyTotalToday";
	@FieldDescribe("今日回复数量")
	@Column(name = ColumnNamePrefix + replyTotalToday_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long replyTotalToday = 0L;

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("创建人姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorName = "";

	public static final String sectionStatus_FIELDNAME = "sectionStatus";
	@FieldDescribe("版块状态：启用|停用")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + sectionStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionStatus = "启用";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public static final String replyMessageNotify_FIELDNAME = "replyMessageNotify";
	@FieldDescribe("回复消息通知：true|false")
	@Column(name = ColumnNamePrefix + replyMessageNotify_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean replyMessageNotify = false;

	public static final String replyMessageNotifyType_FIELDNAME = "replyMessageNotifyType";
	@FieldDescribe("回复消息通知类别：一共3位，第1位是否通知论坛分区管理员，第2位是否通知版主，第3位是否通知发贴人，0-不通知|1-通知")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + replyMessageNotifyType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String replyMessageNotifyType = "0,0,0";

	public static final String subjectMessageNotify_FIELDNAME = "subjectMessageNotify";
	@FieldDescribe("新主题发布消息通知：true|false")
	@Column(name = ColumnNamePrefix + subjectMessageNotify_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean subjectMessageNotify = false;

	public static final String subjectMessageNotifyType_FIELDNAME = "subjectMessageNotifyType";
	@FieldDescribe("新主题发布消息通知类别：一共2位，第1位是否通知论坛分区管理员，第2位是否通知版主，0-不通知|1-通知")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + subjectMessageNotifyType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String subjectMessageNotifyType = "0,0";

	public static final String sectionGrade_FIELDNAME = "sectionGrade";
	@FieldDescribe("版块是否支持评分：true|false")
	@Column(name = ColumnNamePrefix + sectionGrade_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean sectionGrade = false;

	public Boolean getSectionGrade() {
		return sectionGrade;
	}

	public void setSectionGrade(Boolean sectionGrade) {
		this.sectionGrade = sectionGrade;
	}

	public Boolean getSubjectMessageNotify() { return this.subjectMessageNotify == null?false:this.subjectMessageNotify; }

	public void setSubjectMessageNotify(final Boolean subjectMessageNotify) { this.subjectMessageNotify = subjectMessageNotify; }

	public String getSubjectMessageNotifyType() { return this.subjectMessageNotifyType == null?"0,0" : this.subjectMessageNotifyType; }

	public void setSubjectMessageNotifyType(final String subjectMessageNotifyType) { this.subjectMessageNotifyType = subjectMessageNotifyType; }

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

	public String getSectionLevel() {
		return sectionLevel;
	}

	public void setSectionLevel(String sectionLevel) {
		this.sectionLevel = sectionLevel;
	}

	public String getSectionDescription() {
		return sectionDescription;
	}

	public void setSectionDescription(String sectionDescription) {
		this.sectionDescription = sectionDescription;
	}

	public String getSectionNotice() {
		return sectionNotice;
	}

	public void setSectionNotice(String sectionNotice) {
		this.sectionNotice = sectionNotice;
	}

	public String getSectionVisible() {
		return sectionVisible;
	}

	public void setSectionVisible(String sectionVisible) {
		this.sectionVisible = sectionVisible;
	}

	public String getSubjectPublishAble() {
		return subjectPublishAble;
	}

	public void setSubjectPublishAble(String subjectPublishAble) {
		this.subjectPublishAble = subjectPublishAble;
	}

	public String getReplyPublishAble() {
		return replyPublishAble;
	}

	public void setReplyPublishAble(String replyPublishAble) {
		this.replyPublishAble = replyPublishAble;
	}

	public String getSectionType() {
		return sectionType;
	}

	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}

	public Boolean getIndexRecommendable() {
		return indexRecommendable;
	}

	public void setIndexRecommendable(Boolean indexRecommendable) {
		this.indexRecommendable = indexRecommendable;
	}

	public Boolean getSubjectNeedAudit() {
		return subjectNeedAudit;
	}

	public void setSubjectNeedAudit(Boolean subjectNeedAudit) {
		this.subjectNeedAudit = subjectNeedAudit;
	}

	public Boolean getReplyNeedAudit() {
		return replyNeedAudit;
	}

	public void setReplyNeedAudit(Boolean replyNeedAudit) {
		this.replyNeedAudit = replyNeedAudit;
	}

	public Boolean getSubSectionCreateAble() {
		return subSectionCreateAble;
	}

	public void setSubSectionCreateAble(Boolean subSectionCreateAble) {
		this.subSectionCreateAble = subSectionCreateAble;
	}

	public Long getSubjectTotal() {
		return subjectTotal;
	}

	public void setSubjectTotal(Long subjectTotal) {
		this.subjectTotal = subjectTotal;
	}

	public Long getReplyTotal() {
		return replyTotal;
	}

	public void setReplyTotal(Long replyTotal) {
		this.replyTotal = replyTotal;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSectionStatus() {
		return sectionStatus;
	}

	public void setSectionStatus(String sectionStatus) {
		this.sectionStatus = sectionStatus;
	}

	public Long getSubjectTotalToday() {
		return subjectTotalToday;
	}

	public void setSubjectTotalToday(Long subjectTotalToday) {
		this.subjectTotalToday = subjectTotalToday;
	}

	public Long getReplyTotalToday() {
		return replyTotalToday;
	}

	public void setReplyTotalToday(Long replyTotalToday) {
		this.replyTotalToday = replyTotalToday;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getTypeCategory() {
		return typeCategory;
	}

	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}

	public List<String> getVisiblePermissionList() {
		return visiblePermissionList;
	}

	public void setVisiblePermissionList(List<String> visiblePermissionList) { this.visiblePermissionList = visiblePermissionList; }

	public Boolean getReplyMessageNotify() { return this.replyMessageNotify == null ? false : this.replyMessageNotify; }

	public void setReplyMessageNotify(final Boolean replyMessageNotify) { this.replyMessageNotify = replyMessageNotify; }

	public String getReplyMessageNotifyType() { return this.replyMessageNotifyType == null ? "0,0,0" : this.replyMessageNotifyType; }

	public void setReplyMessageNotifyType(final String replyMessageNotifyType) { this.replyMessageNotifyType = replyMessageNotifyType; }

	public List<String> getModeratorNames() {
		return this.moderatorNames == null ? Collections.EMPTY_LIST : this.moderatorNames;
	}

	public void setModeratorNames(final List<String> moderatorNames) {
		this.moderatorNames = moderatorNames;
	}

	public List<String> getSubjectTypeList() {
		return this.subjectTypeList;
	}

	public void setSubjectTypeList(final List<String> subjectTypeList) {
		this.subjectTypeList = subjectTypeList;
	}

	public List<String> addVisitPermission(String permissoin) {
		if (this.visiblePermissionList == null) {
			this.visiblePermissionList = new ArrayList<>();
		}
		if (!this.visiblePermissionList.contains(permissoin)) {
			this.visiblePermissionList.add(permissoin);
		}
		return this.visiblePermissionList;
	}

	public List<String> getPublishPermissionList() {
		return publishPermissionList == null ? Collections.EMPTY_LIST : publishPermissionList;
	}

	public void setPublishPermissionList(List<String> publishPermissionList) {
		this.publishPermissionList = publishPermissionList;
	}

	public List<String> addPublishPermission(String permissoin) {
		if (this.publishPermissionList == null) {
			this.publishPermissionList = new ArrayList<>();
		}
		if (!this.publishPermissionList.contains(permissoin)) {
			this.publishPermissionList.add(permissoin);
		}
		return this.publishPermissionList;
	}

	public List<String> getReplyPermissionList() {
		return replyPermissionList;
	}

	public void setReplyPermissionList(List<String> replyPermissionList) {
		this.replyPermissionList = replyPermissionList;
	}

	public List<String> addReplyPermission(String permissoin) {
		if (this.replyPermissionList == null) {
			this.replyPermissionList = new ArrayList<>();
		}
		if (!this.replyPermissionList.contains(permissoin)) {
			this.replyPermissionList.add(permissoin);
		}
		return this.replyPermissionList;
	}

	public List<String> addModeratorName(String person) {
		if (this.moderatorNames == null) {
			this.moderatorNames = new ArrayList<>();
		}
		if (!this.moderatorNames.contains(person)) {
			this.moderatorNames.add(person);
		}
		return this.moderatorNames;
	}

	public List<String> addSubjectTypeList(String person) {
		if (this.subjectTypeList == null) {
			this.subjectTypeList = new ArrayList<>();
		}
		if (!this.subjectTypeList.contains(person)) {
			this.subjectTypeList.add(person);
		}
		return this.subjectTypeList;
	}
}
