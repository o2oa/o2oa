package com.x.program.center.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fancyLou on 2020-06-15.
 * Copyright © 2020 O2. All rights reserved.
 */


@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AppPackApkFile.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AppPackApkFile.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.structure)
public class AppPackApkFile extends StorageObject {


    private static final long serialVersionUID = 492931147504877023L;
    private static final String TABLE = PersistenceProperties.AppPackApkFile.table;

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

    /* 以上为 JpaObject 默认字段 */

    public void onPersist() throws Exception {

    }

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("文件名称.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
    @CheckPersist(allowEmpty = false, fileNameString = true)
    private String name;

    public static final String storage_FIELDNAME = "storage";
    @FieldDescribe("存储器的名称,也就是多个存放节点的名字.")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
    @CheckPersist(allowEmpty = false, simplyString = true)
    @Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
    private String storage;

    public static final String extension_FIELDNAME = "extension";
    @FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
    @CheckPersist(allowEmpty = false, fileNameString = true)
    private String extension;

    public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
    @FieldDescribe("最后更新时间")
    @Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date lastUpdateTime;


    public static final String deepPath_FIELDNAME = "deepPath";
    @FieldDescribe("是否使用更深的路径.")
    @CheckPersist(allowEmpty = true)
    @Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + deepPath_FIELDNAME)
    private Boolean deepPath;

    public static final String length_FIELDNAME = "length";
    @FieldDescribe("文件大小.")
    @Column(name = ColumnNamePrefix + length_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Long length;


    @Override
    public String path() throws Exception {
        if (null == this.packInfoId) {
            throw new Exception("packInfoId can not be null.");
        }
        String str = this.packInfoId;
        str += PATHSEPARATOR;
        str += DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
        str += PATHSEPARATOR;
        str += this.id;
        str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
        return str;
    }

    public static final String packInfoId_FIELDNAME = "packInfoId";
    @FieldDescribe("打包信息id， 关联的会话.")
    @Column(length = length_64B, name = ColumnNamePrefix + packInfoId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String packInfoId;

    public static final String appVersionName_FIELDNAME = "appVersionName";
    @FieldDescribe("安装包版本号名称，暂时没有先预留.")
    @Column(length = length_64B, name = ColumnNamePrefix + appVersionName_FIELDNAME)
    private String appVersionName;

    public static final String appVersionNo_FIELDNAME = "appVersionNo";
    @FieldDescribe("安装包版本号，可比较，暂时没有先预留.")
    @Column(length = length_64B, name = ColumnNamePrefix + appVersionNo_FIELDNAME)
    private String appVersionNo;


    public static final Integer statusInit = 0;
    public static final Integer statusCompleted = 1;
    public static final Integer statusError = 2;

    public static final String status_FIELDNAME = "status";
    @FieldDescribe("状态，异步下载文件所以需要这个状态，0开启，1下载完成， 2过程有异常")
    @Column(name = ColumnNamePrefix + status_FIELDNAME)
    private Integer status;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPackInfoId() {
        return packInfoId;
    }

    public void setPackInfoId(String packInfoId) {
        this.packInfoId = packInfoId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getStorage() {
        return storage;
    }

    @Override
    public void setStorage(String storage) {
        this.storage = storage;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public Boolean getDeepPath() {
        return deepPath;
    }

    @Override
    public void setDeepPath(Boolean deepPath) {
        this.deepPath = deepPath;
    }


    @Override
    public Long getLength() {
        return length;
    }

    @Override
    public void setLength(Long length) {
        this.length = length;
    }


    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionNo() {
        return appVersionNo;
    }

    public void setAppVersionNo(String appVersionNo) {
        this.appVersionNo = appVersionNo;
    }
}
