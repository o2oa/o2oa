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

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 文档发布消息提醒信息表
 * 
 * @author O2LEE
 *
 */
@Schema(name = "ReadRemind", description = "内容管理文档发布消息提醒信息.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.ReadRemind.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ReadRemind.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ReadRemind extends SliceJpaObject {

	private static final long serialVersionUID = -570048661936488247L;
	private static final String TABLE = PersistenceProperties.ReadRemind.table;

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

	/* 更新运行方法 */
	public ReadRemind() {

	}

	public ReadRemind(Document doc, String readerFlagType, String readerFlagName, String remindReader) {
		this.title = doc.getTitle();
		this.appId = doc.getAppId();
		this.categoryId = doc.getCategoryId();
		this.documentId = doc.getId();
		this.remindTime = new Date();
		this.readerFlagName = readerFlagName;
		this.readerFlagType = readerFlagName;
		this.remindReader = remindReader;
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
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("栏目ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("分类ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public static final String readerFlagType_FIELDNAME = "readerFlagType";
	@FieldDescribe("标识：需要被提醒的人员|组织|群组")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + readerFlagType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String readerFlagType;

	public static final String readerFlagName_FIELDNAME = "readerFlagName";
	@FieldDescribe("标识：需要被提醒的人员|组织|群组名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readerFlagName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String readerFlagName;

	public static final String remindReader_FIELDNAME = "remindReader";
	@FieldDescribe("真正需要被提醒的人员标识")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ remindReader_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String remindReader;

	public static final String remindTime_FIELDNAME = "remindTime";
	@FieldDescribe("提醒发送时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + remindTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date remindTime;

	public static final String readTime_FIELDNAME = "readTime";
	@FieldDescribe("阅读时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + readTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date readTime;

	public static final String reminded_FIELDNAME = "reminded";
	@FieldDescribe("是否已经提醒.")
	@Column(name = ColumnNamePrefix + reminded_FIELDNAME)
	private Boolean reminded = false;

	public static final String readed_FIELDNAME = "readed";
	@FieldDescribe("是否已经阅读.")
	@Column(name = ColumnNamePrefix + readed_FIELDNAME)
	private Boolean readed = false;

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

	public String getReaderFlagType() {
		return readerFlagType;
	}

	public void setReaderFlagType(String readerFlagType) {
		this.readerFlagType = readerFlagType;
	}

	public String getReaderFlagName() {
		return readerFlagName;
	}

	public void setReaderFlagName(String readerFlagName) {
		this.readerFlagName = readerFlagName;
	}

	public String getRemindReader() {
		return remindReader;
	}

	public void setRemindReader(String remindReader) {
		this.remindReader = remindReader;
	}

	public Date getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(Date remindTime) {
		this.remindTime = remindTime;
	}

	public Date getReadTime() {
		return readTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public Boolean getReaded() {
		return readed;
	}

	public void setReaded(Boolean readed) {
		this.readed = readed;
	}

	public Boolean getReminded() {
		return reminded;
	}

	public void setReminded(Boolean reminded) {
		this.reminded = reminded;
	}
}