package com.x.jpush.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Created by fancyLou on 9/13/21.
 * Copyright © 2021 O2. All rights reserved.
 */

@Schema(name = "PushDevice", description = "消息推送设备.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.PushDevice.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.PushDevice.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PushDevice extends SliceJpaObject {



    private static final String TABLE = PersistenceProperties.PushDevice.table;
    private static final long serialVersionUID = 2074606124902551261L;


    @Override
    public void onPersist() throws Exception {

    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }


    /*
     * =============================================================================
     * ===== 以下为具体不同的业务及数据表字段要求
     * =============================================================================
     * =====
     */

    private static final String deviceId_FIELDNAME = "deviceId";
    @FieldDescribe("设备id，发送推送消息用的设备唯一编码")
    @Column(name = ColumnNamePrefix + deviceId_FIELDNAME, length = JpaObject.length_255B)
    @Index(name = TABLE + IndexNameMiddle + deviceId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String deviceId;

    //推送通道类型
    public static final String DEVICE_TYPE_IOS = "ios";
    public static final String DEVICE_TYPE_ANDROID = "android";

    private static final String deviceType_FIELDNAME = "deviceType";
    @FieldDescribe("设备类型：ios|android")
    @Column(name = ColumnNamePrefix + deviceType_FIELDNAME, length = JpaObject.length_16B)
    @Index(name = TABLE + IndexNameMiddle + deviceType_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String deviceType;

    //推送通道类型
    public static final String PUSH_TYPE_JPUSH = "jpush"; // 极光推送
    public static final String PUSH_TYPE_HUAWEI = "huawei"; // 华为推送

    private static final String pushType_FIELDNAME = "pushType";
    @FieldDescribe("推送通道类型：jpush|huawei")
    @Column(name = ColumnNamePrefix + pushType_FIELDNAME, length = JpaObject.length_16B)
    @Index(name = TABLE + IndexNameMiddle + pushType_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String pushType;

    private static final String person_FIELDNAME = "person";
    @FieldDescribe("人员标识")
    @Column(name = ColumnNamePrefix + person_FIELDNAME, length = JpaObject.length_255B)
    @Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String person;


    private static final String unique_FIELDNAME = "unique";
    @FieldDescribe("唯一编码，md5(deviceType+deviceId+pushType+person)")
    @Column(name = ColumnNamePrefix + unique_FIELDNAME, length = JpaObject.length_255B)
    @Index(name = TABLE + IndexNameMiddle + unique_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String unique;


    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }
}
