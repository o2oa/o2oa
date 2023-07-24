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
import com.x.processplatform.core.entity.content.WorkCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "WorkEvent", description = "工作处理事件.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Message.WorkCompletedEvent.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Message.WorkCompletedEvent.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WorkCompletedEvent extends SliceJpaObject {

    private static final long serialVersionUID = -4700820656587964782L;

	private static final String TABLE = PersistenceProperties.Message.WorkCompletedEvent.table;

    public static final String TYPE_UPDATE = "update";
    public static final String TYPE_CREATE = "create";
    public static final String TYPE_DELETE = "delete";

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

    public WorkCompletedEvent() {

    }

    public static WorkCompletedEvent createEventInstance(String application, String workCompleted, String job) {
        WorkCompletedEvent o = new WorkCompletedEvent();
        o.setType(TYPE_CREATE);
        o.setApplication(application);
        o.setWorkCompleted(workCompleted);
        o.setJob(job);
        return o;
    }

    public static WorkCompletedEvent updateEventInstance(String application, String workCompleted, String job) {
        WorkCompletedEvent o = new WorkCompletedEvent();
        o.setType(TYPE_UPDATE);
        o.setApplication(application);
        o.setWorkCompleted(workCompleted);
        o.setJob(job);
        return o;
    }

    public static WorkCompletedEvent deleteEventInstance(String application, String workCompleted, String job) {
        WorkCompletedEvent o = new WorkCompletedEvent();
        o.setType(TYPE_DELETE);
        o.setApplication(application);
        o.setWorkCompleted(workCompleted);
        o.setJob(job);
        return o;
    }

    public static WorkCompletedEvent createEventInstance(WorkCompleted workCompleted) {
        return createEventInstance(workCompleted.getApplication(), workCompleted.getId(),
                workCompleted.getJob());
    }

    public static WorkCompletedEvent updateEventInstance(WorkCompleted workCompleted) {
        return updateEventInstance(workCompleted.getApplication(), workCompleted.getId(),
                workCompleted.getJob());
    }

    public static WorkCompletedEvent deleteEventInstance(WorkCompleted workCompleted) {
        return deleteEventInstance(workCompleted.getApplication(), workCompleted.getId(),
                workCompleted.getJob());
    }

    public static final String JOB_FIELDNAME = "job";
    @Schema(description = "任务标识.")
    @FieldDescribe("任务标识")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + JOB_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + JOB_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String job;

    public static final String WORKCOMPLETED_FIELDNAME = "workCompleted";
    @Schema(description = "已完成工作标识.")
    @FieldDescribe("已完成工作标识.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + WORKCOMPLETED_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + WORKCOMPLETED_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String workCompleted;

    public static final String TYPE_FIELDNAME = "type";
    @Schema(description = "工作事件类型.")
    @FieldDescribe("工作事件类型.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + TYPE_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + TYPE_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String type;

    public static final String APPLICATION_FIELDNAME = "application";
    @Schema(description = "流程应用标识.")
    @FieldDescribe("应用.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + APPLICATION_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + APPLICATION_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String application;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getWorkCompleted() {
        return workCompleted;
    }

    public void setWorkCompleted(String workCompleted) {
        this.workCompleted = workCompleted;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}