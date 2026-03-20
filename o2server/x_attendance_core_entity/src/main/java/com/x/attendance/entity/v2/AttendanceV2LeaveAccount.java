package com.x.attendance.entity.v2;


import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;

// 这个表是 性能优化表。
//否则你每次查余额都要：
// sum(ledger.remaining)
@Entity
@Schema(name = "AttendanceV2LeaveAccount", description = "假期账户汇总.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveAccount.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveAccount extends SliceJpaObject {

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

    public static final String totalGranted_FIELDNAME = "totalGranted";
    @FieldDescribe("总发放")
    @Column(name = ColumnNamePrefix + totalGranted_FIELDNAME)
    private Double totalGranted = 0.0;

    public static final String totalUsed_FIELDNAME = "totalUsed";
    @FieldDescribe("总使用")
    @Column(name = ColumnNamePrefix + totalUsed_FIELDNAME)
    private Double totalUsed = 0.0;

    public static final String balance_FIELDNAME = "balance";
    @FieldDescribe("余额")
    @Column(name = ColumnNamePrefix + balance_FIELDNAME)
    private Double balance = 0.0;


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

    public Double getTotalGranted() {
        return totalGranted;
    }

    public void setTotalGranted(Double totalGranted) {
        this.totalGranted = totalGranted;
    }

    public Double getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(Double totalUsed) {
        this.totalUsed = totalUsed;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    
}
