package com.x.processplatform.core.entity.content;

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
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.Manual;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Content.Hint.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Hint.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Hint extends SliceJpaObject {

	private static final long serialVersionUID = 9029223955324393167L;

	private static final String TABLE = PersistenceProperties.Content.Hint.table;

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

	public Hint() {

	}

	public static Hint EmptyTaskIdentityOnManual(Work work, Manual manual) {
		String message = DateTools.now() + " 在 [" + manual.getName() + "] 环节没有找到任何可用的处理人.";
		return new Hint(work, message);
	}

	public Hint(Work work, String message) {
		this.job = work.getJob();
		this.work = work.getId();
		this.application = work.getApplication();
		this.process = work.getProcess();
		this.message = message;
	}

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String work_FIELDNAME = "work";
	@FieldDescribe("工作ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String work;

	public static final String message_FIELDNAME = "message";
	@FieldDescribe("消息内容")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + message_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + message_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String message;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}