package com.x.file.core.entity.personal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.SliceJpaObject;
import com.x.file.core.entity.open.FileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.file.core.entity.PersistenceProperties;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Personal.Attachment2.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Personal.Attachment2.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Attachment2 extends SliceJpaObject {

	private static final long serialVersionUID = -1406950046153468143L;

	private static final String TABLE = PersistenceProperties.Personal.Attachment2.table;

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
		this.lastUpdateTime = new Date();
		/* 如果为顶层，那么将目录设置为空 */
		this.folder = StringUtils.trimToEmpty(this.folder);
		/* 如果扩展名为空去掉null */
		this.extension = StringUtils.trimToEmpty(extension);
	}

	/* 更新运行方法 */

	public Attachment2() {

	}

	public Attachment2(String name, String person, String folder, String originFile, Long length, String type) throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.person = person;
		this.lastUpdatePerson = person;
		this.folder = folder;
		this.originFile = originFile;
		this.length = length;
		this.type = type;
		this.status = "正常";
		if (null == this.extension) {
			throw new Exception("extension can not be null.");
		}
		this.type = FileType.getExtType(this.extension);
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
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 同一个用户同一个目录正常状态下不能有重名 */
	@CitationNotExist(fields = { "name", "id" }, type = Attachment2.class, equals = {
			@Equal(property = "person", field = "person"), @Equal(property = "folder", field = "folder"),
			@Equal(property = "status", field = "status") }))
	private String name;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String extension;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("文件所属分类.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String folder_FIELDNAME = "folder";
	@FieldDescribe("文件所属目录.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + folder_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + folder_FIELDNAME)
	/* 上级目录必须存在 */
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Folder2.class, equals = @Equal(property = "person", field = "person")))
	private String folder;

	public static final String originFile_FIELDNAME = "originFile";
	@FieldDescribe("真实文件id.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + originFile_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + originFile_FIELDNAME)
	/* 文件必须存在 */
	@CheckPersist(allowEmpty = false)
	private String originFile;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("文件状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后更新人员")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

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
}