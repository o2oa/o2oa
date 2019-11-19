package com.x.bbs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 论坛信息表
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.BBSForumInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSForumInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSForumInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSForumInfo.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */

	public static final String forumName_FIELDNAME = "forumName";
	@FieldDescribe("论坛名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + forumName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumName = "";

	public static final String forumManagerList_FIELDNAME = "forumManagerList";
	@FieldDescribe("论坛管理员列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ forumManagerList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + forumManagerList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + forumManagerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + forumManagerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> forumManagerList = null;

	public static final String forumNotice_FIELDNAME = "forumNotice";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("论坛公告")
	@Column(length = JpaObject.length_32K, name = ColumnNamePrefix + forumNotice_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String forumNotice = "";

	public static final String forumVisible_FIELDNAME = "forumVisible";
	@FieldDescribe("论坛可见：所有人（默认）|根据权限")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + forumVisible_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumVisible_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumVisible = "所有人";

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
	@Index(name = TABLE + IndexNameMiddle + subjectPublishAble_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectPublishAble = "所有人";

	public static final String publishPermissionList_FIELDNAME = "publishPermissionList";
	@FieldDescribe("可发布范围")
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
	@FieldDescribe("可回复范围")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ replyPermissionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ replyPermissionList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ replyPermissionList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + replyPermissionList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> replyPermissionList;

	public static final String indexListStyle_FIELDNAME = "indexListStyle";
	@FieldDescribe("首页列表样式：经典|简单矩形|图片矩形")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + indexListStyle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String indexListStyle = "经典";

	public static final String forumIndexStyle_FIELDNAME = "forumIndexStyle";
	@FieldDescribe("论坛主页面样式：经典|新闻|照片")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + forumIndexStyle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumIndexStyle = "经典";

	public static final String subjectType_FIELDNAME = "subjectType";
	@FieldDescribe("论坛版块默认的主题分类名称,以|分隔,如讨论|新闻")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + subjectType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectType = "讨论|新闻";

	public static final String typeCategory_FIELDNAME = "typeCategory";
	@FieldDescribe("论坛版块支持的主题类别名称,以|分隔,默认:信息|问题|投票")
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

	public static final String sectionCreateAble_FIELDNAME = "sectionCreateAble";
	@FieldDescribe("允许创建版块：true|false")
	@Column(name = ColumnNamePrefix + sectionCreateAble_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean sectionCreateAble = true;

	public static final String sectionTotal_FIELDNAME = "sectionTotal";
	@FieldDescribe("版块数量")
	@Column(name = ColumnNamePrefix + sectionTotal_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long sectionTotal = 0L;

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

	public static final String forumColor_FIELDNAME = "forumColor";
	@FieldDescribe("主题顔色")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + forumColor_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumColor = "";

	public static final String forumStatus_FIELDNAME = "forumStatus";
	@FieldDescribe("论坛状态：启用|停用")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + forumStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumStatus = "启用";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public static final String replyMessageNotify_FIELDNAME = "replyMessageNotify";
	@FieldDescribe("回复消息通知：true|false")
	@Column(name = ColumnNamePrefix + replyMessageNotify_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + replyMessageNotify_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean replyMessageNotify = false;

	public static final String replyMessageNotifyType_FIELDNAME = "replyMessageNotifyType";
	@FieldDescribe("回复消息通知类别：一共3位，第1位是否通知论坛分区管理员，第2位是否通知版主，第3位是否通知发贴人，0-不通知|1-通知")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + replyMessageNotifyType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String replyMessageNotifyType = "0,0,0";

	public static final String subjectMessageNotify_FIELDNAME = "subjectMessageNotify";
	@FieldDescribe("新主题发布消息通知：true|false")
	@Column(name = ColumnNamePrefix + subjectMessageNotify_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subjectMessageNotify_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean subjectMessageNotify = false;

	public static final String subjectMessageNotifyType_FIELDNAME = "subjectMessageNotifyType";
	@FieldDescribe("新主题发布消息通知类别：一共2位，第1位是否通知论坛分区管理员，第2位是否通知版主，0-不通知|1-通知")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + subjectMessageNotifyType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String subjectMessageNotifyType = "0,0";

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public List<String> getForumManagerList() {
		return forumManagerList;
	}

	public void setForumManagerList(List<String> forumManagerList) {
		this.forumManagerList = forumManagerList;
	}

	public String getForumNotice() {
		return forumNotice;
	}

	public void setForumNotice(String forumNotice) {
		this.forumNotice = forumNotice;
	}

	public String getForumVisible() {
		return forumVisible;
	}

	public void setForumVisible(String forumVisible) {
		this.forumVisible = forumVisible;
	}

	public String getIndexListStyle() {
		return indexListStyle;
	}

	public void setIndexListStyle(String indexListStyle) {
		this.indexListStyle = indexListStyle;
	}

	public String getForumIndexStyle() {
		return forumIndexStyle;
	}

	public void setForumIndexStyle(String forumIndexStyle) {
		this.forumIndexStyle = forumIndexStyle;
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

	public Boolean getSectionCreateAble() {
		return sectionCreateAble;
	}

	public void setSectionCreateAble(Boolean sectionCreateAble) {
		this.sectionCreateAble = sectionCreateAble;
	}

	public Long getSectionTotal() {
		return sectionTotal;
	}

	public void setSectionTotal(Long sectionTotal) {
		this.sectionTotal = sectionTotal;
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

	public String getForumStatus() {
		return forumStatus;
	}

	public void setForumStatus(String forumStatus) {
		this.forumStatus = forumStatus;
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

	public String getForumColor() {
		return forumColor;
	}

	public void setForumColor(String forumColor) {
		this.forumColor = forumColor;
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

	public Boolean getReplyMessageNotify() { return this.replyMessageNotify; }

	public void setReplyMessageNotify(final Boolean replyMessageNotify) { this.replyMessageNotify = replyMessageNotify; }

	public String getReplyMessageNotifyType() { return this.replyMessageNotifyType; }

	public void setReplyMessageNotifyType(final String replyMessageNotifyType) { this.replyMessageNotifyType = replyMessageNotifyType; }

	public Boolean getSubjectMessageNotify() {
		return this.subjectMessageNotify;
	}

	public void setSubjectMessageNotify(final Boolean subjectMessageNotify) {
		this.subjectMessageNotify = subjectMessageNotify;
	}

	public String getSubjectMessageNotifyType() {
		return this.subjectMessageNotifyType;
	}

	public void setSubjectMessageNotifyType(final String subjectMessageNotifyType) {
		this.subjectMessageNotifyType = subjectMessageNotifyType;
	}

	public long minusSection(Long count) {
		if (this.sectionTotal == null || this.sectionTotal < 0) {
			this.sectionTotal = 0L;
		}
		if (count <= 0) {
			return this.sectionTotal;
		}
		if (this.sectionTotal > 0) {
			this.sectionTotal = this.sectionTotal - 1;
		}
		return this.sectionTotal.longValue();
	}

	public long addSection(Long count) {
		if (this.sectionTotal == null || this.sectionTotal < 0) {
			this.sectionTotal = 0L;
		}
		if (count <= 0) {
			return this.sectionTotal.longValue();
		} else {
			this.sectionTotal = this.sectionTotal + 1;
			return this.sectionTotal.longValue();
		}
	}

	public long minusSubjectTotal(Long count) {
		if (this.subjectTotal == null || this.subjectTotal < 0) {
			this.subjectTotal = 0L;
		}
		if (count <= 0) {
			return this.subjectTotal;
		}
		if (this.subjectTotal > 0) {
			this.subjectTotal = this.subjectTotal - 1;
		}
		return this.subjectTotal.longValue();
	}

	public long addSubjectTotal(Long count) {
		if (this.subjectTotal == null || this.subjectTotal < 0) {
			this.subjectTotal = 0L;
		}
		if (count <= 0) {
			return this.subjectTotal.longValue();
		} else {
			this.subjectTotal = this.subjectTotal + 1;
			return this.subjectTotal.longValue();
		}
	}

	public long minusSubjectTotalToday(Long count) {
		if (this.subjectTotalToday == null || this.subjectTotalToday < 0) {
			this.subjectTotalToday = 0L;
		}
		if (count <= 0) {
			return this.subjectTotalToday;
		}
		if (this.subjectTotalToday > 0) {
			this.subjectTotalToday = this.subjectTotalToday - 1;
		}
		return this.subjectTotalToday.longValue();
	}

	public long addSubjectTotalToday(Long count) {
		if (this.subjectTotalToday == null || this.subjectTotalToday < 0) {
			this.subjectTotalToday = 0L;
		}
		if (count <= 0) {
			return this.subjectTotalToday.longValue();
		} else {
			this.subjectTotalToday = this.subjectTotalToday + 1;
			return this.subjectTotalToday.longValue();
		}
	}

	public long minusReplyTotal(Long count) {
		if (this.replyTotal == null || this.replyTotal < 0) {
			this.replyTotal = 0L;
		}
		if (count <= 0) {
			return this.replyTotal;
		}
		if (this.replyTotal > 0) {
			this.replyTotal = this.replyTotal - 1;
		}
		return this.replyTotal.longValue();
	}

	public long addReplyTotal(Long count) {
		if (this.replyTotal == null || this.replyTotal < 0) {
			this.replyTotal = 0L;
		}
		if (count <= 0) {
			return this.replyTotal.longValue();
		} else {
			this.replyTotal = this.replyTotal + 1;
			return this.replyTotal.longValue();
		}
	}

	public long minusReplyTotalToday(Long count) {
		if (this.replyTotalToday == null || this.replyTotalToday < 0) {
			this.replyTotalToday = 0L;
		}
		if (count <= 0) {
			return this.replyTotalToday;
		}
		if (this.replyTotalToday > 0) {
			this.replyTotalToday = this.replyTotalToday - 1;
		}
		return this.replyTotalToday.longValue();
	}

	public long addReplyTotalToday(Long count) {
		if (this.replyTotalToday == null || this.replyTotalToday < 0) {
			this.replyTotalToday = 0L;
		}
		if (count <= 0) {
			return this.replyTotalToday.longValue();
		} else {
			this.replyTotalToday = this.replyTotalToday + 1;
			return this.replyTotalToday.longValue();
		}
	}

	public List<String> addForumManager(String forumManagerName) {
		if (this.forumManagerList == null) {
			this.forumManagerList = new ArrayList<>();
		}
		if (!this.forumManagerList.contains(forumManagerName)) {
			this.forumManagerList.add(forumManagerName);
		}
		return this.forumManagerList;
	}

	public List<String> getVisiblePermissionList() {
		return visiblePermissionList;
	}

	public void setVisiblePermissionList(List<String> visiblePermissionList) {
		this.visiblePermissionList = visiblePermissionList;
	}

	public List<String> addVisiblePermission(String permissoin) {
		if (this.visiblePermissionList == null) {
			this.visiblePermissionList = new ArrayList<>();
		}
		if (!this.visiblePermissionList.contains(permissoin)) {
			this.visiblePermissionList.add(permissoin);
		}
		return this.visiblePermissionList;
	}

	public List<String> getPublishPermissionList() {
		return publishPermissionList;
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
}