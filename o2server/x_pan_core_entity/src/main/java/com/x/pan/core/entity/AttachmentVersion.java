package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.openjpa.persistence.jdbc.Index;

/**
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttachmentVersion.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AttachmentVersion.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttachmentVersion extends SliceJpaObject {

    private static final long serialVersionUID = 400900776076914626L;

    private static final String TABLE = PersistenceProperties.AttachmentVersion.table;

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

    @Override
    public void onPersist() {
        //do nothing.
    }

    public AttachmentVersion() {

    }

    public AttachmentVersion(String person, Attachment3 attachment, String changes, String diff) {
        this.setPerson(person);
        this.setAttachmentId(attachment.getId());
        this.setExtension(attachment.getExtension());
        this.setName(attachment.getName());
        this.setZoneId(attachment.getZoneId());
        this.setFileVersion(attachment.getFileVersion());
        this.setLength(attachment.getLength());
        this.setOriginFile(attachment.getOriginFile());
        this.setFileChanges(changes);
        this.setFileDiff(diff);
    }

    public AttachmentVersion(String person, String changes, String diff) {
        this.setPerson(person);
        this.setFileChanges(changes);
        this.setFileDiff(diff);
    }

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("所属用户.")
    @Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String person;

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("文件名称.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
    @CheckPersist(allowEmpty = false, fileNameString = true)
    private String name;

    public static final String extension_FIELDNAME = "extension";
    @FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
    @CheckPersist(allowEmpty = false, fileNameString = true)
    private String extension;

    public static final String length_FIELDNAME = "length";
    @FieldDescribe("文件大小.")
    @Column(name = ColumnNamePrefix + length_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Long length;

    public static final String attachmentId_FIELDNAME = "attachmentId";
    @FieldDescribe("归属文件Id.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + attachmentId_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + attachmentId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String attachmentId;

    public static final String zoneId_FIELDNAME = "zoneId";
    @FieldDescribe("共享区ID。")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + zoneId_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + zoneId_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String zoneId;

    public static final String originFile_FIELDNAME = "originFile";
    @FieldDescribe("真实文件id.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + originFile_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + originFile_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String originFile;

    public static final String fileVersion_FIELDNAME = "fileVersion";
    @FieldDescribe("文件版本")
    @Column(name = ColumnNamePrefix + fileVersion_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Integer fileVersion;

    public static final String fileChanges_FIELDNAME = "fileChanges";
    @FieldDescribe("文件操作信息")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_64K, name = ColumnNamePrefix + fileChanges_FIELDNAME)
    private String fileChanges;

    public static final String fileDiff_FIELDNAME = "fileDiff";
    @FieldDescribe("文件变更详细信息")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_10M, name = ColumnNamePrefix + fileDiff_FIELDNAME)
    private String fileDiff;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getOriginFile() {
        return originFile;
    }

    public void setOriginFile(String originFile) {
        this.originFile = originFile;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(Integer fileVersion) {
        this.fileVersion = fileVersion;
    }

    public String getFileChanges() {
        return fileChanges;
    }

    public void setFileChanges(String fileChanges) {
        this.fileChanges = fileChanges;
    }

    public String getFileDiff() {
        return fileDiff;
    }

    public void setFileDiff(String fileDiff) {
        this.fileDiff = fileDiff;
    }
}
