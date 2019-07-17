package com.x.cms.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

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
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 文档基础信息类
 * @author O2LEE
 *
 */
@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Document.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Document.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Document extends SliceJpaObject {

	private static final long serialVersionUID = 7668822947307502058L;
	private static final String TABLE = PersistenceProperties.Document.table;
	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;

	/* 以上为 JpaObject 默认字段 */
	public void onPersist() throws Exception {

	}

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
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */
	
	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("文档摘要")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String summary;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("文档标题")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title;

	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("文档类型，跟随分类类型，信息 | 数据")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String documentType = "信息";

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
	
	public static final String appAlias_FIELDNAME = "appAlias";
	@FieldDescribe("栏目别名")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appAlias_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appAlias;

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

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@FieldDescribe("分类别名")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryAlias;

	public static final String form_FIELDNAME = "form";
	@FieldDescribe("绑定的表单模板ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	public static final String formName_FIELDNAME = "formName";
	@FieldDescribe("绑定的表单模板名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + formName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String formName;

	public static final String importBatchName_FIELDNAME = "importBatchName";
	@FieldDescribe("文件导入的批次号：一般是分类ID+时间缀")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + importBatchName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + importBatchName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String importBatchName;

	public static final String readFormId_FIELDNAME = "readFormId";
	@FieldDescribe("绑定的阅读表单模板ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + readFormId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readFormId;

	public static final String readFormName_FIELDNAME = "readFormName";
	@FieldDescribe("绑定的阅读表单模板名称")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + readFormName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readFormName;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("创建人组织，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("创建人顶层组织，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;

	public static final String docStatus_FIELDNAME = "docStatus";
	@FieldDescribe("文档状态: published | draft | checking | error")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + docStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docStatus = "draft";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明备注，可以填写说明信息，如导入信息检验失败原因等")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public static final String viewCount_FIELDNAME = "viewCount";
	@FieldDescribe("文档被查看次数")
	@Column(name = ColumnNamePrefix + viewCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long viewCount = 0L;
	
	public static final String commendCount_FIELDNAME = "commendCount";
	@FieldDescribe("文档被赞次数")
	@Column(name = ColumnNamePrefix + commendCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long commendCount = 0L;
	
	public static final String commentCount_FIELDNAME = "commentCount";
	@FieldDescribe("文档评论次数")
	@Column(name = ColumnNamePrefix + commentCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long commentCount = 0L;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("文档发布时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishTime;
	
	public static final String modifyTime_FIELDNAME = "modifyTime";
	@FieldDescribe("文档修改时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + modifyTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + modifyTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date modifyTime;

	public static final String isTop_FIELDNAME = "isTop";
	@FieldDescribe("是否置顶")
	@Column(name = ColumnNamePrefix + isTop_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isTop_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isTop = false;
	
	public static final String hasIndexPic_FIELDNAME = "hasIndexPic";
	@FieldDescribe("是否含有首页图片")
	@Column(name = ColumnNamePrefix + hasIndexPic_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hasIndexPic_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean hasIndexPic = false;
	
	public static final String reviewed_FIELDNAME = "reviewed";
	@FieldDescribe("是否已经更新review信息.")
	@Column(name = ColumnNamePrefix + reviewed_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + reviewed_FIELDNAME)
	private Boolean reviewed = false;

	public static final String sequenceTitle_FIELDNAME = "sequenceTitle";
	@FieldDescribe("用于标题排序的sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceTitle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceTitle = "";
	
	public static final String sequenceAppAlias_FIELDNAME = "sequenceAppAlias";
	@FieldDescribe("用于栏目别名排序的sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceAppAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceAppAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceAppAlias = "";
	
	public static final String sequenceCategoryAlias_FIELDNAME = "sequenceCategoryAlias";
	@FieldDescribe("用于分类别名排序的sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceCategoryAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceCategoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceCategoryAlias = "";
	
	public static final String sequenceCreatorPerson_FIELDNAME = "sequenceCreatorPerson";
	@FieldDescribe("用于创建者排序的sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceCreatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceCreatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceCreatorPerson = "";
	
	public static final String sequenceCreatorUnitName_FIELDNAME = "sequenceCreatorUnitName";
	@FieldDescribe("用于创建者组织排序的sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceCreatorUnitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceCreatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceCreatorUnitName = "";
	
	public static final String readPersonList_FIELDNAME = "readPersonList";
	@FieldDescribe("阅读人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readPersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readPersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readPersonList;

	public static final String readUnitList_FIELDNAME = "readUnitList";
	@FieldDescribe("阅读组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	public static final String readGroupList_FIELDNAME = "readGroupList";
	@FieldDescribe("阅读群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readGroupList;

	public static final String authorPersonList_FIELDNAME = "authorPersonList";
	@FieldDescribe("作者人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ authorPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + authorPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ authorPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + authorPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> authorPersonList;

	public static final String authorUnitList_FIELDNAME = "authorUnitList";
	@FieldDescribe("作者组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + authorUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + authorUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ authorUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + authorUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> authorUnitList;

	public static final String authorGroupList_FIELDNAME = "authorGroupList";
	@FieldDescribe("作者群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + authorGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + authorGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ authorGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + authorGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> authorGroupList;
	
	public static final String remindPersonList_FIELDNAME = "remindPersonList";
	@FieldDescribe("发布提醒人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ remindPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + remindPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ remindPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + remindPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> remindPersonList;

	public static final String remindUnitList_FIELDNAME = "remindUnitList";
	@FieldDescribe("发布提醒组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + remindUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + remindUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ remindUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + remindUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> remindUnitList;

	public static final String remindGroupList_FIELDNAME = "remindGroupList";
	@FieldDescribe("发布提醒群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + remindGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + remindGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ remindGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + remindGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> remindGroupList;

	public static final String managerList_FIELDNAME = "managerList";
	@FieldDescribe("管理者")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + managerList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + managerList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ managerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + managerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> managerList;

	public static final String pictureList_FIELDNAME = "pictureList";
	@FieldDescribe("首页图片列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + pictureList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + pictureList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ pictureList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + pictureList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> pictureList;
	
	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getReadFormId() {
		return readFormId;
	}

	public void setReadFormId(String readFormId) {
		this.readFormId = readFormId;
	}

	public String getReadFormName() {
		return readFormName;
	}

	public void setReadFormName(String readFormName) {
		this.readFormName = readFormName;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public void addViewCount(Integer count) {
		if( this.viewCount == null ) {
			this.viewCount = 0L;
		}
		this.viewCount = this.viewCount + count;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public List<String> getPictureList() {
		return pictureList;
	}

	public void setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
	}

	public Boolean getHasIndexPic() {
		return hasIndexPic;
	}

	public void setHasIndexPic(Boolean hasIndexPic) {
		this.hasIndexPic = hasIndexPic;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public List<String> getReadPersonList() {
		if (this.readPersonList == null) {
			this.readPersonList = new ArrayList<>();
		}
		return this.readPersonList;
	}

	public List<String> getReadUnitList() {
		if (this.readUnitList == null) {
			this.readUnitList = new ArrayList<>();
		}
		return this.readUnitList;
	}

	public List<String> getReadGroupList() {
		if (this.readGroupList == null) {
			this.readGroupList = new ArrayList<>();
		}
		return this.readGroupList;
	}

	public List<String> getAuthorPersonList() {
		if (this.authorPersonList == null) {
			this.authorPersonList = new ArrayList<>();
		}
		return this.authorPersonList;
	}

	public List<String> getAuthorUnitList() {
		if (this.authorUnitList == null) {
			this.authorUnitList = new ArrayList<>();
		}
		return this.authorUnitList;
	}

	public List<String> getAuthorGroupList() {
		if (this.authorGroupList == null) {
			this.authorGroupList = new ArrayList<>();
		}
		return this.authorGroupList;
	}

	public void setReadPersonList(List<String> readPersonList) {
		this.readPersonList = readPersonList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public void setReadGroupList(List<String> readGroupList) {
		this.readGroupList = readGroupList;
	}

	public void setAuthorPersonList(List<String> authorPersonList) {
		this.authorPersonList = authorPersonList;
	}

	public void setAuthorUnitList(List<String> authorUnitList) {
		this.authorUnitList = authorUnitList;
	}

	public void setAuthorGroupList(List<String> authorGroupList) {
		this.authorGroupList = authorGroupList;
	}

	public List<String> getManagerList() {
		if (this.managerList == null) {
			this.managerList = new ArrayList<>();
		}
		return this.managerList;
	}

	public void setManagerList(List<String> managerList) {
		this.managerList = managerList;
	}

	public void addReadPersonList(String readPerson) {
		this.readPersonList = addStringToList(this.readPersonList, readPerson);
	}

	public void addReadUnitList(String readUnit) {
		this.readUnitList = addStringToList(this.readUnitList, readUnit);
	}

	public void addReadGroupList(String readGroup) {
		this.readGroupList = addStringToList(this.readGroupList, readGroup);
	}

	public void addAuthorPersonList(String authorPerson) {
		this.authorPersonList = addStringToList(this.authorPersonList, authorPerson);
	}

	public void addAuthorUnitList(String authorUnit) {
		this.authorUnitList = addStringToList(this.authorUnitList, authorUnit);
	}

	public void addAuthorGroupList(String authorGroup) {
		this.authorGroupList = addStringToList(this.authorGroupList, authorGroup);
	}

	public void removeReadPersonList(String readPerson) {
		this.readPersonList = addStringToList(this.readPersonList, readPerson);
	}

	public void removeReadUnitList(String readUnit) {
		this.readUnitList = addStringToList(this.readUnitList, readUnit);
	}

	public void removeReadGroupList(String readGroup) {
		this.readGroupList = addStringToList(this.readGroupList, readGroup);
	}

	public void removeAuthorPersonList(String authorPerson) {
		removeStringFromList(this.authorPersonList, authorPerson);
	}

	public void removeAuthorUnitList(String authorUnit) {
		removeStringFromList(this.authorUnitList, authorUnit);
	}

	public void removeAuthorGroupList(String authorGroup) {
		removeStringFromList(this.authorGroupList, authorGroup);
	}

	public void addManagerList(String manager) {
		addStringToList(this.managerList, manager);
	}

	public void removeManagerList(String manager) {
		removeStringFromList(this.managerList, manager);
	}

	public String getImportBatchName() {
		return importBatchName;
	}

	public void setImportBatchName(String importBatchName) {
		this.importBatchName = importBatchName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getRemindPersonList() {
		return remindPersonList;
	}

	public void setRemindPersonList(List<String> remindPersonList) {
		this.remindPersonList = remindPersonList;
	}

	public List<String> getRemindUnitList() {
		return remindUnitList;
	}

	public void setRemindUnitList(List<String> remindUnitList) {
		this.remindUnitList = remindUnitList;
	}

	public List<String> getRemindGroupList() {
		return remindGroupList;
	}

	public void setRemindGroupList(List<String> remindGroupList) {
		this.remindGroupList = remindGroupList;
	}

	private List<String> addStringToList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (!sourceList.contains(targetString)) {
			sourceList.add(targetString);
		}
		return sourceList;
	}

	private List<String> removeStringFromList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (sourceList.contains(targetString)) {
			sourceList.remove(targetString);
		}
		return sourceList;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}

	public Long getCommendCount() {
		return commendCount;
	}

	public void setCommendCount(Long commendCount) {
		this.commendCount = commendCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}
	
	public void addCommentCount(Integer count) {
		if( this.commendCount == null ) {
			this.commentCount = 0L;
		}
		this.commentCount = this.commentCount + count;
	}
	
	public void addCommendCount(Integer count) {
		if( this.commendCount == null ) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount + count;
	}
	
	public void subCommentCount(Integer count) {
		if( this.commentCount == null ) {
			this.commentCount = 0L;
		}
		this.commentCount = this.commentCount - count;
		if( this.commentCount < 0 ) {
			this.commentCount = 0L;
		}
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

	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	public String getAppAlias() {
		return appAlias;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}
	
	public String getSequenceTitle() {
		return sequenceTitle;
	}

	public void setSequenceTitle(String sequenceTitle) {
		this.sequenceTitle = sequenceTitle;
	}

	public String getSequenceAppAlias() {
		return sequenceAppAlias;
	}

	public void setSequenceAppAlias(String sequenceAppAlias) {
		this.sequenceAppAlias = sequenceAppAlias;
	}

	public String getSequenceCategoryAlias() {
		return sequenceCategoryAlias;
	}

	public void setSequenceCategoryAlias(String sequenceCategoryAlias) {
		this.sequenceCategoryAlias = sequenceCategoryAlias;
	}

	public String getSequenceCreatorPerson() {
		return sequenceCreatorPerson;
	}

	public void setSequenceCreatorPerson(String sequenceCreatorPerson) {
		this.sequenceCreatorPerson = sequenceCreatorPerson;
	}

	public String getSequenceCreatorUnitName() {
		return sequenceCreatorUnitName;
	}

	public void setSequenceCreatorUnitName(String sequenceCreatorUnitName) {
		this.sequenceCreatorUnitName = sequenceCreatorUnitName;
	}

	/**
	 * 支持提供排序的列名
	 */
	public static final String[] documentFieldNames = {appAlias_FIELDNAME, appId_FIELDNAME, appName_FIELDNAME, categoryAlias_FIELDNAME, categoryId_FIELDNAME,
			categoryName_FIELDNAME, commendCount_FIELDNAME, commentCount_FIELDNAME, creatorPerson_FIELDNAME, creatorTopUnitName_FIELDNAME, 
			creatorUnitName_FIELDNAME, description_FIELDNAME, docStatus_FIELDNAME, hasIndexPic_FIELDNAME, isTop_FIELDNAME, modifyTime_FIELDNAME, 
			publishTime_FIELDNAME, summary_FIELDNAME, title_FIELDNAME, viewCount_FIELDNAME, createTime_FIELDNAME };

	public static Boolean isFieldInSequence( String orderField ) {
		//判断排序列情况
		if( StringUtils.isEmpty( orderField ) ) { 
			return true;
		}
		if( id_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( sequence_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( title_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( appAlias_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( appName_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( categoryAlias_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( categoryName_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( creatorPerson_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		if( creatorUnitName_FIELDNAME.equalsIgnoreCase( orderField )) {
			return true;
		}
		return false;
	}
	
	public static String getSequnceFieldNameWithProperty( String fieldName ) {
		if( sequence_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequence_FIELDNAME;
		}
		if( id_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return id_FIELDNAME;
		}
		if( title_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceTitle_FIELDNAME;
		}
		if( appAlias_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceAppAlias_FIELDNAME;
		}
		if( appName_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceAppAlias_FIELDNAME;
		}
		if( categoryAlias_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceCategoryAlias_FIELDNAME;
		}
		if( categoryName_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceCategoryAlias_FIELDNAME;
		}
		if( creatorPerson_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceCreatorPerson_FIELDNAME;
		}
		if( creatorUnitName_FIELDNAME.equalsIgnoreCase( fieldName )) {
			return sequenceCreatorUnitName_FIELDNAME;
		}
		return sequence_FIELDNAME;
	}
}