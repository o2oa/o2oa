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

// 记录某次请假 使用了哪些批次额度。
// 支持请假撤销回滚
@Entity
@Schema(name = "AttendanceV2LeaveUsageDetail", description = "假期使用明细.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveUsageDetail.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveUsageDetail extends SliceJpaObject {

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

    public static final String leaveRequestId_FIELDNAME = "leaveRequestId";
    @FieldDescribe("请假申请 id")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + leaveRequestId_FIELDNAME)
    private String leaveRequestId;

    public static final String ledgerId_FIELDNAME = "ledgerId";
    @FieldDescribe("批次 id")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + ledgerId_FIELDNAME)
    private String ledgerId;

    public static final String usedAmount_FIELDNAME = "usedAmount";
    @FieldDescribe("使用额度")
    @Column(name = ColumnNamePrefix + usedAmount_FIELDNAME)
    private Double usedAmount;


    public String getLeaveRequestId() {
        return leaveRequestId;
    }

    public void setLeaveRequestId(String leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
    }

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public Double getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Double usedAmount) {
        this.usedAmount = usedAmount;
    }
}