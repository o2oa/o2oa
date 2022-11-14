package com.x.cms.core.entity.message;

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
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DocumentEvent", description = "文档处理事件.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Message.DocumentEvent.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Message.DocumentEvent.TABLE + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentEvent extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.Message.DocumentEvent.TABLE;

    public static final String TYPE_UPDATE = "update";
    public static final String TYPE_CREATE = "create";
    public static final String TYPE_DELETE = "delete";

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

    /* 以上为 JpaObject 默认字段 */

    @Override
    public void onPersist() throws Exception {
        // nothing
    }

    public DocumentEvent() {
        //nothing
    }

    public static DocumentEvent updateEventInstance(String appInfo, String document) {
        DocumentEvent o = new DocumentEvent();
        o.setType(TYPE_UPDATE);
        o.setAppInfo(appInfo);
        o.setDocument(document);
        return o;
    }

    public static DocumentEvent createEventInstance(String appInfo, String document) {
        DocumentEvent o = new DocumentEvent();
        o.setType(TYPE_CREATE);
        o.setAppInfo(appInfo);
        o.setDocument(document);
        return o;
    }

    public static DocumentEvent deleteEventInstance(String appInfo, String document) {
        DocumentEvent o = new DocumentEvent();
        o.setType(TYPE_DELETE);
        o.setAppInfo(appInfo);
        o.setDocument(document);
        return o;
    }

    public static DocumentEvent updateEventInstance(Document document) {
        return updateEventInstance(document.getAppId(), document.getId());
    }

    public static DocumentEvent createEventInstance(Document document) {
        return createEventInstance(document.getAppId(), document.getId());
    }

    public static DocumentEvent deleteEventInstance(Document document) {
        return deleteEventInstance(document.getAppId(), document.getId());
    }

    public static final String DOCUMENT_FIELDNAME = "document";
    @Schema(description = "文档标识.")
    @FieldDescribe("文档标识.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + DOCUMENT_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + DOCUMENT_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String document;

    public static final String TYPE_FIELDNAME = "type";
    @Schema(description = "工作事件类型.")
    @FieldDescribe("工作事件类型.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + TYPE_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + TYPE_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String type;

    public static final String APPINFO_FIELDNAME = "appInfo";
    @Schema(description = "栏目标识.")
    @FieldDescribe("栏目标识.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + APPINFO_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + APPINFO_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String appInfo;

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
