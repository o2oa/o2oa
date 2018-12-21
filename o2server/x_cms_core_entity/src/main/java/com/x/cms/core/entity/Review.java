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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Review.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Review.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Review extends SliceJpaObject {

	private static final long serialVersionUID = -570048661936488247L;
	private static final String TABLE = PersistenceProperties.Review.table;

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

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
		if ( StringUtils.isEmpty( this.publishTimeMonth ) && (null != this.publishTime )) {
			try {
				this.publishTimeMonth = DateTools.format( this.publishTime, DateTools.format_yyyyMM );
			}catch( Exception e ) {
				this.publishTimeMonth = "0";
			}
		}
	}

	/* 更新运行方法 */

	public Review() {

	}

	public Review( Document doc, String readerType, String readerName, Boolean isPublic ) {
		this.title = doc.getTitle();
		this.appAlias = doc.getAppName();
		this.appId = doc.getAppId();
		this.appName = doc.getAppName();
		this.categoryAlias = doc.getCategoryAlias();
		this.categoryId = doc.getCategoryId();
		this.categoryName = doc.getCategoryName();
		this.creatorIdentity = doc.getCreatorIdentity();
		this.creatorPerson = doc.getCreatorPerson();
		this.creatorUnit = doc.getCreatorUnitName();
		this.documentId = doc.getId();
		this.isPublic = isPublic;
		this.publishTime = doc.getPublishTime();
		
		if ( StringUtils.isEmpty( this.publishTimeMonth ) && (null != this.publishTime )) {
			try {
				this.publishTimeMonth = DateTools.format( this.publishTime, DateTools.format_yyyyMM );
			}catch( Exception e ) {
				this.publishTimeMonth = "0";
			}
		}
		this.readerName = readerName;
		this.readerType = readerType;
	}

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("文档ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String isPublic_FIELDNAME = "isPublic";
	@FieldDescribe("是否全员可见.")
	@Column(name = ColumnNamePrefix + isPublic_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isPublic_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isPublic;
	
	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("发布时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date publishTime;
	
	public static final String publishTimeMonth_FIELDNAME = "publishTimeMonth";
	@FieldDescribe("用于在Filter中分类使用.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + publishTimeMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTimeMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String publishTimeMonth;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("栏目ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("栏目名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix + appName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String appAlias_FIELDNAME = "appAlias";
	@FieldDescribe("栏目别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix + appAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("分类ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("分类名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix + categoryName_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@FieldDescribe("分类别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix + categoryAlias_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;	

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建人Identity")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("创建人组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnit;
	
	public static final String readerType_FIELDNAME = "readerType";
	@FieldDescribe("可见人员|组织|群组")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + readerType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + readerType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String readerType;
	
	public static final String readerName_FIELDNAME = "readerName";
	@FieldDescribe("可见人员|组织|群组名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + readerName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + readerName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String readerName;

	public String getDocumentId() {
		return documentId;
	}

	public String getTitle() {
		return title;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public String getPublishTimeMonth() {
		return publishTimeMonth;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppName() {
		return appName;
	}

	public String getAppAlias() {
		return appAlias;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public String getCreatorUnit() {
		return creatorUnit;
	}
	
	public String getReaderType() {
		return readerType;
	}

	public String getReaderName() {
		return readerName;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public void setPublishTimeMonth(String publishTimeMonth) {
		this.publishTimeMonth = publishTimeMonth;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
	}

	public void setReaderType(String readerType) {
		this.readerType = readerType;
	}

	public void setReaderName(String readerName) {
		this.readerName = readerName;
	}
}