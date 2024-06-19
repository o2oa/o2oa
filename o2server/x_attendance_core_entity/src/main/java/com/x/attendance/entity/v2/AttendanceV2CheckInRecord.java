package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * 打卡考勤记录
 * Created by fancyLou on 2023/2/20.
 * Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2CheckInRecord", description = "打卡考勤记录.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2CheckInRecord.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2CheckInRecord.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2CheckInRecord extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2CheckInRecord.table;
    private static final long serialVersionUID = -2959741414964993090L;


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
    /*
     * =============================================================================
     * ===== 以上为 JpaObject 默认字段
     * =============================================================================
     * =====
     */

    /*
     * =============================================================================
     * ===== 以下为具体不同的业务及数据表字段要求
     * =============================================================================
     * =====
     */


    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("打卡的用户标识DN")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("打卡记录日期字符串: YYYY-MM-dd")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String recordDateString;

    public static final String recordDate_FIELDNAME = "recordDate";
    @FieldDescribe("打卡记录时间: YYYY-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + recordDate_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date recordDate;


    // 班次信息中的打卡时间 对应到每一条需要打卡的
    public static final String preDutyTime_FIELDNAME = "preDutyTime";
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + preDutyTime_FIELDNAME)
    @FieldDescribe("打卡时间")
    private String preDutyTime;
    public static final String preDutyTimeBeforeLimit_FIELDNAME = "preDutyTimeBeforeLimit";
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + preDutyTimeBeforeLimit_FIELDNAME)
    @FieldDescribe("打卡时间前限制打卡时间")
    private String preDutyTimeBeforeLimit;
    public static final String preDutyTimeAfterLimit_FIELDNAME = "preDutyTimeAfterLimit";
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + preDutyTimeAfterLimit_FIELDNAME)
    @FieldDescribe("打卡时间后限制打卡时间")
    private String preDutyTimeAfterLimit;


    // 打卡数据来源
    public static final String SOURCE_TYPE_USER_CHECK = "USER_CHECK"; // 用户打卡
    public static final String SOURCE_TYPE_AUTO_CHECK = "AUTO_CHECK"; // 系统自动打卡
    public static final String SOURCE_TYPE_FAST_CHECK = "FAST_CHECK"; // 极速打卡
    public static final String SOURCE_TYPE_SYSTEM_IMPORT = "SYSTEM_IMPORT"; // 系统倒入
    public static final String sourceType_FIELDNAME = "sourceType";
    @FieldDescribe("打卡数据来源：USER_CHECK|AUTO_CHECK|FAST_CHECK|SYSTEM_IMPORT")
    @Column(length = length_128B, name = ColumnNamePrefix + sourceType_FIELDNAME)
    private String sourceType;

    //时间结果
    //Normal：正常;
    //Early：早退;
    //Late：迟到;
    //SeriousLate：严重迟到；
    //Absenteeism：旷工迟到；
    //NotSigned：未打卡
    //PreCheckIn：预存数据； 为打卡准备
    public static final String CHECKIN_RESULT_NORMAL = "Normal";
    public static final String CHECKIN_RESULT_Early = "Early";
    public static final String CHECKIN_RESULT_Late = "Late";
    public static final String CHECKIN_RESULT_SeriousLate = "SeriousLate";
    public static final String CHECKIN_RESULT_Absenteeism = "Absenteeism";
    public static final String CHECKIN_RESULT_NotSigned = "NotSigned";
    public static final String CHECKIN_RESULT_PreCheckIn = "PreCheckIn";

    public static final String checkInResult_FIELDNAME = "checkInResult";
    @FieldDescribe("打卡结果: Normal|Early|Late|SeriousLate|NotSigned")
    @Column(length = length_32B, name = ColumnNamePrefix + checkInResult_FIELDNAME)
    private String checkInResult;

    //考勤类型 OnDuty：上班 OffDuty：下班
    public static final String OnDuty = "OnDuty";
    public static final String OffDuty = "OffDuty";

    public static final String checkInType_FIELDNAME = "checkInType";
    @FieldDescribe("考勤类型: OnDuty|OffDuty")
    @Column(length = length_16B, name = ColumnNamePrefix + checkInType_FIELDNAME)
    private String checkInType;

    public static final String sourceDevice_FIELDNAME = "sourceDevice";
    @FieldDescribe("来源设备")
    @Column( length = JpaObject.length_128B, name = ColumnNamePrefix + sourceDevice_FIELDNAME )
    private String sourceDevice;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("其他说明备注")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    private String description;

    public static final String recordAddress_FIELDNAME = "recordAddress";
    @FieldDescribe("打卡地点描述")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recordAddress_FIELDNAME)
    private String recordAddress;

    public static final String longitude_FIELDNAME = "longitude";
    @FieldDescribe("经度")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME)
    private String longitude;

    public static final String latitude_FIELDNAME = "latitude";
    @FieldDescribe("纬度")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME)
    private String latitude;


    public static final String signDescription_FIELDNAME = "signDescription";
    @FieldDescribe("外勤打卡说明")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + signDescription_FIELDNAME)
    private String signDescription;

    public static final String fieldWork_FIELDNAME = "fieldWork";
    @FieldDescribe("是否外勤打卡.")
    @Column(name = ColumnNamePrefix + fieldWork_FIELDNAME)
    private Boolean fieldWork;


    public static final String groupId_FIELDNAME = "groupId";
    @FieldDescribe("对应的考勤组id.")
    @Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
    private String groupId;

    public static final String groupName_FIELDNAME = "groupName";
    @FieldDescribe("考勤组名称")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + groupName_FIELDNAME)
    private String groupName ;


    public static final String groupCheckType_FIELDNAME = "groupCheckType";
    @FieldDescribe("考勤组的考勤类型，自由工时的时候打卡处理方式不同，不需要打卡时间")
    @Column(length = length_16B, name = ColumnNamePrefix
            + groupCheckType_FIELDNAME)
    private String groupCheckType;

    public static final String shiftId_FIELDNAME = "shiftId";
    @FieldDescribe("对应的班次id.")
    @Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
    private String shiftId;

    public static final String shiftName_FIELDNAME = "shiftName";
    @FieldDescribe("班次名称")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + shiftName_FIELDNAME)
    private String shiftName;

    public static final String workPlaceIdd_FIELDNAME = "workPlaceId";
    @FieldDescribe("对应的工作场所id.")
    @Column(length = length_64B, name = ColumnNamePrefix + workPlaceIdd_FIELDNAME)
    private String workPlaceId;

    public static final String placeName_FIELDNAME = "placeName";
    @FieldDescribe("场所名称")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + placeName_FIELDNAME)
    private String placeName;


    public static final String offDutyNextDay_FIELDNAME = "offDutyNextDay";
    @FieldDescribe("是否次日")
    @Column(name = ColumnNamePrefix + offDutyNextDay_FIELDNAME)
    private Boolean offDutyNextDay = false; // 下班打卡是否是次日 下班打卡可跨天


    public static final String appealId_FIELDNAME = "appealId";
    @FieldDescribe("考勤申诉信息记录ID(申诉成功后记录).")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + appealId_FIELDNAME)
    private String appealId;



    public static final String leaveDataId_FIELDNAME = "leaveDataId";
    @FieldDescribe("请假数据id，关联请假数据，如果有值表示在请假时间段内.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + leaveDataId_FIELDNAME)
    private String leaveDataId;


    /**
     * 判断当前打卡记录是否为异常数据
     * @param fieldWorkMarkError 外勤是否标记为异常数据
     * @return true 是异常数据
     */
    public boolean checkResultException(boolean fieldWorkMarkError) {
        if (fieldWorkMarkError) {
            return (getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL) && BooleanUtils.isTrue(getFieldWork())
                    || (
                            !getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)
                                    && !getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                                    && StringUtils.isEmpty(getLeaveDataId())));
        } else {
            return !getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)
                    && StringUtils.isEmpty(getLeaveDataId())
                    && !getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL);
        }
    }


    


    public Boolean getOffDutyNextDay() {
        return offDutyNextDay;
    }

    public void setOffDutyNextDay(Boolean offDutyNextDay) {
        this.offDutyNextDay = offDutyNextDay;
    }

    public String getLeaveDataId() {
        return leaveDataId;
    }

    public void setLeaveDataId(String leaveDataId) {
        this.leaveDataId = leaveDataId;
    }

    public String getAppealId() {
        return appealId;
    }

    public void setAppealId(String appealId) {
        this.appealId = appealId;
    }

    public String getPreDutyTime() {
        return preDutyTime;
    }

    public void setPreDutyTime(String preDutyTime) {
        this.preDutyTime = preDutyTime;
    }

    public String getPreDutyTimeBeforeLimit() {
        return preDutyTimeBeforeLimit;
    }

    public void setPreDutyTimeBeforeLimit(String preDutyTimeBeforeLimit) {
        this.preDutyTimeBeforeLimit = preDutyTimeBeforeLimit;
    }

    public String getPreDutyTimeAfterLimit() {
        return preDutyTimeAfterLimit;
    }

    public void setPreDutyTimeAfterLimit(String preDutyTimeAfterLimit) {
        this.preDutyTimeAfterLimit = preDutyTimeAfterLimit;
    }

    public Boolean getFieldWork() {
        return fieldWork;
    }

    public void setFieldWork(Boolean fieldWork) {
        this.fieldWork = fieldWork;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCheckInResult() {
        return checkInResult;
    }

    public void setCheckInResult(String checkInResult) {
        this.checkInResult = checkInResult;
    }

    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public String getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(String sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecordAddress() {
        return recordAddress;
    }

    public void setRecordAddress(String recordAddress) {
        this.recordAddress = recordAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSignDescription() {
        return signDescription;
    }

    public void setSignDescription(String signDescription) {
        this.signDescription = signDescription;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getWorkPlaceId() {
        return workPlaceId;
    }

    public void setWorkPlaceId(String workPlaceId) {
        this.workPlaceId = workPlaceId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getGroupCheckType() {
        return groupCheckType;
    }

    public void setGroupCheckType(String groupCheckType) {
        this.groupCheckType = groupCheckType;
    }
}
