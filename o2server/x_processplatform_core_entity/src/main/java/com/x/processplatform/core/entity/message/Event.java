package com.x.processplatform.core.entity.message;

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
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Event", description = "流程平台流转事件.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Message.Event.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Message.Event.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Event extends SliceJpaObject {

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.Message.Event.table;

	public static final String EVENTTYPE_UPDATETABLE = "updateTable";

	public static final String EVENTTYPE_ARCHIVEHADOOP = "archiveHadoop";

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
		// nothing
	}

	public Event() {
		this.failure = 0;
	}

	public static final String JOB_FIELDNAME = "job";
	@FieldDescribe("任务标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + JOB_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String job;

	public static final String TARGET_FIELDNAME = "target";
	@FieldDescribe("目标对象标识.")
	@Column(length = length_255B, name = ColumnNamePrefix + TARGET_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String target;

	public static final String TYPE_FIELDNAME = "type";
	@FieldDescribe("消息类型.")
	@Column(length = length_255B, name = ColumnNamePrefix + TYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String FAILURE_FIELDNAME = "failure";
	@FieldDescribe("失败次数.")
	@Column(name = ColumnNamePrefix + FAILURE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer failure;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Integer getFailure() {
		return failure;
	}

	public void setFailure(Integer failure) {
		this.failure = failure;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}