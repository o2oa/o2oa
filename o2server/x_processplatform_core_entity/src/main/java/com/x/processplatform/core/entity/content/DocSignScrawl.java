package com.x.processplatform.core.entity.content;

import static com.x.base.core.entity.StorageType.processPlatform;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 签批涂鸦信息
 * 
 * @author sword
 */
@Schema(name = "DocSignScrawl", description = "流程平台批注.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.DocSignScrawl.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.DocSignScrawl.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = processPlatform)
public class DocSignScrawl extends StorageObject {

	private static final long serialVersionUID = 3335030367719974674L;

	private static final String TABLE = PersistenceProperties.Content.DocSignScrawl.table;

	public static final String SCRAWL_TYPE_PLACEHOLDER = "placeholder";

	public static final String SCRAWL_TYPE_BASE64 = "base64";

	public static final String SCRAWL_TYPE_IMAGE = "image";

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
		if (StringUtils.isNotBlank(this.name)) {
			this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		}
	}

	public DocSignScrawl() {
		// nothing
	}

	public DocSignScrawl(DocSign docSign, String name) {
		this.signId = docSign.getId();
		this.job = docSign.getJob();
		this.activity = docSign.getActivity();
		this.activityName = docSign.getActivityName();
		this.person = docSign.getPerson();
		this.type = SCRAWL_TYPE_PLACEHOLDER;
		this.name = name;
		this.setCreateTime(new Date());
	}

	@Override
	public String path() {
		String str = DateTools.format(this.getCreateTime(), DateTools.formatCompact_yyyyMMdd);
		str += PATHSEPARATOR;
		str += this.job;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件名称,带扩展名的文件名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String name;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名。")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String extension;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lastUpdateTime;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	private Boolean deepPath;

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

	public static final String signId_FIELDNAME = "signId";
	@FieldDescribe("签批ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + signId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + signId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String signId;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("任务.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("活动ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("活动名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("签批人")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String commitTime_FIELDNAME = "commitTime";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("提交时间.")
	@Column(name = ColumnNamePrefix + commitTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commitTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date commitTime;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("涂鸦类型：placeholder(占位符)|base64(图片base64存储在data字段中)|image(图片存储在附件中)")
	@Column(length = length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String width_FIELDNAME = "width";
	@FieldDescribe("宽")
	@Column(length = length_64B, name = ColumnNamePrefix + width_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String width;

	public static final String height_FIELDNAME = "height";
	@FieldDescribe("高")
	@Column(length = length_64B, name = ColumnNamePrefix + height_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String height;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Date getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
}
