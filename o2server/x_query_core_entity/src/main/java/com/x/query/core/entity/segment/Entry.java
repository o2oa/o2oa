package com.x.query.core.entity.segment;

import java.util.Date;

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
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.entity.PersistenceProperties;
import com.x.query.core.entity.Stat;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Segment.Entry.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Segment.Entry.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Entry extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Segment.Entry.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	@CheckRemove(citationNotExists =
	/* 已经没有Stat使用View了 */
	@CitationNotExist(type = Stat.class, fields = Stat.view_FIELDNAME))
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {

	}

	/* 更新运行方法 */

	public static final String TYPE_WORKCOMPLETED = "workCompleted";
	public static final String TYPE_WORK = "work";
	public static final String TYPE_CMS = "cms";

	public static final String bundle_FIELDNAME = "bundle";
	@Flag
	@FieldDescribe("通用数据标识,cms为doucment.getId(),processPlatform为job")
	@Column(length = length_255B, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String bundle;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型.")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String type;

	public static final String reference_FIELDNAME = "reference";
	@FieldDescribe("关联id")
	@Column(length = length_id, name = ColumnNamePrefix + reference_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + reference_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String reference;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("记录的最后修改时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	private Date lastUpdateTime;

	public static final String wait_FIELDNAME = "wait";
	@FieldDescribe("等待处理")
	@Column(name = ColumnNamePrefix + wait_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + wait_FIELDNAME)
	private Boolean wait;

	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("摘要")
	@Column(length = length_255B, name = ColumnNamePrefix + summary_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String summary;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("创建部门")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnit;

	/**/
	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("cms栏目ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("cms栏目名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + appName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("cms分类ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("cms分类名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("processPlatform应用id")
	@Column(length = length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String application;

	public static final String applicationName_FIELDNAME = "applicationName";
	@FieldDescribe("processPlatform应用名称")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("processPlatform流程id")
	@Column(length = length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@FieldDescribe("processPlatform流程名称")
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Boolean getWait() {
		return wait;
	}

	public void setWait(Boolean wait) {
		this.wait = wait;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
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

}
