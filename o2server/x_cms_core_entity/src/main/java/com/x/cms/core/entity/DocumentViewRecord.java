package com.x.cms.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 内容管理日志信息表
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.DocumentViewRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DocumentViewRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentViewRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentViewRecord.table;

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
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("栏目名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("分类ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("分类名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("文档ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	private String documentId;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("文档标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	private String title;

	public static final String viewerName_FIELDNAME = "viewerName";
	@FieldDescribe("访问者姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + viewerName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + IndexNameMiddle + viewerName_FIELDNAME)
	private String viewerName;

	public static final String viewerUnitName_FIELDNAME = "viewerUnitName";
	@FieldDescribe("访问者所属组织名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + viewerUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String viewerUnitName;

	public static final String viewerTopUnitName_FIELDNAME = "viewerTopUnitName";
	@FieldDescribe("访问者所属顶层组织名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + viewerTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String viewerTopUnitName;

	public static final String viewCount_FIELDNAME = "viewCount";
	@FieldDescribe("访问次数")
	@Column(name = ColumnNamePrefix + viewCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer viewCount;

	public static final String lastViewTime_FIELDNAME = "lastViewTime";
	@FieldDescribe("最后访问时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastViewTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastViewTime_FIELDNAME)
	private Date lastViewTime;

	public String getAppId() {
		return appId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public String getViewerName() {
		return viewerName;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public void setViewerName(String viewerName) {
		this.viewerName = viewerName;
	}

	public String getAppName() {
		return appName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getTitle() {
		return title;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public Date getLastViewTime() {
		return lastViewTime;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public void setLastViewTime(Date lastViewTime) {
		this.lastViewTime = lastViewTime;
	}

	public String getViewerUnitName() {
		return viewerUnitName;
	}

	public String getViewerTopUnitName() {
		return viewerTopUnitName;
	}

	public void setViewerUnitName(String viewerUnitName) {
		this.viewerUnitName = viewerUnitName;
	}

	public void setViewerTopUnitName(String viewerTopUnitName) {
		this.viewerTopUnitName = viewerTopUnitName;
	}

}