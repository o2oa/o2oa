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
@Schema(name = "AttendanceV2LeaveGrantRecord", description = "假期额度发放记录.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveGrantRecord.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveGrantRecord extends SliceJpaObject {

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

    public static final String policyId_FIELDNAME = "policyId";
    @FieldDescribe("规则ID")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + policyId_FIELDNAME)
    private String policyId;

    public static final String grantPeriod_FIELDNAME = "grantPeriod";
    @FieldDescribe("发放周期")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + grantPeriod_FIELDNAME)
    private String grantPeriod;

    public static final String grantAmount_FIELDNAME = "grantAmount";
    @FieldDescribe("发放额度")
    @Column(name = ColumnNamePrefix + grantAmount_FIELDNAME)
    private Double grantAmount = 0.0;

    public static final String grantTime_FIELDNAME = "grantTime";
    @FieldDescribe("发放时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + grantTime_FIELDNAME)
    private Date grantTime;

    public static final String errorMessage_FIELDNAME = "errorMessage";
    @FieldDescribe("错误信息，如果发放失败，这里会有错误信息")
    @Column( length = JpaObject.length_255B, name = ColumnNamePrefix + errorMessage_FIELDNAME )
    private String errorMessage;


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

    public Double getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Double grantAmount) {
        this.grantAmount = grantAmount;
    }

    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}