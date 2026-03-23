package com.x.onlyofficefile.core.entity;

import static com.x.base.core.entity.StorageType.custom;
import java.util.Date;
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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.OnlyOfficeFile.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OnlyOfficeFile.table+ JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = custom)
public class OnlyOfficeFile extends StorageObject {

	private static final long serialVersionUID = 1198758411385570095L;
	private static final String TABLE = PersistenceProperties.OnlyOfficeFile.table;
	private static final String CUSTOM_STORAGE = "onlyofficeFile";

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	@Override
	public void onPersist() throws Exception {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	/* 以上为 JpaObject 默认字段 */

	public static final String filename_FIELDNAME = "fileName";
	@FieldDescribe("文件名")
	@Column(length = 500, name = ColumnNamePrefix + filename_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fileName;

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("创建者")
	@Column(length = length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator;

    public static final String FileModel_FIELDNAME = "fileModel";
    @FieldDescribe("文件信息")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = length_64K, name = ColumnNamePrefix + FileModel_FIELDNAME)
	private String fileModel;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("文件状态")
	@Column(length = length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status;

	public static final String key_FIELDNAME = "key";
	@FieldDescribe("文件key")
	@Column(length = length_32B, name = ColumnNamePrefix + key_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String key;

	public static final String fileVersion_FIELDNAME = "fileVersion";
	@FieldDescribe("文件版本")
	@Column(length = length_8B,name = ColumnNamePrefix + fileVersion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileVersion;

	public static final String relevanceId_FIELDNAME = "relevanceId";
	@FieldDescribe("关联文档id")
	@Column(length = JpaObject.length_128B,name = ColumnNamePrefix + relevanceId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + relevanceId_FIELDNAME)
	private String relevanceId;

	public static final String fileToken_FIELDNAME = "fileToken";
	@FieldDescribe("文件Token")
	@Column(length = length_64B, name = ColumnNamePrefix + fileToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileToken;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deepPath_FIELDNAME)
	private Boolean deepPath;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名。")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String extension;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lastUpdateTime;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String docId_FIELDNAME = "docId";
	@FieldDescribe("业务文档ID")
	@Column(length = length_128B, name = ColumnNamePrefix + docId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docId;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("文档分类")
	@Column(length = length_64B, name = ColumnNamePrefix + category_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String category;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getFileModel() {
		return fileModel;
	}

	public void setFileModel(String fileModel) {
		this.fileModel = fileModel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}


	public String getFileToken() {
		return fileToken;
	}

	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

	public String getRelevanceId() {
		return relevanceId;
	}

	public void setRelevanceId(String relevanceId) {
		this.relevanceId = relevanceId;
	}


	@Override
	public String path() throws Exception {
		if (null == this.getCreateTime()) {
			throw new Exception("CreateTime can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = CUSTOM_STORAGE;
		str += PATHSEPARATOR;
		str += DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
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

	@Override
	public String getName() {
		return getFileName();
	}

	@Override
	public void setName(String name) {
		setFileName(name);
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
