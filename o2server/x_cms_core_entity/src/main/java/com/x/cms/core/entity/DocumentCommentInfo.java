package com.x.cms.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 信息评论表
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.DocumentCommentInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DocumentCommentInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentCommentInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentCommentInfo.table;

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
	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("栏目ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("栏目名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appName;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("分类ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("分类名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryName;

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("文档ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String documentId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("评论标题：如果没有则与主题相同")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	private String title = "";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("上级评论ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentId = "";

	public static final String isPrivate_FIELDNAME = "isPrivate";
	@FieldDescribe("是否私信评论")
	@Column(name = ColumnNamePrefix + isPrivate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isPrivate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isPrivate = false;

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("创建人姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorName = "";

	public static final String commentAuditStatus_FIELDNAME = "commentAuditStatus";
	@FieldDescribe("评论审核状态：无审核|待审核|审核通过")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + commentAuditStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commentAuditStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String commentAuditStatus = "无审核";

	public static final String auditorName_FIELDNAME = "auditorName";
	@FieldDescribe("审核人姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + auditorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + auditorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String auditorName = "";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME )
	@CheckPersist( allowEmpty = true)
	private Integer orderNumber = 1;
	
	public static final String commendCount_FIELDNAME = "commendCount";
	@FieldDescribe("评论被赞次数")
	@Column(name = ColumnNamePrefix + commendCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long commendCount = 0L;
	
	public Long getCommendCount() {
		return commendCount;
	}

	public void setCommendCount(Long commendCount) {
		this.commendCount = commendCount;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCommentAuditStatus() {
		return commentAuditStatus;
	}

	public void setCommentAuditStatus(String commentAuditStatus) {
		this.commentAuditStatus = commentAuditStatus;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	public void addCommendCount(Integer count) {
		if( this.commendCount == null ) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount + count;
	}
	
	public void subCommendCount(Integer count) {
		if( this.commendCount == null ) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount - count;
		if( this.commendCount < 0 ) {
			this.commendCount = 0L;
		}
	}
}