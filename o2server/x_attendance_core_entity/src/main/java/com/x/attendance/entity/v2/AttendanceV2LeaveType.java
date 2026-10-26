package com.x.attendance.entity.v2;


import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
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
@Schema(name = "AttendanceV2LeaveType", description = "假期类型.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveType.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveType extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2LeaveType.table;
    private static final long serialVersionUID = 1L;

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

    public void onPersist() throws Exception {
    }

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("假期名称")
    @CheckPersist(allowEmpty = false)
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + name_FIELDNAME)
    private String name;

    public static final String quotaType_FIELDNAME = "quotaType";
    @FieldDescribe("额度类型 QUOTA|UNLIMITED")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + quotaType_FIELDNAME)
    private String quotaType;

    public static final String unit_FIELDNAME = "unit";
    @FieldDescribe("单位 DAY/HOUR")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + unit_FIELDNAME)
    private String unit;

    public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	private Integer orderNumber;

    public static final String isPaid_FIELDNAME = "isPaid";
    @FieldDescribe("是否带薪")
    @Column(name = ColumnNamePrefix + isPaid_FIELDNAME)
    private Boolean isPaid = true;

    public static final String active_FIELDNAME = "active";
    @FieldDescribe("状态 ENABLED/DISABLED")
    @Column(name = ColumnNamePrefix + active_FIELDNAME)
    private Boolean active = true;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }
    
}
