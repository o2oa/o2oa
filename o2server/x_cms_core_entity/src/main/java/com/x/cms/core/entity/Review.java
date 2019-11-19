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
	}

	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("文档类型：信息|数据.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String documentType;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("栏目ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("栏目名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ appName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String appAlias_FIELDNAME = "appAlias";
	@FieldDescribe("栏目别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ appAlias_FIELDNAME)
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
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ categoryName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@FieldDescribe("分类别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ categoryAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;

	public static final String docId_FIELDNAME = "docId";
	@FieldDescribe("文档ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + docId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docId;

	public static final String docSequence_FIELDNAME = "docSequence";
	@FieldDescribe("文档Sequence.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + docSequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docSequence_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docSequence;

	public static final String docStatus_FIELDNAME = "docStatus";
	@FieldDescribe("文档状态.")
	@Column(length = length_32B, name = ColumnNamePrefix + docStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String docStatus;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("文档标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("拟稿人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建人Identity")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("创建人组织")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("创建人顶层组织，可能为空，如果由系统创建。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;

	public static final String docCreateTime_FIELDNAME = "docCreateTime";
	@FieldDescribe("document的拟稿时间.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 结束时间不能为空,如果为空排序可能出错 */
	@Column(name = ColumnNamePrefix + docCreateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docCreateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date docCreateTime;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("document的发布时间.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 结束时间不能为空,如果为空排序可能出错 */
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishTime;

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

	public static final String permissionObj_FIELDNAME = "permissionObj";
	@FieldDescribe("权限拥有者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ permissionObj_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionObj_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionObj;

	public static final String permissionObjType_FIELDNAME = "permissionObjType";
	@FieldDescribe("权限拥有者类型: PERSON|IDENTITY|UNIT|GROUP|ROLE")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + permissionObjType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionObjType;

	public static final String importBatchName_FIELDNAME = "importBatchName";
	@FieldDescribe("文件导入的批次号：一般是分类ID+时间缀")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + importBatchName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + importBatchName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String importBatchName;

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

	public static final String modifyTime_FIELDNAME = "modifyTime";
	@FieldDescribe("文档修改时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + modifyTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + modifyTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date modifyTime;

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

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
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

	public String getImportBatchName() {
		return importBatchName;
	}

	public void setImportBatchName(String importBatchName) {
		this.importBatchName = importBatchName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
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

	public String getAppAlias() {
		return appAlias;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
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

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getDocCreateTime() {
		return docCreateTime;
	}

	public void setDocCreateTime(Date docCreateTime) {
		this.docCreateTime = docCreateTime;
	}

	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	public Boolean getHasIndexPic() {
		return hasIndexPic;
	}

	public void setHasIndexPic(Boolean hasIndexPic) {
		this.hasIndexPic = hasIndexPic;
	}

	public String getPermissionObj() {
		return permissionObj;
	}

	public void setPermissionObj(String permissionObj) {
		this.permissionObj = permissionObj;
	}

	public String getPermissionObjType() {
		return permissionObjType;
	}

	public void setPermissionObjType(String permissionObjType) {
		this.permissionObjType = permissionObjType;
	}

	public String getDocSequence() {
		return docSequence;
	}

	public void setDocSequence(String docSequence) {
		this.docSequence = docSequence;
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

	public static final String[] sortableFieldNames = { appAlias_FIELDNAME, appId_FIELDNAME, appName_FIELDNAME,
			categoryAlias_FIELDNAME, categoryId_FIELDNAME, categoryName_FIELDNAME, commendCount_FIELDNAME,
			commentCount_FIELDNAME, creatorPerson_FIELDNAME, creatorTopUnitName_FIELDNAME, creatorUnitName_FIELDNAME,
			docStatus_FIELDNAME, hasIndexPic_FIELDNAME, isTop_FIELDNAME, modifyTime_FIELDNAME, publishTime_FIELDNAME,
			title_FIELDNAME, viewCount_FIELDNAME };

	public static Boolean isFieldInSequence(String orderField) {
		// 判断排序列情况
		if (StringUtils.isEmpty(orderField)) {
			return true;
		}
		if (id_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (sequence_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (title_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (appAlias_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (appName_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (categoryAlias_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (categoryName_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (creatorPerson_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (creatorUnitName_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		return false;
	}

	public static String getSequnceFieldNameWithProperty(String fieldName) {
		if (sequence_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequence_FIELDNAME;
		}
		if (id_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return id_FIELDNAME;
		}
		if (title_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceTitle_FIELDNAME;
		}
		if (appAlias_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceAppAlias_FIELDNAME;
		}
		if (appName_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceAppAlias_FIELDNAME;
		}
		if (categoryAlias_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCategoryAlias_FIELDNAME;
		}
		if (categoryName_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCategoryAlias_FIELDNAME;
		}
		if (creatorPerson_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCreatorPerson_FIELDNAME;
		}
		if (creatorUnitName_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCreatorUnitName_FIELDNAME;
		}
		return sequence_FIELDNAME;
	}
}