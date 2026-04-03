package com.x.attendance.entity.v2;


import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Schema(name = "AttendanceV2LeaveTransaction", description = "假期账户流水.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveTransaction.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveTransaction extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2LeaveTransaction.table;
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

    public static final String ledgerId_FIELDNAME = "ledgerId";
    @FieldDescribe("批次 id")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + ledgerId_FIELDNAME)
    private String ledgerId;

    public static final String bizType_FIELDNAME = "bizType";
    @FieldDescribe("业务类型  GRANT: 发放 USE: 使用 CANCEL: 取消 EXPIRE: 过期 CARRYFORWARD: 结转 ADJUST: 调整")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + bizType_FIELDNAME)
    private String bizType;

    public static final String amount_FIELDNAME = "amount";
    @FieldDescribe("变化额度")
    @Column(name = ColumnNamePrefix + amount_FIELDNAME)
    private Double amount;

    
    public static final String leaveRequestId_FIELDNAME = "leaveRequestId";
    @FieldDescribe("请假申请 id")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + leaveRequestId_FIELDNAME)
    private String leaveRequestId;

    public static final String fromYear_FIELDNAME = "fromYear";
    @FieldDescribe("类型是CARRYFORWARD，来源年份")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fromYear_FIELDNAME)
    private String fromYear;

    

    public String getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(String leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public String getFromYear() {
        return fromYear;
    }

    public void setFromYear(String fromYear) {
        this.fromYear = fromYear;
    }

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

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}