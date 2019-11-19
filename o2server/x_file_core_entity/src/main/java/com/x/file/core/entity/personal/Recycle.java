package com.x.file.core.entity.personal;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.PersistenceProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Personal.Recycle.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Personal.Recycle.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Recycle extends SliceJpaObject {

	private static final long serialVersionUID = 5424940251448214931L;

	private static final String TABLE = PersistenceProperties.Personal.Recycle.table;

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
		/* 如果扩展名为空去掉null */
		this.extension = StringUtils.trimToEmpty(extension);
	}

	/* 更新运行方法 */

	public Recycle() {

	}

	public Recycle(String person, String name, String fileId, String fileType) throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		Date now = new Date();
		this.setCreateTime(now);
		this.person = person;
		this.name = name;
		this.fileId = fileId;
		this.fileType = fileType;
		this.validTime = DateTools.getDateAfterYearAdjust(new Date(), null, null, 30);
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("所属用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("删除的文件或目录名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 同一个用户同一个名称文件不能多次分享 */
	@CitationNotExist(fields = { "name", "id" }, type = Recycle.class, equals = {
			@Equal(property = "person", field = "person") }))
	private String name;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("文件或目录id.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 同一个用户同一个文件或目录不能多次分享 */
	@CitationNotExist(fields = { "fileId", "id" }, type = Recycle.class, equals = {
			@Equal(property = "person", field = "person") }))
	private String fileId;

	public static final String fileType_FIELDNAME = "fileType";
	@FieldDescribe("删除的文件类型:文件(attachment)|目录(folder).")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fileType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fileType;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extension;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String validTime_FIELDNAME = "validTime";
	@FieldDescribe("保存到时间")
	@Column(name = ColumnNamePrefix + validTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + validTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date validTime;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
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

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}