package com.x.processplatform.core.entity.content;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 签批信息
 * @author sword
 */

@Schema(name = "DocSign", description = "流程平台签名信息.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.DocSign.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.DocSign.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocSign extends SliceJpaObject {

	private static final long serialVersionUID = -6321354875403866277L;

	private static final String TABLE = PersistenceProperties.Content.DocSign.table;

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
		if (StringTools.utf8Length(this.getProperties().getTitle()) > length_255B) {
			this.title = StringTools.utf8SubString(this.getProperties().getTitle(), length_255B - 3) + "...";
		}
	}

	@PostLoad
	public void postLoad() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.getProperties().getTitle())) {
			this.title = this.getProperties().getTitle();
		}
	}

	public void setTitle(String title) {
		this.title = title;
		this.getProperties().setTitle(title);
	}

	public String getTitle() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getTitle())) {
			return this.properties.getTitle();
		} else {
			return this.title;
		}
	}

	public DocSign() {
	}

	public DocSign(Task task) {
		this.setTitle(task.getTitle());
		this.application = task.getApplication();
		this.process = task.getProcess();
		this.job = task.getJob();
		this.activity = task.getActivity();
		this.activityName = task.getActivityName();
		this.person = task.getPerson();
		this.taskId = task.getId();
	}

	public DocSignProperties getProperties() {
		if (null == this.properties) {
			this.properties = new DocSignProperties();
		}
		return this.properties;
	}

	public void setProperties(DocSignProperties properties) {
		this.properties = properties;
	}

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("工单ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String taskId_FIELDNAME = "taskId";
	@FieldDescribe("待办ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + taskId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + taskId_FIELDNAME, unique = true)
	@CheckPersist(allowEmpty = false)
	private String taskId;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("当前处理人")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String signScrawlId_FIELDNAME = "signScrawlId";
	@FieldDescribe("正文签批转存为图片的ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + signScrawlId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String signScrawlId;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态：1(暂存)|2(签批正文不可以修改)|3(签批正文可以修改).")
	@Column(name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer status;

	public static final String commitTime_FIELDNAME = "commitTime";
	@FieldDescribe("提交时间.")
	@Column(name = ColumnNamePrefix + commitTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commitTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date commitTime;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_20M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private DocSignProperties properties;

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Date getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

	public String getSignScrawlId() {
		return signScrawlId;
	}

	public void setSignScrawlId(String signScrawlId) {
		this.signScrawlId = signScrawlId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
