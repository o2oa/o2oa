package com.x.file.core.entity.open;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.PersistenceProperties;

/**
 * 
 * 有两个可能性创建File:<br/>
 * 1.通过servlet上传<br/>
 * 2.通过Attachment转储<br/>
 *
 */
@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Open.File.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Open.File.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.file)
public class File extends StorageObject {

	private static final long serialVersionUID = 9009603537597089732L;

	private static final String TABLE = PersistenceProperties.Open.File.table;

	private static final String OPEN = "open";

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

	public File() {

	}

	public File(String storage, String name, String person, ReferenceType referenceType, String reference)
			throws Exception {
		if (StringUtils.isEmpty(storage)) {
			throw new Exception("storage can not be empty.");
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		if (null == referenceType) {
			throw new Exception("referenceType can not be null.");
		}
		if (StringUtils.isEmpty(reference)) {
			throw new Exception("reference can not be empty.");
		}
		this.storage = storage;
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.person = person;
		this.referenceType = referenceType;
		this.reference = reference;
		if (null == this.extension) {
			throw new Exception("extension can not be null.");
		}
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	@FieldDescribe("名称.")
	@Column(length = PersistenceProperties.file_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false)
	private String name;

	@FieldDescribe("所属用户.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xperson")
	@Index(name = TABLE + "_person")
	@CheckPersist(allowEmpty = false)
	private String person;

	@FieldDescribe("关联类型.")
	@Enumerated(EnumType.STRING)
	@Column(length = ReferenceType.length, name = "xreferenceType")
	@Index(name = TABLE + "_referenceType")
	@CheckPersist(allowEmpty = false)
	private ReferenceType referenceType;

	@FieldDescribe("关联ID.")
	@Column(length = JpaObject.length_255B, name = "xreference")
	@Index(name = TABLE + "_reference")
	@CheckPersist(allowEmpty = false)
	private String reference;

	@FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
	@Column(length = JpaObject.length_64B, name = "xextension")
	@CheckPersist(allowEmpty = false, fileNameString = true)
	@Index(name = TABLE + "_extension")
	private String extension;

	@FieldDescribe("存储器的名称,也就是多个存放节点的名字.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + "_storage")
	private String storage;

	@FieldDescribe("文件大小.")
	@Index(name = TABLE + "_length")
	@Column(name = "xlength")
	@CheckPersist(allowEmpty = true)
	private Long length;

	@FieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@FieldDescribe("最后更新时间")
	@Column(name = "xlastNotExistedTime")
	@Index(name = TABLE + "_lastNotExistedTime")
	@CheckPersist(allowEmpty = true)
	private Date lastNotExistedTime;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deepPath_FIELDNAME)
	private Boolean deepPath;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String path() throws Exception {
		if (null == this.person) {
			throw new Exception("person can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		if (StringUtils.isEmpty(reference)) {
			throw new Exception("reference can not be empty.");
		}
		if (Objects.isNull(this.getCreateTime())) {
			throw new Exception("createTime can not be empty.");
		}
		if (Objects.isNull(this.referenceType)) {
			throw new Exception("referenceType can not be empty.");
		}

		String str = this.person;
		str += PATHSEPARATOR;
		str += OPEN;
		str += PATHSEPARATOR;
		str += referenceType.toString();
		str += PATHSEPARATOR;
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += reference;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastNotExistedTime() {
		return lastNotExistedTime;
	}

	public void setLastNotExistedTime(Date lastNotExistedTime) {
		this.lastNotExistedTime = lastNotExistedTime;
	}

}