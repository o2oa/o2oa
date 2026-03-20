package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;

// 年假结转记录
@Entity
@Schema(name = "AttendanceV2LeaveCarryForward", description = "假期结转记录.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveCarryForward.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveCarryForward extends SliceJpaObject {

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

    public static final String fromYear_FIELDNAME = "fromYear";
    @FieldDescribe("来源年份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fromYear_FIELDNAME)
    private String fromYear;

    public static final String amount_FIELDNAME = "amount";
    @FieldDescribe("结转额度")
    @Column(name = ColumnNamePrefix + amount_FIELDNAME)
    private Double amount;


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

    public String getFromYear() {
        return fromYear;
    }

    public void setFromYear(String fromYear) {
        this.fromYear = fromYear;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
