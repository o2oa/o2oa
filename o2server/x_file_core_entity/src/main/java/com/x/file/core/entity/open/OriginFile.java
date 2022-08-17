package com.x.file.core.entity.open;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OriginFile", description = "云文件原始文件.")
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Open.OriginFile.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Open.OriginFile.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.file)
public class OriginFile extends StorageObject {

	private static final long serialVersionUID = 1329079884381130955L;

	private static final String TABLE = PersistenceProperties.Open.OriginFile.table;

	private static final String PERSONAL = "open";

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
		/* 如果扩展名为空去掉null */
		this.extension = StringUtils.trimToEmpty(extension);
	}

	/* 更新运行方法 */

	public OriginFile() {

	}

	public OriginFile(String storage, String name, String person, String fileMd5) throws Exception {
		if (StringUtils.isEmpty(storage)) {
			throw new Exception("storage can not be empty.");
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		this.storage = storage;
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.person = person;
		this.lastUpdatePerson = person;
		this.fileMd5 = fileMd5;
		if (null == this.extension) {
			throw new Exception("extension can not be null.");
		}
		this.type = FileType.getExtType(this.extension);
	}

	@Override
	public String path() throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new IllegalStateException("id can not be empty.");
		}
		if (StringUtils.isEmpty(type)) {
			throw new IllegalStateException("type can not be empty.");
		}
		String str = this.type;
		str += PATHSEPARATOR;
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
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
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
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
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("上传用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
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

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("存储器的名称,也就是多个存放节点的名字.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String fileMd5_FIELDNAME = "fileMd5";
	@FieldDescribe("文件md5值.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + fileMd5_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 所有文件md5不能重复 */
	@CitationNotExist(fields = { "fileMd5", "id" }, type = OriginFile.class))
	@Index(name = TABLE + IndexNameMiddle + fileMd5_FIELDNAME)
	private String fileMd5;

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

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	private Boolean deepPath;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
}