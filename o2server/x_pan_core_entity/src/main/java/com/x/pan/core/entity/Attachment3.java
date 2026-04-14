package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.imageio.IIOException;
import javax.persistence.*;
import java.util.Date;

/**
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Attachment3.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Attachment3.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.Attachment3.table + JpaObject.IndexNameMiddle
		+ Attachment3.folder_FIELDNAME, columnNames = { JpaObject.ColumnNamePrefix+Attachment3.folder_FIELDNAME,
				JpaObject.ColumnNamePrefix+Attachment3.name_FIELDNAME, JpaObject.ColumnNamePrefix+Attachment3.status_FIELDNAME}) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Attachment3 extends SliceJpaObject {

	private static final long serialVersionUID = 400900776076914626L;

	private static final String TABLE = PersistenceProperties.Attachment3.table;

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
	public void onPersist() throws Exception {
		this.lastUpdateTime = new Date();
		/* 如果为顶层，那么将目录设置为空 */
		this.folder = StringUtils.trimToEmpty(this.folder);
		/* 如果扩展名为空去掉null */
		this.extension = StringUtils.trimToEmpty(extension);
	}

	public Attachment3() {

	}

	public Attachment3(String name, String person, String folder, String originFile, Long length, String zoneId)
			throws Exception {
		this.lastUpdateTime = new Date();
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.person = person;
		this.lastUpdatePerson = person;
		this.folder = folder;
		this.zoneId = zoneId;
		this.originFile = originFile;
		this.length = length;
		this.status = FileStatusEnum.VALID.getName();
		if (null == this.extension) {
			throw new IIOException("extension can not be null.");
		}
		this.type = FileTypeEnum.getExtType(this.extension);
		this.fileVersion = 1;
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
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long length;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("文件所属分类.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String folder_FIELDNAME = "folder";
	@FieldDescribe("文件所属目录.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + folder_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String folder;

	public static final String zoneId_FIELDNAME = "zoneId";
	@FieldDescribe("共享区ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + zoneId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + zoneId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String zoneId;

	public static final String originFile_FIELDNAME = "originFile";
	@FieldDescribe("真实文件id.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + originFile_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + originFile_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String originFile;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("文件状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = FileStatusEnum.VALID.getName();

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后更新人员")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("文件描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String fileVersion_FIELDNAME = "fileVersion";
	@FieldDescribe("文件版本")
	@Column(name = ColumnNamePrefix + fileVersion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer fileVersion;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
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

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getOriginFile() {
		return originFile;
	}

	public void setOriginFile(String originFile) {
		this.originFile = originFile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public Integer getFileVersion() {
		return fileVersion == null ? 1 : fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
	}
}
