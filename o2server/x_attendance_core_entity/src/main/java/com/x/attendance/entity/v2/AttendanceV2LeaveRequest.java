package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Schema(name = "AttendanceV2LeaveRequest", description = "请假申请.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveRequest.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveRequest extends SliceJpaObject {

    private static final long serialVersionUID = 1L;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {}

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("用户")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + person_FIELDNAME)
    private String person;

    public static final String leaveTypeId_FIELDNAME = "leaveTypeId";
    @FieldDescribe("假期类型")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + leaveTypeId_FIELDNAME)
    private String leaveTypeId;

    public static final String startTime_FIELDNAME = "startTime";
    @FieldDescribe("开始时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + startTime_FIELDNAME)
    private Date startTime;

    public static final String endTime_FIELDNAME = "endTime";
    @FieldDescribe("结束时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + endTime_FIELDNAME)
    private Date endTime;

    public static final String duration_FIELDNAME = "duration";
    @FieldDescribe("请假时长")
    @Column(name = ColumnNamePrefix + duration_FIELDNAME)
    private Double duration;

    public static final String status_FIELDNAME = "status";
    @FieldDescribe("请假当前状态： PENDING / APPROVED / REJECTED / CANCELLED")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
    private String status;

    public static final String jobId_FIELDNAME = "jobId";
    @FieldDescribe("流程的jobId，申诉流程结束后写入.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + jobId_FIELDNAME)
    private String jobId;


    public static final String description_FIELDNAME = "description";
    @FieldDescribe("请假说明")
    @Column( name = ColumnNamePrefix + description_FIELDNAME )
    private String description;


    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(String leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
