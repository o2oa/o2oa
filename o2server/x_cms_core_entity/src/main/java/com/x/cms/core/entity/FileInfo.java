package com.x.cms.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileInfo", description = "内容管理文件信息.")
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.FileInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.FileInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.cms)
public class FileInfo extends StorageObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.FileInfo.table;

	@Override
	public void onPersist() throws Exception {
		this.extension = StringUtils.trimToEmpty(this.extension);
		this.site = StringUtils.trimToEmpty(this.site);
		if (StringTools.utf8Length(this.getProperties().getName()) > length_255B) {
			this.name = StringTools.utf8FileNameSubString(this.getProperties().getName(), length_255B);
		}
	}

	@PostLoad
	public void postLoad() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.getProperties().getName())) {
			this.name = this.getProperties().getName();
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
		this.getProperties().setName(name);
	}

	@Override
	public String getName() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getName())) {
			return this.properties.getName();
		} else {
			return this.name;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FileInfoProperties getProperties() {
		if (null == this.properties) {
			this.properties = new FileInfoProperties();
		}
		return this.properties;
	}

	public void setProperties(FileInfoProperties properties) {
		this.properties = properties;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lastUpdateTime;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String site_FIELDNAME = "site";
	@FieldDescribe("附件框分类.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + site_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String site;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private FileInfoProperties properties;

	public static final String OBJECTSECURITYCLEARANCE_FIELDNAME = "objectSecurityClearance";
	@FieldDescribe("客体密级标识.")
	@Column(name = ColumnNamePrefix + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer objectSecurityClearance;

	public Integer getObjectSecurityClearance() {
		return objectSecurityClearance;
	}

	public void setObjectSecurityClearance(Integer objectSecurityClearance) {
		this.objectSecurityClearance = objectSecurityClearance;
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
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
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
	public String path() {
		if (StringUtils.isEmpty(this.appId)) {
			throw new IllegalStateException("appId can not be null.");
		}
		if (StringUtils.isEmpty(this.categoryId)) {
			throw new IllegalStateException("categoryId can not be null.");
		}
		if (StringUtils.isEmpty(documentId)) {
			throw new IllegalStateException("documentId can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new IllegalStateException("id can not be empty.");
		}
		String str = DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.appId;
		str += PATHSEPARATOR;
		str += this.categoryId;
		str += PATHSEPARATOR;
		str += this.documentId;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件真实名称")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name;

	public static final String cloudId_FIELDNAME = "cloudId";
	@FieldDescribe("云文件ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + cloudId_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String cloudId;

	public static final String fileName_FIELDNAME = "fileName";
	@FieldDescribe("服务器上编码后的文件名,为了方便辨识带扩展名")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ fileName_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String fileName;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("文件所属应用ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("文件所属分类ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("文件所属文档ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	public static final String fileType_FIELDNAME = "fileType";
	@FieldDescribe("文件类别：云文件（CLOUD） | 附件(ATTACHMENT)")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fileType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileType;

	public static final String fileExtType_FIELDNAME = "fileExtType";
	@FieldDescribe("文件类别：PICTURE | WORD | EXCEL | PPT | ZIP | TXT | OTHER")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + fileExtType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileExtType;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("根据流文件判断的文件类型.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String fileHost_FIELDNAME = "fileHost";
	@FieldDescribe("文件存储主机名")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + fileHost_FIELDNAME)
	private String fileHost;

	public static final String filePath_FIELDNAME = "filePath";
	@FieldDescribe("文件存储路径")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + filePath_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String filePath;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("文件说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String creatorUid_FIELDNAME = "creatorUid";
	@FieldDescribe("创建者UID")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ creatorUid_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUid;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension;

	public static final String seqNumber_FIELDNAME = "seqNumber";
	@FieldDescribe("排序号")
	@Column(name = ColumnNamePrefix + seqNumber_FIELDNAME)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private Integer seqNumber = 1000;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String text_FIELDNAME = "text";
	@FieldDescribe("文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_100M, name = ColumnNamePrefix + text_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String text;

	public static final String readIdentityList_FIELDNAME = "readIdentityList";
	@FieldDescribe("可以访问的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readIdentityList;

	public static final String readUnitList_FIELDNAME = "readUnitList";
	@FieldDescribe("可以访问的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	public static final String editIdentityList_FIELDNAME = "editIdentityList";
	@FieldDescribe("可以修改的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ editIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + editIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editIdentityList;

	public static final String editUnitList_FIELDNAME = "editUnitList";
	@FieldDescribe("可以修改的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ editUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + editUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editUnitList;

	public static final String controllerIdentityList_FIELDNAME = "controllerIdentityList";
	@FieldDescribe("可以管理的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ controllerIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerIdentityList;

	public static final String controllerUnitList_FIELDNAME = "controllerUnitList";
	@FieldDescribe("可以管理的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ controllerUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerUnitList;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	private Boolean deepPath;

	/**
	 * 获取文件所属应用ID
	 *
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * 设置文件所属应用ID
	 *
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 获取分类说明信息
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置分类说明信息
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取分类创建者帐号
	 *
	 * @return
	 */
	public String getCreatorUid() {
		return creatorUid;
	}

	/**
	 * 设置分类创建者帐号
	 *
	 * @param creatorUid
	 */
	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

	/**
	 * 获取文件名称
	 *
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名称
	 *
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取文件所属分类ID
	 *
	 * @return
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * 设置文件所属分类ID
	 *
	 * @param categoryId
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * 获取文件所属应用ID
	 *
	 * @return
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * 设置文件所属应用ID
	 *
	 * @param documentId
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * 获取文件的类别：文件（FILE）|附件(ATTACHMENT)
	 *
	 * @return
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * 设置文件的类别：文件（FILE）|附件(ATTACHMENT)
	 *
	 * @param fileType
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * 获取文件存储的主机名
	 *
	 * @return
	 */
	public String getFileHost() {
		return fileHost;
	}

	/**
	 * 设置文件存储的主机名
	 *
	 * @param fileHost
	 */
	public void setFileHost(String fileHost) {
		this.fileHost = fileHost;
	}

	/**
	 * 获取文件存储的路径
	 *
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 设置文件存储的路径
	 *
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	/**
	 * 附件控件框
	 *
	 * @return
	 */
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getFileExtType() {
		return fileExtType;
	}

	public void setFileExtType(String fileExtType) {
		this.fileExtType = fileExtType;
	}

	public String getCloudId() {
		return cloudId;
	}

	public void setCloudId(String cloudId) {
		this.cloudId = cloudId;
	}

	public Integer getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(Integer seqNumber) {
		this.seqNumber = seqNumber;
	}

	public List<String> getReadIdentityList() {
		return readIdentityList;
	}

	public void setReadIdentityList(List<String> readIdentityList) {
		this.readIdentityList = readIdentityList;
	}

	public List<String> getReadUnitList() {
		return readUnitList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public List<String> getEditIdentityList() {
		return editIdentityList;
	}

	public void setEditIdentityList(List<String> editIdentityList) {
		this.editIdentityList = editIdentityList;
	}

	public List<String> getEditUnitList() {
		return editUnitList;
	}

	public void setEditUnitList(List<String> editUnitList) {
		this.editUnitList = editUnitList;
	}

	public List<String> getControllerIdentityList() {
		return controllerIdentityList;
	}

	public void setControllerIdentityList(List<String> controllerIdentityList) {
		this.controllerIdentityList = controllerIdentityList;
	}

	public List<String> getControllerUnitList() {
		return controllerUnitList;
	}

	public void setControllerUnitList(List<String> controllerUnitList) {
		this.controllerUnitList = controllerUnitList;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
