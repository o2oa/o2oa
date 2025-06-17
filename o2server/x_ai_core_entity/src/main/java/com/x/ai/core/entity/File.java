package com.x.ai.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
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

@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.File.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.File.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.custom)
public class File extends StorageObject {

	private static final long serialVersionUID = 9009603537597089732L;

	private static final String TABLE = PersistenceProperties.File.table;

	private static final String BASE_PATH = "o2_ai";

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

	public void onPersist() {
		//
	}

	public File() {
	}

	public File(String storage, String name, String creator) {
		this.storage = storage;
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.creator = creator;
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	public static final String NAME_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + NAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + NAME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("创建者.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("关联文件ID.")
	@Flag
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + fileId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileId;

	public static final String EXTENSION_FIELDNAME = "extension";
	@FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + EXTENSION_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String extension;

	public static final String STORAGE_FIELDNAME = "storage";
	@FieldDescribe("存储器的名称,也就是多个存放节点的名字.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + STORAGE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String storage;

	public static final String LENGTH_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + LENGTH_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String LASTUPDATETIME_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + LASTUPDATETIME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + LASTUPDATETIME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String DEEPPATH_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + DEEPPATH_FIELDNAME)
	private Boolean deepPath;

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
	public String path() {
		String str = BASE_PATH;
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
