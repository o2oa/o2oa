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


// 假期余额最核心的表。
@Entity
@Schema(name = "AttendanceV2LeaveLedger", description = "假期额度批次.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveLedger.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveLedger extends SliceJpaObject {

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
    @FieldDescribe("发放周期，根据规则发放方式来，如果是年，则格式为 yyyy，如果是月，则格式为 yyyyMM，如果是一次性发放，则格式为一次性")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + grantPeriod_FIELDNAME)
    private String grantPeriod;

    public static final String grantAmount_FIELDNAME = "grantAmount";
    @FieldDescribe("发放额度")
    @Column(name = ColumnNamePrefix + grantAmount_FIELDNAME)
    private Double grantAmount = 0.0;

    public static final String usedAmount_FIELDNAME = "usedAmount";
    @FieldDescribe("已使用额度")
    @Column(name = ColumnNamePrefix + usedAmount_FIELDNAME)
    private Double usedAmount = 0.0;

    public static final String remainingAmount_FIELDNAME = "remainingAmount";
    @FieldDescribe("剩余额度")
    @Column(name = ColumnNamePrefix + remainingAmount_FIELDNAME)
    private Double remainingAmount = 0.0;

    public static final String grantTime_FIELDNAME = "grantTime";
    @FieldDescribe("发放时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + grantTime_FIELDNAME)
    private Date grantTime;


    public static final String expireTime_FIELDNAME = "expireTime";
    @FieldDescribe("过期时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
    private Date expireTime;

    public static final String active_FIELDNAME = "active";
    @FieldDescribe("状态 ENABLED/DISABLED")
    @Column(name = ColumnNamePrefix + active_FIELDNAME)
    private Boolean active = true;


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

    public Double getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Double grantAmount) {
        this.grantAmount = grantAmount;
    }

    public Double getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Double usedAmount) {
        this.usedAmount = usedAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}