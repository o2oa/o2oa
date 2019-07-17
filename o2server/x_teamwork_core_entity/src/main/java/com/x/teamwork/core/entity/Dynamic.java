package com.x.teamwork.core.entity;

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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Dynamic.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Dynamic.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Dynamic extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Dynamic.table;

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
	public static final String optType_FIELDNAME = "optType";
	@FieldDescribe("操作类别：Create|Modify|Delete|Other")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + optType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String optType = "Other";

	public static final String objectType_FIELDNAME = "objectType";
	@FieldDescribe("动态对象类别:Project|Task|TaskGroup|TaskView|Config|Chat|Other")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + objectType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + objectType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String objectType = "Other";

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("操作对象ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + bundle_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String bundle = null;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("动态对象标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title = "";

	public static final String projectId_FIELDNAME = "projectId";
	@FieldDescribe("所属项目ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + projectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + projectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projectId = null;

	public static final String projectTitle_FIELDNAME = "projectTitle";
	@FieldDescribe("项目标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + projectTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projectTitle = null;

	public static final String taskId_FIELDNAME = "taskId";
	@FieldDescribe("所属工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + taskId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskId = null;

	public static final String taskTitle_FIELDNAME = "taskTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + taskTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String taskTitle = null;

	public static final String dateTimeStr_FIELDNAME = "dateTimeStr";
	@FieldDescribe("操作时间")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dateTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dateTimeStr = null;

	public static final String optTime_FIELDNAME = "optTime";
	@FieldDescribe("操作时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + optTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date optTime = null;

	public static final String operator_FIELDNAME = "operator";
	@FieldDescribe("操作者名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + operator_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + operator_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operator = null;

	public static final String target_FIELDNAME = "target";
	@FieldDescribe("目标者姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + target_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String target = null;

	public static final String content_FIELDNAME = "content";
	@FieldDescribe("内容")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + content_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String content = null;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public static final String viewUrl_FIELDNAME = "viewUrl";
	@FieldDescribe("访问链接")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + viewUrl_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String viewUrl = null;
	
	public static final String personal_FIELDNAME = "personal";
	@FieldDescribe("是否是关于用户个人操作比如工作任务组，项目组，工作列表")
	@Column( name = ColumnNamePrefix + personal_FIELDNAME)
	@Index( name = TABLE + IndexNameMiddle + personal_FIELDNAME )
	private Boolean personal = false;

	public Boolean getPersonal() {
		return personal;
	}

	public void setPersonal(Boolean personal) {
		this.personal = personal;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}	

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getDateTimeStr() {
		return dateTimeStr;
	}

	public void setDateTimeStr(String dateTimeStr) {
		this.dateTimeStr = dateTimeStr;
	}

	public Date getOptTime() {
		return optTime;
	}

	public void setOptTime(Date optTime) {
		this.optTime = optTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}
}