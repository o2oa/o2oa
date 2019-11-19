package com.x.okr.entity;

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
 * 工作考核信息实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkAppraiseInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkAppraiseInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkAppraiseInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkAppraiseInfo.table;

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
	public static final String title_FIELDNAME = "title";
	@FieldDescribe("考核标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerId = "";

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = "";

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	public static final String workTitle_FIELDNAME = "workTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workTitle = "";

	public static final String wf_jobId_FIELDNAME = "wf_jobId";
	@FieldDescribe("流程wf_JobID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + wf_jobId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String wf_jobId = "";

	public static final String wf_workId_FIELDNAME = "wf_workId";
	@FieldDescribe("流程wf_workId")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + wf_workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String wf_workId = "";

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("当前审核环节")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName = "";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("审核状态")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getCenterTitle() {
		return centerTitle;
	}

	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWf_jobId() {
		return wf_jobId;
	}

	public void setWf_jobId(String wf_jobId) {
		this.wf_jobId = wf_jobId;
	}

	public String getWf_workId() {
		return wf_workId;
	}

	public void setWf_workId(String wf_workId) {
		this.wf_workId = wf_workId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}