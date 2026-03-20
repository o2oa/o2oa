package com.x.attendance.entity.v2;


import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import javax.persistence.*;

@Entity
@Schema(name = "AttendanceV2LeaveGrantJob", description = "假期额度发放任务.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveGrantJob.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveGrantJob extends SliceJpaObject {

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

    public static final String policyId_FIELDNAME = "policyId";
    @FieldDescribe("规则ID")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + policyId_FIELDNAME)
    private String policyId;

    public static final String grantPeriod_FIELDNAME = "grantPeriod";
    @FieldDescribe("发放周期，例如 2025")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + grantPeriod_FIELDNAME)
    private String grantPeriod;

    public static final String status_FIELDNAME = "status";
    @FieldDescribe("任务状态 RUNNING|SUCCESS|FAILED")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
    private String status;


    public static final String finishTime_FIELDNAME = "finishTime";
    @FieldDescribe("任务完成时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + finishTime_FIELDNAME)
    private Date finishTime;


    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getGrantPeriod() {
        return grantPeriod;
    }

    public void setGrantPeriod(String grantPeriod) {
        this.grantPeriod = grantPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}