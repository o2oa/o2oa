package com.x.attendance.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "AttendanceDetail", description = "考勤钉钉信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceDingtalkDetail.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDingtalkDetail.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDingtalkDetail extends SliceJpaObject  {


    private static final long serialVersionUID = -7370848875783257116L;
    private static final String TABLE = PersistenceProperties.AttendanceDingtalkDetail.table;

    @Override
    public void onPersist() throws Exception {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();
    /*
     * =============================================================================
     * ===== 以上为 JpaObject 默认字段
     * =============================================================================
     * =====
     */

    public static final String ddId_FIELDNAME = "ddId";
    @FieldDescribe("钉钉打卡结果id")
    @Column( name = ColumnNamePrefix + ddId_FIELDNAME )
    private long ddId;

    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("钉钉的用户id")
    @Column( length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME )
    private String userId;

    public static final String o2User_FIELDNAME = "o2User";
    @FieldDescribe("O2OA用户distinguishedName")
    @Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + o2User_FIELDNAME )
    private String o2User;

    public static final String o2Unit_FIELDNAME = "o2Unit";
    @FieldDescribe("O2OA用户所在的组织distinguishedName")
    @Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + o2Unit_FIELDNAME )
    private String o2Unit;

    public static final String baseCheckTime_FIELDNAME = "baseCheckTime";
    @FieldDescribe("基准时间，用于计算迟到和早退")
    @Column( name = ColumnNamePrefix + baseCheckTime_FIELDNAME )
    private long baseCheckTime;

    public static final String userCheckTime_FIELDNAME = "userCheckTime";
    @FieldDescribe("实际打卡时间,  用户打卡时间的毫秒数")
    @Column( name = ColumnNamePrefix + userCheckTime_FIELDNAME )
    private long userCheckTime;

    public static final String userCheckTimeDate_FIELDNAME = "userCheckTimeDate";
    @FieldDescribe("实际打卡时间,  用Date格式存储")
    @Column( name = ColumnNamePrefix + userCheckTimeDate_FIELDNAME )
    private Date userCheckTimeDate;

    public static final String workDate_FIELDNAME = "workDate";
    @FieldDescribe("工作日")
    @Column( name = ColumnNamePrefix + workDate_FIELDNAME )
    private long workDate;

    //时间结果
    //Normal：正常;
    //Early：早退;
    //Late：迟到;
    //SeriousLate：严重迟到；
    //Absenteeism：旷工迟到；
    //NotSigned：未打卡
    public static final String TIMERESULT_NORMAL = "Normal";
    public static final String TIMERESULT_Early = "Early";
    public static final String TIMERESULT_Late = "Late";
    public static final String TIMERESULT_SeriousLate = "SeriousLate";
    public static final String TIMERESULT_Absenteeism = "Absenteeism";
    public static final String TIMERESULT_NotSigned = "NotSigned";

    public static final String timeResult_FIELDNAME = "timeResult";
    @FieldDescribe("时间结果")
    @Column( length = length_32B, name = ColumnNamePrefix + timeResult_FIELDNAME )
    private String timeResult;

    //考勤类型 OnDuty：上班 OffDuty：下班
    public static final String OnDuty = "OnDuty";
    public static final String OffDuty = "OffDuty";

    public static final String checkType_FIELDNAME = "checkType";
    @FieldDescribe("考勤类型")
    @Column( length = length_32B, name = ColumnNamePrefix + checkType_FIELDNAME )
    private String checkType;

    //位置结果
    //Normal：范围内；
    //Outside：范围外；
    //NotSigned：未打卡
    public static final String LOCATIONRESULT_Normal = "Normal";
    public static final String LOCATIONRESULT_Outside = "Outside";
    public static final String LOCATIONRESULT_NotSigned = "NotSigned";

    public static final String locationResult_FIELDNAME = "locationResult";
    @FieldDescribe("位置结果")
    @Column( length = length_32B, name = ColumnNamePrefix + locationResult_FIELDNAME )
    private String locationResult;

    //数据来源
    //ATM：考勤机;
    //BEACON：IBeacon;
    //DING_ATM：钉钉考勤机;
    //USER：用户打卡;
    //BOSS：老板改签;
    //APPROVE：审批系统;
    //SYSTEM：考勤系统;
    //AUTO_CHECK：自动打卡
    public static final String SOURCETYPE_ATM = "ATM";
    public static final String SOURCETYPE_BEACON = "BEACON";
    public static final String SOURCETYPE_DING_ATM = "DING_ATM";
    public static final String SOURCETYPE_USER = "USER";
    public static final String SOURCETYPE_BOSS = "BOSS";
    public static final String SOURCETYPE_APPROVE = "APPROVE";
    public static final String SOURCETYPE_SYSTEM = "SYSTEM";
    public static final String SOURCETYPE_AUTO_CHECK = "AUTO_CHECK";

    public static final String sourceType_FIELDNAME = "sourceType";
    @FieldDescribe("数据来源")
    @Column( length = length_32B, name = ColumnNamePrefix + sourceType_FIELDNAME )
    private String sourceType;

    public static final String groupId_FIELDNAME = "groupId";
    @FieldDescribe("考勤组ID")
    @Column( name = ColumnNamePrefix + groupId_FIELDNAME )
    private long groupId;

    public static final String planId_FIELDNAME = "planId";
    @FieldDescribe("排班ID")
    @Column( name = ColumnNamePrefix + planId_FIELDNAME )
    private long planId;

    public static final String recordId_FIELDNAME = "recordId";
    @FieldDescribe("打卡记录ID")
    @Column( name = ColumnNamePrefix + recordId_FIELDNAME )
    private long recordId;

//    @FieldDescribe("关联的审批id")
//    @Column(name = ColumnNamePrefix + "userId", length = length_255B)
//    private long approveId;
//
//    @FieldDescribe("关联的审批实例id")
//    @Column(name = ColumnNamePrefix + "userId", length = length_255B)
//    private String procInstId;


    public String getO2User() {
        return o2User;
    }

    public void setO2User(String o2User) {
        this.o2User = o2User;
    }

    public String getO2Unit() {
        return o2Unit;
    }

    public void setO2Unit(String o2Unit) {
        this.o2Unit = o2Unit;
    }

    public long getDdId() {
        return ddId;
    }

    public void setDdId(long ddId) {
        this.ddId = ddId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getBaseCheckTime() {
        return baseCheckTime;
    }

    public void setBaseCheckTime(long baseCheckTime) {
        this.baseCheckTime = baseCheckTime;
    }

    public long getUserCheckTime() {
        return userCheckTime;
    }

    public void setUserCheckTime(long userCheckTime) {
        this.userCheckTime = userCheckTime;
    }

    public long getWorkDate() {
        return workDate;
    }

    public void setWorkDate(long workDate) {
        this.workDate = workDate;
    }

    public String getTimeResult() {
        return timeResult;
    }

    public void setTimeResult(String timeResult) {
        this.timeResult = timeResult;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getLocationResult() {
        return locationResult;
    }

    public void setLocationResult(String locationResult) {
        this.locationResult = locationResult;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public Date getUserCheckTimeDate() {
        return userCheckTimeDate;
    }

    public void setUserCheckTimeDate(Date userCheckTimeDate) {
        this.userCheckTimeDate = userCheckTimeDate;
    }
}
