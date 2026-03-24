package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Strategy;

// 定义 假期额度发放规则。
@Entity
@Schema(name = "AttendanceV2LeavePolicy", description = "假期额度规则.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeavePolicy.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeavePolicy extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2LeavePolicy.table;
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

    public static final String leaveTypeId_FIELDNAME = "leaveTypeId";
    @FieldDescribe("假期类型ID")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + leaveTypeId_FIELDNAME)
    private String leaveTypeId;

    public static final String policyName_FIELDNAME = "policyName";
    @FieldDescribe("规则名称")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + policyName_FIELDNAME)
    private String policyName;

     public static final String grantScopeType_FIELDNAME = "grantScopeType";
    @FieldDescribe("发放范围 ALL: 全员  DEPARTMENT: 指定员工/部门")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + grantScopeType_FIELDNAME)
    private String grantScopeType;

    public static final String grantScopeList_FIELDNAME = "grantScopeList";
	@FieldDescribe("grantScopeType=DEPARTMENT， 发放范围人员、组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = PersistenceProperties.AttendanceV2LeavePolicy.table + ContainerTableNameMiddle
			+ grantScopeList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = PersistenceProperties.AttendanceV2LeavePolicy.table + grantScopeList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + grantScopeList_FIELDNAME)
	@ElementIndex(name = PersistenceProperties.AttendanceV2LeavePolicy.table + grantScopeList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> grantScopeList;

    public static final String grantType_FIELDNAME = "grantType";
    @FieldDescribe("发放方式： YEARLY/MONTHLY/ONE_TIME")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + grantType_FIELDNAME)
    private String grantType;

    public static final String grantTypeValue_FIELDNAME = "grantTypeValue";
    @FieldDescribe("发放方式日期规则配置： Y:01-01/MF:1,ML:1/ONE_TIME")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + grantTypeValue_FIELDNAME)
    private String grantTypeValue;

    public static final String grantNextExecuteTime_FIELDNAME = "grantNextExecuteTime";
    @FieldDescribe("下次发放时间，yyyy-MM-dd")
    @Column(  length = JpaObject.length_16B, name = ColumnNamePrefix + grantNextExecuteTime_FIELDNAME)
    private String grantNextExecuteTime;
   
    public static final String grantAmount_FIELDNAME = "grantAmount";
    @FieldDescribe("发放额度")
    @Column(name = ColumnNamePrefix + grantAmount_FIELDNAME)
    private Double grantAmount = 0.0;
    
	public static final String grantAmountType_FIELDNAME = "grantAmountType";
	@FieldDescribe("发放额度规则，只在按年发放时需要.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + grantAmountType_FIELDNAME)
	private AttendanceV2LeavePolicyGrantAmountTypeProperties grantAmountType;
 

    public static final String expireType_FIELDNAME = "expireType";
    @FieldDescribe("过期类型 NEVER / FIXED / RELATIVE")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + expireType_FIELDNAME)
    private String expireType;

    public static final String expireValue_FIELDNAME = "expireValue";
    @FieldDescribe("过期值")
    @Column(name = ColumnNamePrefix + expireValue_FIELDNAME)
    private Integer expireValue;

    public static final String carryForward_FIELDNAME = "carryForward";
    @FieldDescribe("是否允许结转")
    @Column(name = ColumnNamePrefix + carryForward_FIELDNAME)
    private Boolean carryForward = false;

    public static final String maxCarryForward_FIELDNAME = "maxCarryForward";
    @FieldDescribe("最大结转额度")
    @Column(name = ColumnNamePrefix + maxCarryForward_FIELDNAME)
    private Double maxCarryForward = 0.0;

    public static final String policyVersion_FIELDNAME = "policyVersion";
    @FieldDescribe("规则版本")
    @Column(name = ColumnNamePrefix + policyVersion_FIELDNAME)
    private Integer policyVersion = 1;

    public static final String active_FIELDNAME = "active";
    @FieldDescribe("状态 ENABLED/DISABLED")
    @Column(name = ColumnNamePrefix + active_FIELDNAME)
    private Boolean active = true;




    public String getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(String leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public Double getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Double grantAmount) {
        this.grantAmount = grantAmount;
    }

    public String getExpireType() {
        return expireType;
    }

    public void setExpireType(String expireType) {
        this.expireType = expireType;
    }

    public Integer getExpireValue() {
        return expireValue;
    }

    public void setExpireValue(Integer expireValue) {
        this.expireValue = expireValue;
    }

    public Boolean getCarryForward() {
        return carryForward;
    }

    public void setCarryForward(Boolean carryForward) {
        this.carryForward = carryForward;
    }

    public Double getMaxCarryForward() {
        return maxCarryForward;
    }

    public void setMaxCarryForward(Double maxCarryForward) {
        this.maxCarryForward = maxCarryForward;
    }

    public Integer getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(Integer policyVersion) {
        this.policyVersion = policyVersion;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getGrantScopeType() {
        return grantScopeType;
    }

    public void setGrantScopeType(String grantScopeType) {
        this.grantScopeType = grantScopeType;
    }

    public List<String> getGrantScopeList() {
        return grantScopeList;
    }

    public void setGrantScopeList(List<String> grantScopeList) {
        this.grantScopeList = grantScopeList;
    }

    public String getGrantTypeValue() {
        return grantTypeValue;
    }

    public void setGrantTypeValue(String grantTypeValue) {
        this.grantTypeValue = grantTypeValue;
    }

    public String getGrantNextExecuteTime() {
        return grantNextExecuteTime;
    }

    public void setGrantNextExecuteTime(String grantNextExecuteTime) {
        this.grantNextExecuteTime = grantNextExecuteTime;
    }

    public AttendanceV2LeavePolicyGrantAmountTypeProperties getGrantAmountType() {
        return grantAmountType;
    }

    public void setGrantAmountType(AttendanceV2LeavePolicyGrantAmountTypeProperties grantAmountType) {
        this.grantAmountType = grantAmountType;
    } 

    
    
}
